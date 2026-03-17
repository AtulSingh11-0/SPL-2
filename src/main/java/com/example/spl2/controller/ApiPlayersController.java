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
@RequestMapping("/api/players")
@CrossOrigin(origins = "*")
public class ApiPlayersController {

    @Autowired
    private PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<PlayerDTO>> listPlayers(@RequestParam(required = false) String status,
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

    @PostMapping
    public ResponseEntity<PlayerDTO> create(@RequestBody PlayerDTO playerDTO) {
        PlayerDTO created = playerService.createPlayer(playerDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> delete(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            long deleted = playerService.deletePlayersByStatus(status);
            return new ResponseEntity<>(Map.of("deleted", deleted, "status", status), HttpStatus.OK);
        } else {
            playerService.deleteAllPlayers();
            return new ResponseEntity<>(Map.of("deleted", "ALL"), HttpStatus.OK);
        }
    }
}

