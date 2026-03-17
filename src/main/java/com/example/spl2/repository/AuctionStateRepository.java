package com.example.spl2.repository;

import com.example.spl2.entity.AuctionState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuctionStateRepository extends JpaRepository<AuctionState, Long> {

    Optional<AuctionState> findById(Long id);

    Optional<AuctionState> findFirstByOrderByIdDesc();
}

