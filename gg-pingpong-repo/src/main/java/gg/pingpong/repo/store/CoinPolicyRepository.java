package gg.pingpong.repo.store;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.pingpong.data.store.CoinPolicy;

public interface CoinPolicyRepository extends JpaRepository<CoinPolicy, Long> {
	Optional<CoinPolicy> findTopByOrderByCreatedAtDesc();

}
