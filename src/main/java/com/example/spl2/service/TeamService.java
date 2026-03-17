package com.example.spl2.service;

import com.example.spl2.dto.TeamDTO;
import com.example.spl2.entity.Team;
import com.example.spl2.entity.Player;
import com.example.spl2.exception.TeamAlreadyExistsException;
import com.example.spl2.exception.TeamNotFoundException;
import com.example.spl2.repository.TeamRepository;
import com.example.spl2.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public TeamDTO createTeam(TeamDTO teamDTO) {
        if (teamRepository.existsByTeamName(teamDTO.getTeamName())) {
            throw new TeamAlreadyExistsException("Team with name '" + teamDTO.getTeamName() + "' already exists");
        }

        Team team = Team.builder()
                .teamName(teamDTO.getTeamName())
                .captain(teamDTO.getCaptain())
                .totalBudget(teamDTO.getTotalBudget())
                .playerRetention1(teamDTO.getPlayerRetention1())
                .playerRetention2(teamDTO.getPlayerRetention2())
                .playerRetention1Money(teamDTO.getPlayerRetention1Money())
                .playerRetention2Money(teamDTO.getPlayerRetention2Money())
                .build();

        // Calculate remaining budget after retentions
        Double remainingBudget = teamDTO.getTotalBudget();
        if (teamDTO.getPlayerRetention1Money() != null) {
            remainingBudget -= teamDTO.getPlayerRetention1Money();
        }
        if (teamDTO.getPlayerRetention2Money() != null) {
            remainingBudget -= teamDTO.getPlayerRetention2Money();
        }
        team.setRemainingBudget(remainingBudget);

        Team savedTeam = teamRepository.save(team);
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

    @Transactional
    public void releasePlayer(Long teamId, Long playerId, Double releasePrice) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found with id: " + teamId));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + playerId));

        if (!player.getTeam().getId().equals(teamId)) {
            throw new RuntimeException("Player does not belong to this team");
        }

        // Add release price back to team budget
        team.setRemainingBudget(team.getRemainingBudget() + releasePrice);

        // Remove player from team
        player.setTeam(null);
        player.setStatus("REGISTERED");
        player.setSoldPrice(null);

        playerRepository.save(player);
        teamRepository.save(team);
    }

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
        List<Player> players = playerRepository.findByTeamId(team.getId());
        Double spentAmount = players.stream()
                .mapToDouble(p -> p.getSoldPrice() != null ? p.getSoldPrice() : 0)
                .sum();

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
                .playerCount(players.size())
                .spentAmount(spentAmount)
                .build();
    }
}

