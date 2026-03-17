package com.example.spl2.repository;

import com.example.spl2.entity.Bid;
import com.example.spl2.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByPlayerId(Long playerId);

    List<Bid> findByTeamId(Long teamId);

    Optional<Bid> findByPlayerIdAndIsWinningTrue(Long playerId);
}

