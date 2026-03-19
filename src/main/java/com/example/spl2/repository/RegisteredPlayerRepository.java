package com.example.spl2.repository;

import com.example.spl2.entity.RegisteredPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegisteredPlayerRepository extends JpaRepository<RegisteredPlayer, Long> {

    List<RegisteredPlayer> findByStatus(String status);

    List<RegisteredPlayer> findByStatusAndRole(String status, String role);

    Optional<RegisteredPlayer> findByPlayerName(String playerName);

    @Query("SELECT p FROM RegisteredPlayer p WHERE p.status = 'REGISTERED' AND p.role = :role ORDER BY RAND() LIMIT 1")
    Optional<RegisteredPlayer> findRandomRegisteredPlayerByRole(@Param("role") String role);

    long countByStatus(String status);

    long countByStatusAndRole(String status, String role);
}
