package com.example.spl2.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "unsold")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unsold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", nullable = false, unique = true)
    private Player player;

    private LocalDateTime unsoldAt;

    private Integer requeueCount;

    @PrePersist
    protected void onCreate() {
        if (unsoldAt == null) unsoldAt = LocalDateTime.now();
        if (requeueCount == null) requeueCount = 0;
    }
}

