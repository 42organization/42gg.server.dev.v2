package gg.pingpong.admin.repo.coin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.pingpong.data.manage.CoinPolicy;

public interface CoinPolicyAdminRepository extends JpaRepository<CoinPolicy, Long> {
	Optional<CoinPolicy> findFirstByOrderByIdDesc();
}
