package gg.admin.repo.store;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.pingpong.store.CoinPolicy;

public interface CoinPolicyAdminRepository extends JpaRepository<CoinPolicy, Long> {
	Optional<CoinPolicy> findFirstByOrderByIdDesc();
}
