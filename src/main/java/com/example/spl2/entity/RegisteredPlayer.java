package com.example.spl2.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registered_players")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisteredPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String playerName;

    @Column
    private String dateOfBirth;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String role; // Batting All Rounder, Wicketkeeper, Batter, Bowling All Rounder, All Rounder, Bowler

    @Column(nullable = false)
    private Double basePrice;

    @Column(nullable = false)
    private String status; // REGISTERED, SOLD, UNSOLD

    private String battingType;

    private String bowlingType;

    private String imageUrl;

    private String category;

    // Retention flags
    @Column(columnDefinition = "boolean default false")
    private Boolean isRetained;

    private Integer retentionNumber; // 1 or 2

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retained_team_id")
    private Team retainedTeam;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "REGISTERED";
        }
        if (isRetained == null) {
            isRetained = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
