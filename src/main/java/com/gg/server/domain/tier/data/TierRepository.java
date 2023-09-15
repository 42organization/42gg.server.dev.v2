package com.gg.server.domain.tier.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TierRepository extends JpaRepository<Tier, Long> {
    Optional<Tier> findByName(String name);
}