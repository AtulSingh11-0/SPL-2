package com.example.spl2.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auction_state")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String currentCategory; // All-rounder Bowling, All-rounder Batting, etc.

    @Column(nullable = false)
    private Integer categoryIndex; // Track which category we're in

    @ManyToOne
    @JoinColumn(name = "current_player_id")
    private Player currentPlayer;

    @Column(nullable = false)
    private Boolean auctionActive;

    private LocalDateTime categoryStartTime;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
        if (auctionActive == null) {
            auctionActive = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

