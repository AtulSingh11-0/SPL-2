package com.example.spl2.controller;

import com.example.spl2.dto.PlayerDTO;
import com.example.spl2.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/import")
@CrossOrigin(origins = "*")
public class ImportController {

    @Autowired
    private PlayerService playerService;

    /**
     * Upload and parse CSV file to import players
     * CSV Format: PlayerName,Age,Role,BasePrice
     */
    @PostMapping("/upload-csv")
    public ResponseEntity<?> uploadCSV(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return new ResponseEntity<>(
                        Map.of("error", "File is empty"),
                        HttpStatus.BAD_REQUEST);
            }

            String filename = file.getOriginalFilename();
            if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
                return new ResponseEntity<>(Map.of("error", "Please upload a CSV file"), HttpStatus.BAD_REQUEST);
            }

            log.info("Processing CSV file: {}", filename);

            List<String> csvLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    csvLines.add(line);
                }
            }

            if (csvLines.isEmpty()) {
                return new ResponseEntity<>(Map.of("error", "CSV file is empty"), HttpStatus.NO_CONTENT);
            }

            // Parse CSV rows - support extended format:
            // Name, Date Of Birth (string), Age, Role, Batting Type, Bowling Type, Image,
            // Category
            List<PlayerDTO> players = new ArrayList<>();
            // Skip header
            for (int i = 1; i < csvLines.size(); i++) {
                String row = csvLines.get(i).trim();
                if (row.isEmpty())
                    continue;
                // Split by comma - simple CSV parsing (assumes no embedded commas). For robust
                // parsing we can use a CSV library.
                String[] cols = row.split(",");
                // Ensure at least name and role and age and price columns exist; fill missing
                // optional fields with empty
                String name = cols.length > 0 ? cols[0].trim() : "";
                String dobStr = cols.length > 1 ? cols[1].trim() : ""; // keep as-is (string)
                String ageStr = cols.length > 2 ? cols[2].trim() : "0";
                String role = cols.length > 3 ? cols[3].trim() : "";
                String battingType = cols.length > 4 ? cols[4].trim() : null;
                String bowlingType = cols.length > 5 ? cols[5].trim() : null;
                String imageUrl = cols.length > 6 ? cols[6].trim() : null;
                String category = cols.length > 7 ? cols[7].trim() : null;

                if (name.isEmpty() || role.isEmpty()) {
                    log.warn("Skipping CSV row {} due to missing name or role", i + 1);
                    continue;
                }

                // Do not parse DOB - keep raw string from CSV to avoid unwanted timestamp
                // conversion

                Integer age = 0;
                try {
                    age = Integer.parseInt(ageStr);
                } catch (Exception ex) {
                    log.warn("Could not parse age {} for player {}", ageStr, name);
                    age = 0;
                }

                PlayerDTO p = PlayerDTO.builder()
                        .playerName(name)
                        .dateOfBirth(dobStr)
                        .age(age)
                        .role(role)
                        .battingType(battingType)
                        .bowlingType(bowlingType)
                        .imageUrl(imageUrl)
                        .category(category)
                        .basePrice(50.0)
                        .status("REGISTERED")
                        .build();
                players.add(p);
            }

            if (players.isEmpty()) {
                return new ResponseEntity<>(Map.of("error", "No valid player data found in CSV"),
                        HttpStatus.NO_CONTENT);
            }

            int savedCount = 0;
            int skippedCount = 0;
            for (PlayerDTO playerDTO : players) {
                try {
                    if (!playerService.playerExists(playerDTO.getPlayerName())) {
                        playerService.createPlayer(playerDTO);
                        savedCount++;
                    } else {
                        skippedCount++;
                    }
                } catch (Exception e) {
                    log.error("Failed to save player {}: {}", playerDTO.getPlayerName(), e.getMessage());
                }
            }

            long totalPlayers = playerService.countByStatus("REGISTERED");
            return new ResponseEntity<>(Map.of("totalProcessed", players.size(), "saved", savedCount, "skipped",
                    skippedCount, "totalInDatabase", totalPlayers), HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error importing CSV: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", "Failed to import CSV: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
