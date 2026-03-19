package com.example.spl2.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id", nullable = false, unique = true)
    private RegisteredPlayer player;

    @Column(nullable = false)
    private Integer orderIndex; // sequential order in auction queue

    @Column(nullable = false)
    private String category; // role/category to which the player belongs
}
