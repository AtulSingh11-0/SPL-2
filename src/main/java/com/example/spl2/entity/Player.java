package com.example.spl2.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String playerName;

    @Column
    private String dateOfBirth;  // Changed from LocalDate to String to handle CSV import properly

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String role; // All-rounder Bowling, All-rounder Batting, Only Batsman, Only Bowler, Wicket Keeper

    @Column(nullable = false)
    private Double basePrice;

    @Column(nullable = false)
    private String status; // REGISTERED, SOLD, UNSOLD

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private Double soldPrice;

    private String battingType;

    private String bowlingType;

    private String imageUrl;

    private String category;

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
