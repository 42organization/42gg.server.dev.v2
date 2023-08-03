package com.gg.server.domain.coin.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinPolicyRepository extends JpaRepository<CoinPolicy, Long> {
    CoinPolicy findTopByOrderByCreatedAtDesc();

}
