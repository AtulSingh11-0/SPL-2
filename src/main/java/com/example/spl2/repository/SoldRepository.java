package com.example.spl2.repository;

import com.example.spl2.entity.Sold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SoldRepository extends JpaRepository<Sold, Long> {
    Optional<Sold> findByPlayerId(Long playerId);
    int countByTeamId(Long teamId);
}

