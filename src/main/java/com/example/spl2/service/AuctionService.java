package com.example.spl2.service;

import com.example.spl2.dto.PlayerDTO;
import com.example.spl2.entity.AuctionEntry;
import com.example.spl2.entity.AuctionState;
import com.example.spl2.entity.Player;
import com.example.spl2.entity.Sold;
import com.example.spl2.entity.Team;
import com.example.spl2.entity.Unsold;
import com.example.spl2.repository.AuctionEntryRepository;
import com.example.spl2.repository.AuctionStateRepository;
import com.example.spl2.repository.PlayerRepository;
import com.example.spl2.repository.SoldRepository;
import com.example.spl2.repository.TeamRepository;
import com.example.spl2.repository.UnsoldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuctionService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private AuctionStateRepository auctionStateRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private AuctionEntryRepository auctionEntryRepository;

    @Autowired
    private SoldRepository soldRepository;

    @Autowired
    private UnsoldRepository unsoldRepository;

    // Define auction categories in order
    private final List<String> AUCTION_CATEGORIES = Arrays.asList(
            "All-rounder Bowling",
            "All-rounder Batting",
            "Only Batsman",
            "Only Bowler",
            "Wicket Keeper"
    );

    // Role priority order (highest to lowest)
    private final List<String> ROLE_PRIORITY = Arrays.asList(
            "Batting All Rounder",
            "Wicketkeeper",
            "Batter",
            "Bowling All Rounder",
            "All Rounder",
            "Bowler"
    );

    @Transactional
    public void initializeAuction() {
        // Clear existing auction entries
        auctionEntryRepository.deleteAll();

        // Take all non-retained REGISTERED players
        List<Player> candidates = playerRepository.findByStatus("REGISTERED");

        // Filter out retained players (if you have retention flags on Player, check them; here we assume status != RETAINED)
        List<Player> nonRetained = candidates.stream()
                .filter(p -> !"RETAINED".equalsIgnoreCase(p.getStatus()))
                .collect(Collectors.toList());

        // Group by role according to ROLE_PRIORITY
        List<Player> ordered = new ArrayList<>();
        for (String role : ROLE_PRIORITY) {
            List<Player> group = nonRetained.stream()
                    .filter(p -> role.equalsIgnoreCase(p.getRole()))
                    .collect(Collectors.toList());
            // shuffle within group
            Collections.shuffle(group);
            ordered.addAll(group);
        }

        // For any players whose role didn't match exactly, append them at end shuffled
        List<Player> remainder = nonRetained.stream()
                .filter(p -> !ROLE_PRIORITY.contains(p.getRole()))
                .collect(Collectors.toList());
        Collections.shuffle(remainder);
        ordered.addAll(remainder);

        // Persist auction entries with sequential orderIndex
        int idx = 1;
        for (Player p : ordered) {
            AuctionEntry entry = AuctionEntry.builder()
                    .player(p)
                    .orderIndex(idx++)
                    .category(p.getRole())
                    .build();
            auctionEntryRepository.save(entry);
        }

        // Initialize auction state
        auctionStateRepository.deleteAll();
        AuctionState state = AuctionState.builder()
                .currentCategory(ROLE_PRIORITY.get(0))
                .categoryIndex(0)
                .auctionActive(false)
                .categoryStartTime(LocalDateTime.now())
                .build();
        auctionStateRepository.save(state);
    }

    @Transactional
    public PlayerDTO getNextPlayer() {
        List<AuctionEntry> entries = auctionEntryRepository.findAllByOrderByOrderIndexAsc();
        if (entries.isEmpty()) {
            // When empty, check unsold and requeue if necessary
            if (!unsoldRepository.findAll().isEmpty()) {
                requeueUnsoldToAuction();
                entries = auctionEntryRepository.findAllByOrderByOrderIndexAsc();
                if (entries.isEmpty()) return null;
            } else {
                return null;
            }
        }

        AuctionEntry next = entries.get(0);
        Player player = next.getPlayer();

        // Keep entry in DB until sold or unsold action; but return the DTO for frontend display
        return playerService.convertPlayerToDTO(player);
    }

    @Transactional
    public void sellPlayer(Long playerId, Long teamId, Double soldPrice) {
        // Validate auction entry exists for player
        if (!auctionEntryRepository.existsByPlayerId(playerId)) {
            throw new RuntimeException("Player is not in the active auction pool");
        }

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Use optimistic locking on Team: fetch and update within transaction
        try {
            if (team.getRemainingBudget() < soldPrice) {
                throw new RuntimeException("Insufficient budget");
            }

            // Deduct budget
            team.setRemainingBudget(team.getRemainingBudget() - soldPrice);
            teamRepository.save(team);

            // Mark player sold
            player.setTeam(team);
            player.setStatus("SOLD");
            player.setSoldPrice(soldPrice);
            playerRepository.save(player);

            // Insert into Sold table
            Sold sold = Sold.builder()
                    .player(player)
                    .team(team)
                    .soldPrice(soldPrice)
                    .build();
            soldRepository.save(sold);

            // Remove from auction entries
            auctionEntryRepository.deleteByPlayerId(playerId);

        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update conflict, please retry");
        }
    }

    @Transactional
    public void markPlayerUnsold(Long playerId) {
        if (!auctionEntryRepository.existsByPlayerId(playerId)) {
            throw new RuntimeException("Player is not in the active auction pool");
        }

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        // Mark as unsold in player table
        player.setStatus("UNSOLD");
        player.setTeam(null);
        player.setSoldPrice(null);
        playerRepository.save(player);

        // Add to Unsold table
        Unsold unsold = Unsold.builder().player(player).requeueCount(0).build();
        unsoldRepository.save(unsold);

        // Remove from auction pool
        auctionEntryRepository.deleteByPlayerId(playerId);
    }

    @Transactional
    protected void requeueUnsoldToAuction() {
        List<Unsold> unsoldList = unsoldRepository.findAll();
        if (unsoldList.isEmpty()) return;

        // Collect players and build new ordered list by ROLE_PRIORITY and shuffle within role
        List<Player> playersToRequeue = unsoldList.stream().map(Unsold::getPlayer).collect(Collectors.toList());

        List<Player> ordered = new ArrayList<>();
        for (String role : ROLE_PRIORITY) {
            List<Player> group = playersToRequeue.stream()
                    .filter(p -> role.equalsIgnoreCase(p.getRole()))
                    .collect(Collectors.toList());
            Collections.shuffle(group);
            ordered.addAll(group);
        }
        // remainder
        List<Player> remainder = playersToRequeue.stream().filter(p -> !ROLE_PRIORITY.contains(p.getRole())).collect(Collectors.toList());
        Collections.shuffle(remainder);
        ordered.addAll(remainder);

        int startIdx = 1;
        List<AuctionEntry> existing = auctionEntryRepository.findAllByOrderByOrderIndexAsc();
        if (!existing.isEmpty()) {
            startIdx = existing.get(existing.size() - 1).getOrderIndex() + 1;
        }

        for (Player p : ordered) {
            AuctionEntry entry = AuctionEntry.builder()
                    .player(p)
                    .orderIndex(startIdx++)
                    .category(p.getRole())
                    .build();
            auctionEntryRepository.save(entry);
        }

        // Clear unsold
        unsoldRepository.deleteAll();
    }

    // Return the current auction state
    public AuctionState getAuctionStatus() {
        return getOrCreateAuctionState();
    }

    // Return the configured auction categories
    public List<String> getAuctionCategories() {
        return AUCTION_CATEGORIES;
    }

    // Move to next category and return next player
    @Transactional
    public PlayerDTO moveToNextCategory() {
        AuctionState state = getOrCreateAuctionState();
        int nextIndex = state.getCategoryIndex() + 1;
        if (nextIndex < AUCTION_CATEGORIES.size()) {
            state.setCategoryIndex(nextIndex);
            state.setCurrentCategory(AUCTION_CATEGORIES.get(nextIndex));
            state.setAuctionActive(false);
            state.setCategoryStartTime(LocalDateTime.now());
            auctionStateRepository.save(state);
            return getNextPlayer();
        } else {
            // All categories exhausted; try to requeue unsold
            requeueUnsoldToAuction();
            // After requeue, try to get next player
            return getNextPlayer();
        }
    }

    // Helper: create or fetch the single auction state
    private AuctionState getOrCreateAuctionState() {
        Optional<AuctionState> optional = auctionStateRepository.findFirstByOrderByIdDesc();
        if (optional.isPresent()) {
            return optional.get();
        }
        AuctionState s = AuctionState.builder()
                .currentCategory(AUCTION_CATEGORIES.get(0))
                .categoryIndex(0)
                .auctionActive(false)
                .categoryStartTime(LocalDateTime.now())
                .build();
        auctionStateRepository.save(s);
        return auctionStateRepository.findFirstByOrderByIdDesc().get();
    }

}
