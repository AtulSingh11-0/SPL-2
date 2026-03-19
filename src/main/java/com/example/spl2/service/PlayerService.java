package com.example.spl2.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spl2.dto.PlayerDTO;
import com.example.spl2.entity.RegisteredPlayer;
import com.example.spl2.exception.PlayerNotFoundException;
import com.example.spl2.repository.RegisteredPlayerRepository;
import com.example.spl2.repository.SoldRepository;

@Service
public class PlayerService {

    @Autowired
    private RegisteredPlayerRepository playerRepository;

    @Autowired
    private SoldRepository soldRepository;

    @Transactional
    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
        RegisteredPlayer player = RegisteredPlayer.builder()
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
                .isRetained(false)
                .build();

        RegisteredPlayer savedPlayer = playerRepository.save(player);
        return convertToDTO(savedPlayer);
    }

    public PlayerDTO getPlayerById(Long id) {
        RegisteredPlayer player = playerRepository.findById(id)
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

    @Transactional
    public PlayerDTO updatePlayer(Long id, PlayerDTO playerDTO) {
        RegisteredPlayer player = playerRepository.findById(id)
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

        RegisteredPlayer updatedPlayer = playerRepository.save(player);
        return convertToDTO(updatedPlayer);
    }

    @Transactional
    public void deletePlayer(Long id) {
        RegisteredPlayer player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found with id: " + id));
        playerRepository.delete(player);
    }

    @Transactional
    public void deleteAllPlayers() {
        playerRepository.deleteAllInBatch();
    }

    @Transactional
    public long deletePlayersByStatus(String status) {
        // Find players by status
        List<RegisteredPlayer> players = playerRepository.findByStatus(status);
        long count = players.size();
        if (count > 0) {
            playerRepository.deleteAll(players);
        }
        return count;
    }

    public List<PlayerDTO> getPlayersByTeamId(Long teamId) {
        // A team's current players are those who are Sold to this team
        return soldRepository.findAll().stream()
                .filter(sold -> sold.getTeam() != null && sold.getTeam().getId().equals(teamId))
                .map(sold -> {
                    PlayerDTO dto = convertPlayerToDTO(sold.getPlayer());
                    dto.setSoldPrice(sold.getSoldPrice());
                    dto.setTeamId(sold.getTeam().getId());
                    dto.setTeamName(sold.getTeam().getTeamName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public long getCountByStatus(String status) {
        return playerRepository.countByStatus(status);
    }

    public long getCountByStatusAndRole(String status, String role) {
        return playerRepository.countByStatusAndRole(status, role);
    }

    public Optional<RegisteredPlayer> getRandomRegisteredPlayerByRole(String role) {
        return playerRepository.findRandomRegisteredPlayerByRole(role);
    }

    public PlayerDTO convertPlayerToDTO(RegisteredPlayer player) {
        return convertToDTO(player);
    }

    private PlayerDTO convertToDTO(RegisteredPlayer player) {
        PlayerDTO dto = PlayerDTO.builder()
                .id(player.getId())
                .playerName(player.getPlayerName())
                .dateOfBirth(player.getDateOfBirth())
                .age(player.getAge())
                .role(player.getRole())
                .basePrice(player.getBasePrice())
                .status(player.getStatus())
                .battingType(player.getBattingType())
                .bowlingType(player.getBowlingType())
                .imageUrl(player.getImageUrl())
                .category(player.getCategory())
                .build();

        return dto;
    }
}
