package com.example.spl2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spl2.dto.TeamDTO;
import com.example.spl2.entity.Sold;
import com.example.spl2.entity.Team;
import com.example.spl2.exception.TeamAlreadyExistsException;
import com.example.spl2.exception.TeamNotFoundException;
import com.example.spl2.repository.AuctionRepository;
import com.example.spl2.repository.RegisteredPlayerRepository;
import com.example.spl2.repository.SoldRepository;
import com.example.spl2.repository.TeamRepository;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private RegisteredPlayerRepository registeredPlayerRepository;

    @Autowired
    private SoldRepository soldRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Transactional
    public TeamDTO createTeam(TeamDTO teamDTO) {
        if (teamRepository.existsByTeamName(teamDTO.getTeamName())) {
            throw new TeamAlreadyExistsException("Team with name '" + teamDTO.getTeamName() + "' already exists");
        }

        Double ret1Money = teamDTO.getPlayerRetention1Money();
        if (teamDTO.getPlayerRetention1() != null && !teamDTO.getPlayerRetention1().isEmpty()) {
            if (ret1Money == null) {
                ret1Money = 6000.0;
            }
        }
        Double ret2Money = teamDTO.getPlayerRetention2Money();
        if (teamDTO.getPlayerRetention2() != null && !teamDTO.getPlayerRetention2().isEmpty()) {
            if (ret2Money == null) {
                ret2Money = 4000.0;
            }
        }

        Team team = Team.builder()
                .teamName(teamDTO.getTeamName())
                .captain(teamDTO.getCaptain())
                .totalBudget(teamDTO.getTotalBudget())
                .playerRetention1(teamDTO.getPlayerRetention1())
                .playerRetention2(teamDTO.getPlayerRetention2())
                .playerRetention1Money(ret1Money)
                .playerRetention2Money(ret2Money)
                .build();

        Double remainingBudget = teamDTO.getTotalBudget();
        if (ret1Money != null) {
            remainingBudget -= ret1Money;
        }
        if (ret2Money != null) {
            remainingBudget -= ret2Money;
        }
        team.setRemainingBudget(remainingBudget);

        Team savedTeam = teamRepository.save(team);

        // Process Captain
        if (teamDTO.getCaptain() != null && !teamDTO.getCaptain().isEmpty()) {
            registeredPlayerRepository.findByPlayerName(teamDTO.getCaptain()).ifPresent(p -> {
                p.setStatus("SOLD");
                p.setIsRetained(true);
                registeredPlayerRepository.save(p);
                Sold sold = Sold.builder().player(p).team(savedTeam).soldPrice(0.0).build();
                soldRepository.save(sold);
                auctionRepository.deleteByPlayerId(p.getId());
            });
        }

        // Process Retention 1
        final Double finalRet1Money = ret1Money;
        if (teamDTO.getPlayerRetention1() != null && !teamDTO.getPlayerRetention1().isEmpty()) {
            registeredPlayerRepository.findByPlayerName(teamDTO.getPlayerRetention1()).ifPresent(p -> {
                p.setStatus("SOLD");
                p.setIsRetained(true);
                registeredPlayerRepository.save(p);
                Double price = finalRet1Money != null ? finalRet1Money : 0.0;
                Sold sold = Sold.builder().player(p).team(savedTeam).soldPrice(price).build();
                soldRepository.save(sold);
                auctionRepository.deleteByPlayerId(p.getId());
            });
        }

        // Process Retention 2
        final Double finalRet2Money = ret2Money;
        if (teamDTO.getPlayerRetention2() != null && !teamDTO.getPlayerRetention2().isEmpty()) {
            registeredPlayerRepository.findByPlayerName(teamDTO.getPlayerRetention2()).ifPresent(p -> {
                p.setStatus("SOLD");
                p.setIsRetained(true);
                registeredPlayerRepository.save(p);
                Double price = finalRet2Money != null ? finalRet2Money : 0.0;
                Sold sold = Sold.builder().player(p).team(savedTeam).soldPrice(price).build();
                soldRepository.save(sold);
                auctionRepository.deleteByPlayerId(p.getId());
            });
        }

        return convertToDTO(savedTeam);
    }

    public TeamDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException("Team not found with id: " + id));
        return convertToDTO(team);
    }

    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TeamDTO updateTeam(Long id, TeamDTO teamDTO) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException("Team not found with id: " + id));

        team.setTeamName(teamDTO.getTeamName());
        team.setCaptain(teamDTO.getCaptain());
        team.setTotalBudget(teamDTO.getTotalBudget());
        team.setPlayerRetention1(teamDTO.getPlayerRetention1());
        team.setPlayerRetention2(teamDTO.getPlayerRetention2());
        team.setPlayerRetention1Money(teamDTO.getPlayerRetention1Money());
        team.setPlayerRetention2Money(teamDTO.getPlayerRetention2Money());

        Team updatedTeam = teamRepository.save(team);
        return convertToDTO(updatedTeam);
    }

    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException("Team not found with id: " + id));
        teamRepository.delete(team);
    }

    // Handled in AuctionService/SoldService primarily, but putting stub here
    @Transactional
    public void deductFromBudget(Long teamId, Double amount) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found with id: " + teamId));

        Double newBudget = team.getRemainingBudget() - amount;
        if (newBudget < 0) {
            throw new RuntimeException("Insufficient budget for team: " + team.getTeamName());
        }

        team.setRemainingBudget(newBudget);
        teamRepository.save(team);
    }

    private TeamDTO convertToDTO(Team team) {
        // Will be updated when implementing explicit Sold table querying
        return TeamDTO.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .captain(team.getCaptain())
                .totalBudget(team.getTotalBudget())
                .remainingBudget(team.getRemainingBudget())
                .playerRetention1(team.getPlayerRetention1())
                .playerRetention2(team.getPlayerRetention2())
                .playerRetention1Money(team.getPlayerRetention1Money())
                .playerRetention2Money(team.getPlayerRetention2Money())
                .playerCount(soldRepository.countByTeamId(team.getId()))
                .spentAmount(team.getTotalBudget() - team.getRemainingBudget())
                .build();
    }
}
