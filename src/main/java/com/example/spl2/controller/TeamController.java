package com.example.spl2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spl2.dto.TeamDTO;
import com.example.spl2.service.AuctionService;
import com.example.spl2.service.TeamService;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private AuctionService auctionService;

    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(@RequestBody TeamDTO teamDTO) {
        TeamDTO createdTeam = teamService.createTeam(teamDTO);
        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        TeamDTO team = teamService.getTeamById(id);
        return new ResponseEntity<>(team, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        List<TeamDTO> teams = teamService.getAllTeams();
        return new ResponseEntity<>(teams, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO> updateTeam(@PathVariable Long id, @RequestBody TeamDTO teamDTO) {
        TeamDTO updatedTeam = teamService.updateTeam(id, teamDTO);
        return new ResponseEntity<>(updatedTeam, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{teamId}/release-player/{playerId}")
    public ResponseEntity<String> releasePlayer(
            @PathVariable Long teamId,
            @PathVariable Long playerId,
            @RequestParam Double releasePrice) {
        auctionService.releasePlayer(teamId, playerId, releasePrice);
        return new ResponseEntity<>("Player released successfully", HttpStatus.OK);
    }
}
