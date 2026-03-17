package com.example.spl2.service;

import com.example.spl2.dto.PlayerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GoogleSheetsService {

    @Value("${google.sheets.api.key:}")
    private String googleSheetsApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Fetch players from Google Sheets using the simple REST API and API key.
     * This avoids the need for OAuth credentials for quick local/dev usage.
     * Expected sheet response shape: { "range": "...", "values": [ [col1, col2, ...], ... ] }
     *
     * @param spreadsheetId the spreadsheet id (not the full URL)
     * @param range         the A1 range (e.g. "Sheet1!A:E")
     * @return list of PlayerDTO
     */
    public List<PlayerDTO> fetchPlayersFromSheet(String spreadsheetId, String range) {
        if (spreadsheetId == null || spreadsheetId.isEmpty()) {
            throw new IllegalArgumentException("spreadsheetId is required");
        }

        if (googleSheetsApiKey == null || googleSheetsApiKey.isEmpty()) {
            // Prefer clear explanation so developer knows how to fix 403
            throw new IllegalStateException("Google Sheets API key is not configured. " +
                    "Set 'google.sheets.api.key' in application.properties or as an environment variable.");
        }

        String url = String.format(
                "https://sheets.googleapis.com/v4/spreadsheets/%s/values/%s?key=%s",
                spreadsheetId, range, googleSheetsApiKey);

        try {
            log.info("Requesting Google Sheets values from URL: {}", url);
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map body = response.getBody();
            if (body == null || !body.containsKey("values")) {
                log.warn("No data found in sheet {}");
                return new ArrayList<>();
            }

            Object valuesObj = body.get("values");
            if (!(valuesObj instanceof List)) {
                log.warn("Unexpected sheet `values` format");
                return new ArrayList<>();
            }

            List<List<Object>> values = (List<List<Object>>) valuesObj;
            return parseSheetRows(values);

        } catch (HttpClientErrorException.Forbidden e) {
            log.error("403 Forbidden when accessing Google Sheets: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Failed to fetch data from Google Sheets: 403 Forbidden - please provide a valid API key or share the sheet for public access");
        } catch (HttpClientErrorException e) {
            log.error("HTTP error when accessing Google Sheets: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Failed to fetch data from Google Sheets: " + e.getStatusText());
        } catch (Exception e) {
            log.error("Error fetching from Google Sheets: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch data from Google Sheets: " + e.getMessage());
        }
    }

    private List<PlayerDTO> parseSheetRows(List<List<Object>> values) {
        List<PlayerDTO> players = new ArrayList<>();

        if (values == null || values.isEmpty()) {
            log.warn("No rows in sheet");
            return players;
        }

        // Skip header row (index 0)
        for (int i = 1; i < values.size(); i++) {
            List<Object> row = values.get(i);

            // Ensure row has enough columns (expect at least 4: name, age, role, basePrice)
            if (row == null || row.size() < 4) {
                log.warn("Row {} has insufficient columns, skipping", i);
                continue;
            }

            try {
                String playerName = row.get(0) != null ? row.get(0).toString().trim() : "";
                String ageStr = row.get(1) != null ? row.get(1).toString().trim() : "0";
                String role = row.get(2) != null ? row.get(2).toString().trim() : "";
                String basePriceStr = row.get(3) != null ? row.get(3).toString().trim() : "0";

                if (playerName.isEmpty() || role.isEmpty()) {
                    log.warn("Skipping empty row {}", i);
                    continue;
                }

                Integer age = parseInteger(ageStr);
                Double basePrice = parseDouble(basePriceStr);

                if (age < 18 || age > 50) {
                    log.warn("Invalid age {} for player {}, skipping", age, playerName);
                    continue;
                }

                if (basePrice <= 0) {
                    log.warn("Invalid base price {} for player {}, skipping", basePrice, playerName);
                    continue;
                }

                PlayerDTO player = PlayerDTO.builder()
                        .playerName(playerName)
                        .age(age)
                        .role(role)
                        .basePrice(basePrice)
                        .status("REGISTERED")
                        .build();

                players.add(player);

            } catch (Exception e) {
                log.error("Error parsing row {}: {}", i, e.getMessage());
            }
        }

        log.info("Parsed {} players from sheet", players.size());
        return players;
    }

    /**
     * Extracts Spreadsheet ID from a full sheet URL.
     */
    public String extractSpreadsheetId(String sheetUrl) {
        if (sheetUrl == null) return null;
        try {
            String[] parts = sheetUrl.split("/d/");
            if (parts.length > 1) {
                String[] idParts = parts[1].split("/");
                return idParts[0];
            }
        } catch (Exception e) {
            log.error("Error extracting spreadsheet ID: {}", e.getMessage());
        }
        return null;
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Could not parse integer: {}", value);
            return 0;
        }
    }

    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            log.warn("Could not parse double: {}", value);
            return 0.0;
        }
    }

    /**
     * Convenience wrapper for syncFromGoogleForm using the user's provided spreadsheet id.
     */
    public List<PlayerDTO> syncFromGoogleForm() {
        String spreadsheetId = "1R7gd375x-UXpw0bfa-T2RkeM7-IoIm58uDRnhJ_wJgo";
        String range = "Sheet1!A:E";
        return fetchPlayersFromSheet(spreadsheetId, range);
    }

    /**
     * Parse CSV data into PlayerDTO objects
     * CSV Format: PlayerName,Age,Role,BasePrice
     */
    public List<PlayerDTO> parseCSVData(List<String> csvLines) {
        List<PlayerDTO> players = new ArrayList<>();

        if (csvLines == null || csvLines.isEmpty()) {
            log.warn("No CSV data provided");
            return players;
        }

        // Skip header row (first line)
        for (int i = 1; i < csvLines.size(); i++) {
            String line = csvLines.get(i).trim();

            // Skip empty lines
            if (line.isEmpty()) {
                continue;
            }

            try {
                // Split by comma
                String[] parts = line.split(",");

                if (parts.length < 4) {
                    log.warn("Row {} has insufficient columns, skipping", i);
                    continue;
                }

                // Parse row data
                String playerName = parts[0].trim();
                String ageStr = parts[1].trim();
                String role = parts[2].trim();
                String basePriceStr = parts[3].trim();

                // Skip empty fields
                if (playerName.isEmpty() || role.isEmpty()) {
                    log.warn("Row {} has empty fields, skipping", i);
                    continue;
                }

                // Convert to proper types
                Integer age = parseInteger(ageStr);
                Double basePrice = parseDouble(basePriceStr);

                // Validate data
                if (age < 18 || age > 50) {
                    log.warn("Invalid age {} for player {}, skipping", age, playerName);
                    continue;
                }

                if (basePrice <= 0) {
                    log.warn("Invalid base price {} for player {}, skipping", basePrice, playerName);
                    continue;
                }

                // Create PlayerDTO
                PlayerDTO player = PlayerDTO.builder()
                        .playerName(playerName)
                        .age(age)
                        .role(role)
                        .basePrice(basePrice)
                        .status("REGISTERED")
                        .build();

                players.add(player);
                log.info("Added player from CSV: {}", playerName);

            } catch (Exception e) {
                log.error("Error parsing row {}: {}", i, e.getMessage());
                continue;
            }
        }

        log.info("Successfully parsed {} players from CSV", players.size());
        return players;
    }
}
