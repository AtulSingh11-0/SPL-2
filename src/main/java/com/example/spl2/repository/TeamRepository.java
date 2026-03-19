package com.example.spl2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.spl2.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByTeamName(String teamName);

    boolean existsByTeamName(String teamName);
}
