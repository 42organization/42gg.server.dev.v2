package com.gg.server.domain.coin.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoinPolicyRepository extends JpaRepository<CoinPolicy, Long> {
    Optional<CoinPolicy> findTopByOrderByCreatedAtDesc();

}
