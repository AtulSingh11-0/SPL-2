package com.example.spl2.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDTO {

    private Long id;
    private String teamName;
    private String captain;
    private Double totalBudget;
    private Double remainingBudget;
    private String playerRetention1;
    private String playerRetention2;
    private Double playerRetention1Money;
    private Double playerRetention2Money;
    private List<PlayerDTO> players;
    private Integer playerCount;
    private Double spentAmount;
}

