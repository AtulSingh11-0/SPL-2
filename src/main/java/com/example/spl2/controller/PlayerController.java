package com.example.spl2.controller;

import com.example.spl2.dto.PlayerDTO;
import com.example.spl2.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/players")
@CrossOrigin(origins = "*")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PostMapping
    public ResponseEntity<PlayerDTO> createPlayer(@RequestBody PlayerDTO playerDTO) {
        PlayerDTO createdPlayer = playerService.createPlayer(playerDTO);
        return new ResponseEntity<>(createdPlayer, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable Long id) {
        PlayerDTO player = playerService.getPlayerById(id);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PlayerDTO>> getPlayers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Long teamId) {

        List<PlayerDTO> players;

        if (teamId != null) {
            players = playerService.getPlayersByTeamId(teamId);
        } else if (status != null && role != null) {
            players = playerService.getPlayersByStatusAndRole(status, role);
        } else if (status != null) {
            players = playerService.getPlayersByStatus(status);
        } else {
            players = playerService.getAllPlayers();
        }

        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable Long id, @RequestBody PlayerDTO playerDTO) {
        PlayerDTO updatedPlayer = playerService.updatePlayer(id, playerDTO);
        return new ResponseEntity<>(updatedPlayer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Delete all players (USE WITH CAUTION)
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deletePlayers(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            long deleted = playerService.deletePlayersByStatus(status);
            return new ResponseEntity<>(Map.of("deleted", deleted, "status", status), HttpStatus.OK);
        } else {
            playerService.deleteAllPlayers();
            return new ResponseEntity<>(Map.of("deleted", "ALL"), HttpStatus.OK);
        }
    }
}

