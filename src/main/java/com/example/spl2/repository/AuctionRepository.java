package com.example.spl2.repository;

import com.example.spl2.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findAllByOrderByOrderIndexAsc();
    boolean existsByPlayerId(Long playerId);
    void deleteByPlayerId(Long playerId);
}
