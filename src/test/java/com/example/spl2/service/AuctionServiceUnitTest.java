package com.example.spl2.service;

import com.example.spl2.dto.PlayerDTO;
import com.example.spl2.entity.AuctionEntry;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuctionServiceUnitTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private AuctionStateRepository auctionStateRepository;

    @Mock
    private AuctionEntryRepository auctionEntryRepository;

    @Mock
    private SoldRepository soldRepository;

    @Mock
    private UnsoldRepository unsoldRepository;

    @Mock
    private PlayerService playerService;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private AuctionService auctionService;

    private Player p1, p2, p3;
    private Team team;

    @BeforeEach
    void setUp() {
        p1 = Player.builder().id(1L).playerName("Player One").age(25).role("Batting All Rounder").status("REGISTERED").basePrice(1000.0).build();
        p2 = Player.builder().id(2L).playerName("Player Two").age(27).role("Wicketkeeper").status("REGISTERED").basePrice(800.0).build();
        p3 = Player.builder().id(3L).playerName("Player Three").age(22).role("Bowler").status("REGISTERED").basePrice(500.0).build();

        team = Team.builder().id(1L).teamName("TeamA").captain("Captain").totalBudget(10000.0).remainingBudget(5000.0).build();
    }

    @Test
    void testInitializeAuction_createsEntries_andInitializesState() {
        when(playerRepository.findByStatus("REGISTERED")).thenReturn(Arrays.asList(p1, p2, p3));

        auctionService.initializeAuction();

        // Expect auction entries saved for each player
        verify(auctionEntryRepository, times(3)).save(any(AuctionEntry.class));
        // Expect auction state saved
        verify(auctionStateRepository, times(1)).save(any());
    }

    @Test
    void testGetNextPlayer_requeuesFromUnsoldIfEmpty() {
        // No auction entries initially, then after requeue return one entry
        when(auctionEntryRepository.findAllByOrderByOrderIndexAsc())
                .thenReturn(List.of(), List.of(AuctionEntry.builder().player(p1).orderIndex(1).category(p1.getRole()).build()));
        // Unsold contains one player
        Unsold u = Unsold.builder().id(1L).player(p1).requeueCount(0).build();
        when(unsoldRepository.findAll()).thenReturn(Arrays.asList(u));

        // After requeue, auctionEntryRepository.findAllByOrderByOrderIndexAsc will return entries (mocked above)
        PlayerDTO dto = auctionService.getNextPlayer();
        // Since playerService.convertPlayerToDTO is mocked, it returns null by default; ensure method attempted requeue
        verify(unsoldRepository, atLeastOnce()).findAll();
    }

    @Test
    void testSellPlayer_success_deductsBudget_andCreatesSold() {
        when(auctionEntryRepository.existsByPlayerId(1L)).thenReturn(true);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        auctionService.sellPlayer(1L, 1L, 1000.0);

        // Verify deductions and saves
        ArgumentCaptor<Team> teamCaptor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(teamCaptor.capture());
        Team savedTeam = teamCaptor.getValue();
        assertEquals(4000.0, savedTeam.getRemainingBudget());

        verify(playerRepository).save(p1);
        verify(soldRepository).save(any(Sold.class));
        verify(auctionEntryRepository).deleteByPlayerId(1L);
    }

    @Test
    void testSellPlayer_insufficientBudget_throws() {
        when(auctionEntryRepository.existsByPlayerId(2L)).thenReturn(true);
        when(playerRepository.findById(2L)).thenReturn(Optional.of(p2));
        Team poor = Team.builder().id(2L).teamName("Poor").captain("Cap").totalBudget(1000.0).remainingBudget(100.0).build();
        when(teamRepository.findById(2L)).thenReturn(Optional.of(poor));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> auctionService.sellPlayer(2L, 2L, 500.0));
        assertTrue(ex.getMessage().toLowerCase().contains("insufficient"));

        verify(soldRepository, never()).save(any());
    }

    @Test
    void testMarkPlayerUnsold_movesPlayerToUnsold_andDeletesAuctionEntry() {
        when(auctionEntryRepository.existsByPlayerId(3L)).thenReturn(true);
        when(playerRepository.findById(3L)).thenReturn(Optional.of(p3));

        auctionService.markPlayerUnsold(3L);

        verify(playerRepository).save(p3);
        verify(unsoldRepository).save(any(Unsold.class));
        verify(auctionEntryRepository).deleteByPlayerId(3L);
    }
}
