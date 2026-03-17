package com.example.spl2.repository;

import com.example.spl2.entity.Unsold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnsoldRepository extends JpaRepository<Unsold, Long> {
    Optional<Unsold> findByPlayerId(Long playerId);
}

