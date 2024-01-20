package com.gg.server.domain.coin.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinPolicyRepository extends JpaRepository<CoinPolicy, Long> {
	Optional<CoinPolicy> findTopByOrderByCreatedAtDesc();

}
