package com.example.spl2.repository;

import com.example.spl2.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByStatus(String status);

    List<Player> findByStatusAndRole(String status, String role);

    List<Player> findByTeamId(Long teamId);

    Optional<Player> findByPlayerName(String playerName);

    @Query("SELECT p FROM Player p WHERE p.status = 'REGISTERED' AND p.role = :role ORDER BY RAND() LIMIT 1")
    Optional<Player> findRandomRegisteredPlayerByRole(@Param("role") String role);

    long countByStatus(String status);

    long countByStatusAndRole(String status, String role);

    long countByTeamIdAndStatus(Long teamId, String status);
}


