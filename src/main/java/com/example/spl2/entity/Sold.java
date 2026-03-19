package com.example.spl2.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sold")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", nullable = false, unique = true)
    private RegisteredPlayer player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private Double soldPrice;

    private LocalDateTime soldAt;

    @PrePersist
    protected void onCreate() {
        if (soldAt == null) soldAt = LocalDateTime.now();
    }
}
