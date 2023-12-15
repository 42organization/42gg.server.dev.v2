package com.gg.server.domain.tier.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TierRepository extends JpaRepository<Tier, Long> {

    @Query("SELECT t FROM Tier t WHERE t.id = 1L")
    Optional<Tier> findStartTier();

    Optional<Tier> findByName(String name);
}