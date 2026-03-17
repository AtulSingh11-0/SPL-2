package com.example.spl2.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerDTO {

    private Long id;
    private String playerName;
    private String dateOfBirth;  // Changed to String for CSV import compatibility
    private Integer age;
    private String role;
    private Double basePrice;
    private String status; // REGISTERED, SOLD, UNSOLD
    private Long teamId;
    private String teamName;
    private Double soldPrice;
    private String battingType;
    private String bowlingType;
    private String imageUrl;
    private String category;
}
