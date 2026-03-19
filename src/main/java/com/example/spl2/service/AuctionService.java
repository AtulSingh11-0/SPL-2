package com.example.spl2.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spl2.dto.PlayerDTO;
import com.example.spl2.entity.Auction;
import com.example.spl2.entity.AuctionState;
import com.example.spl2.entity.RegisteredPlayer;
import com.example.spl2.entity.Sold;
import com.example.spl2.entity.Team;
import com.example.spl2.entity.Unsold;
import com.example.spl2.repository.AuctionRepository;
import com.example.spl2.repository.AuctionStateRepository;
import com.example.spl2.repository.RegisteredPlayerRepository;
import com.example.spl2.repository.SoldRepository;
import com.example.spl2.repository.TeamRepository;
import com.example.spl2.repository.UnsoldRepository;

@Service
public class AuctionService {

    @Autowired
    private RegisteredPlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private AuctionStateRepository auctionStateRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private SoldRepository soldRepository;

    @Autowired
    private UnsoldRepository unsoldRepository;

    private final List<String> AUCTION_CATEGORIES = Arrays.asList(
            "All-rounder Bowling",
            "All-rounder Batting",
            "Only Batsman",
            "Only Bowler",
            "Wicket Keeper");

    @Transactional
    public int initializeAuction() {
        auctionRepository.deleteAll();

        List<RegisteredPlayer> candidates = playerRepository.findByStatus("REGISTERED");

        List<RegisteredPlayer> nonRetained = candidates.stream()
                .filter(p -> p.getIsRetained() == null || !p.getIsRetained())
                .collect(Collectors.toList());

        List<RegisteredPlayer> ordered = new ArrayList<>(nonRetained);
        Collections.shuffle(ordered);

        int idx = 1;
        int created = 0;
        for (RegisteredPlayer p : ordered) {
            Auction entry = Auction.builder()
                    .player(p)
                    .orderIndex(idx++)
                    .category(p.getRole())
                    .build();
            auctionRepository.save(entry);
            created++;
        }

        auctionStateRepository.deleteAll();
        AuctionState state = AuctionState.builder()
                .currentCategory(AUCTION_CATEGORIES.get(0))
                .categoryIndex(0)
                .auctionActive(true)
                .categoryStartTime(LocalDateTime.now())
                .build();
        auctionStateRepository.save(state);
        return created;
    }

    @Transactional
    public PlayerDTO getNextPlayer() {
        List<Auction> entries = auctionRepository.findAllByOrderByOrderIndexAsc();
        if (entries.isEmpty()) {
            if (!unsoldRepository.findAll().isEmpty()) {
                requeueUnsoldToAuction();
                entries = auctionRepository.findAllByOrderByOrderIndexAsc();
            }
        }

        // If still empty after checking unsold, then auction is truly finished
        if (entries.isEmpty()) {
            return null;
        }

        Auction next = entries.get(0);
        RegisteredPlayer player = next.getPlayer();

        return playerService.convertPlayerToDTO(player);
    }

    @Transactional
    public void sellPlayer(Long playerId, Long teamId, Double soldPrice) {
        if (!auctionRepository.existsByPlayerId(playerId)) {
            throw new RuntimeException("Player is not in the active auction pool");
        }

        RegisteredPlayer player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        try {
            if (team.getRemainingBudget() < soldPrice) {
                throw new RuntimeException("Insufficient budget");
            }

            team.setRemainingBudget(team.getRemainingBudget() - soldPrice);
            teamRepository.save(team);

            player.setStatus("SOLD");
            playerRepository.save(player);

            Sold sold = Sold.builder()
                    .player(player)
                    .team(team)
                    .soldPrice(soldPrice)
                    .build();
            soldRepository.save(sold);

            auctionRepository.deleteByPlayerId(playerId);

        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update conflict, please retry");
        }
    }

    @Transactional
    public void markPlayerUnsold(Long playerId) {
        if (!auctionRepository.existsByPlayerId(playerId)) {
            throw new RuntimeException("Player is not in the active auction pool");
        }

        RegisteredPlayer player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        player.setStatus("UNSOLD");
        playerRepository.save(player);

        Unsold unsold = Unsold.builder().player(player).requeueCount(0).build();
        unsoldRepository.save(unsold);

        auctionRepository.deleteByPlayerId(playerId);
    }

    @Transactional
    public void releasePlayer(Long teamId, Long playerId, Double releasePrice) {
        RegisteredPlayer player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + playerId));

        Sold soldRecord = soldRepository.findByPlayerId(playerId)
                .orElseThrow(() -> new RuntimeException("Player is not in the Sold table"));

        if (!soldRecord.getTeam().getId().equals(teamId)) {
            throw new RuntimeException("Player does not belong to this team");
        }

        Team team = soldRecord.getTeam();

        try {
            team.setRemainingBudget(team.getRemainingBudget() + releasePrice);
            teamRepository.save(team);

            soldRepository.delete(soldRecord);

            player.setStatus("UNSOLD");
            playerRepository.save(player);

            Unsold unsold = Unsold.builder().player(player).requeueCount(0).build();
            unsoldRepository.save(unsold);

        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update conflict, please retry");
        }
    }

    @Transactional
    protected void requeueUnsoldToAuction() {
        List<Unsold> unsoldList = unsoldRepository.findAll();
        if (unsoldList.isEmpty())
            return;

        List<RegisteredPlayer> playersToRequeue = unsoldList.stream().map(Unsold::getPlayer)
                .collect(Collectors.toList());

        List<RegisteredPlayer> ordered = new ArrayList<>(playersToRequeue);
        Collections.shuffle(ordered);

        int startIdx = 1;
        List<Auction> existing = auctionRepository.findAllByOrderByOrderIndexAsc();
        if (!existing.isEmpty()) {
            startIdx = existing.get(existing.size() - 1).getOrderIndex() + 1;
        }

        for (RegisteredPlayer p : ordered) {
            Auction entry = Auction.builder()
                    .player(p)
                    .orderIndex(startIdx++)
                    .category(p.getRole())
                    .build();
            auctionRepository.save(entry);
        }

        unsoldRepository.deleteAll();
    }

    public AuctionState getAuctionStatus() {
        return getOrCreateAuctionState();
    }

    public List<String> getAuctionCategories() {
        return AUCTION_CATEGORIES;
    }

    public List<Map<String, Object>> getAuctionEntries() {
        List<Auction> entries = auctionRepository.findAllByOrderByOrderIndexAsc();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Auction e : entries) {
            Map<String, Object> m = new HashMap<>();
            m.put("orderIndex", e.getOrderIndex());
            m.put("category", e.getCategory());
            m.put("player", playerService.convertPlayerToDTO(e.getPlayer()));
            result.add(m);
        }
        return result;
    }

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
            requeueUnsoldToAuction();
            return getNextPlayer();
        }
    }

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
