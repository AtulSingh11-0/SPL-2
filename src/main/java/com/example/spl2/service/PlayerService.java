package com.example.spl2.service;

import com.example.spl2.dto.PlayerDTO;
import com.example.spl2.entity.Player;
import com.example.spl2.entity.Team;
import com.example.spl2.exception.PlayerNotFoundException;
import com.example.spl2.exception.TeamNotFoundException;
import com.example.spl2.repository.PlayerRepository;
import com.example.spl2.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Transactional
    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
        Player player = Player.builder()
                .playerName(playerDTO.getPlayerName())
                .dateOfBirth(playerDTO.getDateOfBirth())
                .age(playerDTO.getAge())
                .role(playerDTO.getRole())
                .basePrice(playerDTO.getBasePrice())
                .battingType(playerDTO.getBattingType())
                .bowlingType(playerDTO.getBowlingType())
                .imageUrl(playerDTO.getImageUrl())
                .category(playerDTO.getCategory())
                .status("REGISTERED")
                .build();

        Player savedPlayer = playerRepository.save(player);
        return convertToDTO(savedPlayer);
    }

    public PlayerDTO getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found with id: " + id));
        return convertToDTO(player);
    }

    public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public long countByStatus(String status) {
        return playerRepository.findByStatus(status).stream().count();
    }

    public boolean playerExists(String playerName) {
        return playerRepository.findAll().stream()
                .anyMatch(p -> p.getPlayerName().equalsIgnoreCase(playerName));
    }

    public List<PlayerDTO> getPlayersByStatus(String status) {
        return playerRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PlayerDTO> getPlayersByStatusAndRole(String status, String role) {
        return playerRepository.findByStatusAndRole(status, role).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PlayerDTO> getPlayersByTeamId(Long teamId) {
        return playerRepository.findByTeamId(teamId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlayerDTO updatePlayer(Long id, PlayerDTO playerDTO) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found with id: " + id));

        player.setPlayerName(playerDTO.getPlayerName());
        player.setDateOfBirth(playerDTO.getDateOfBirth());
        player.setAge(playerDTO.getAge());
        player.setRole(playerDTO.getRole());
        player.setBasePrice(playerDTO.getBasePrice());
        player.setBattingType(playerDTO.getBattingType());
        player.setBowlingType(playerDTO.getBowlingType());
        player.setImageUrl(playerDTO.getImageUrl());
        player.setCategory(playerDTO.getCategory());

        Player updatedPlayer = playerRepository.save(player);
        return convertToDTO(updatedPlayer);
    }

    @Transactional
    public PlayerDTO sellPlayer(Long playerId, Long teamId, Double soldPrice) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found with id: " + playerId));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found with id: " + teamId));

        player.setTeam(team);
        player.setStatus("SOLD");
        player.setSoldPrice(soldPrice);

        Player savedPlayer = playerRepository.save(player);
        return convertToDTO(savedPlayer);
    }

    @Transactional
    public PlayerDTO markPlayerUnsold(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found with id: " + playerId));

        player.setTeam(null);
        player.setStatus("UNSOLD");
        player.setSoldPrice(null);

        Player savedPlayer = playerRepository.save(player);
        return convertToDTO(savedPlayer);
    }

    @Transactional
    public void deletePlayer(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found with id: " + id));
        playerRepository.delete(player);
    }

    @Transactional
    public void deleteAllPlayers() {
        playerRepository.deleteAllInBatch();
    }

    @Transactional
    public long deletePlayersByStatus(String status) {
        List<Player> players = playerRepository.findByStatus(status);
        long count = players.size();
        if (count > 0) {
            playerRepository.deleteAll(players);
        }
        return count;
    }

    public long getCountByStatus(String status) {
        return playerRepository.countByStatus(status);
    }

    public long getCountByStatusAndRole(String status, String role) {
        return playerRepository.countByStatusAndRole(status, role);
    }

    public Optional<Player> getRandomRegisteredPlayerByRole(String role) {
        return playerRepository.findRandomRegisteredPlayerByRole(role);
    }

    public PlayerDTO convertPlayerToDTO(Player player) {
        return convertToDTO(player);
    }

    private PlayerDTO convertToDTO(Player player) {
        PlayerDTO dto = PlayerDTO.builder()
                .id(player.getId())
                .playerName(player.getPlayerName())
                .dateOfBirth(player.getDateOfBirth())
                .age(player.getAge())
                .role(player.getRole())
                .basePrice(player.getBasePrice())
                .status(player.getStatus())
                .soldPrice(player.getSoldPrice())
                .battingType(player.getBattingType())
                .bowlingType(player.getBowlingType())
                .imageUrl(player.getImageUrl())
                .category(player.getCategory())
                .build();

        if (player.getTeam() != null) {
            dto.setTeamId(player.getTeam().getId());
            dto.setTeamName(player.getTeam().getTeamName());
        }

        return dto;
    }
}
