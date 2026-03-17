package com.example.spl2.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String teamName;

    @Column(nullable = false)
    private String captain;

    @Column(nullable = false)
    private Double totalBudget;

    @Column(nullable = false)
    private Double remainingBudget;

    private String playerRetention1;

    private String playerRetention2;

    private Double playerRetention1Money;

    private Double playerRetention2Money;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Player> players;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (remainingBudget == null) {
            remainingBudget = totalBudget;
            if (playerRetention1Money != null) {
                remainingBudget -= playerRetention1Money;
            }
            if (playerRetention2Money != null) {
                remainingBudget -= playerRetention2Money;
            }
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
