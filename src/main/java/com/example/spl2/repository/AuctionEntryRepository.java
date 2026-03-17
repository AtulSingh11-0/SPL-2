package com.example.spl2.repository;

import com.example.spl2.entity.AuctionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionEntryRepository extends JpaRepository<AuctionEntry, Long> {
    List<AuctionEntry> findAllByOrderByOrderIndexAsc();
    boolean existsByPlayerId(Long playerId);
    void deleteByPlayerId(Long playerId);
}

