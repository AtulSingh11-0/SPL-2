package com.example.spl2.controller;

import com.example.spl2.dto.PlayerDTO;
import com.example.spl2.entity.AuctionState;
import com.example.spl2.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auction")
@CrossOrigin(origins = "*")
public class ApiAuctionController {

    @Autowired
    private AuctionService auctionService;

    @PostMapping("/initialize")
    public ResponseEntity<String> initializeAuction() {
        try {
            int created = auctionService.initializeAuction();
            return new ResponseEntity<>(java.util.Map.of("createdEntries", created), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(java.util.Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/next-player")
    public ResponseEntity<?> getNextPlayer() {
        PlayerDTO nextPlayer = auctionService.getNextPlayer();
        if (nextPlayer == null) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(nextPlayer, HttpStatus.OK);
    }

    @PostMapping("/move-to-next-category")
    public ResponseEntity<?> moveToNextCategory() {
        PlayerDTO nextPlayer = auctionService.moveToNextCategory();
        if (nextPlayer == null) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(nextPlayer, HttpStatus.OK);
    }

    @PostMapping("/sell-player/{playerId}")
    public ResponseEntity<String> sellPlayer(
            @PathVariable Long playerId,
            @RequestParam Long teamId,
            @RequestParam Double soldPrice) {
        auctionService.sellPlayer(playerId, teamId, soldPrice);
        return new ResponseEntity<>("Player sold successfully", HttpStatus.OK);
    }

    @PostMapping("/unsold-player/{playerId}")
    public ResponseEntity<String> markPlayerUnsold(@PathVariable Long playerId) {
        auctionService.markPlayerUnsold(playerId);
        return new ResponseEntity<>("Player marked as unsold", HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<AuctionState> getAuctionStatus() {
        AuctionState status = auctionService.getAuctionStatus();
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAuctionCategories() {
        List<String> categories = auctionService.getAuctionCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
}

