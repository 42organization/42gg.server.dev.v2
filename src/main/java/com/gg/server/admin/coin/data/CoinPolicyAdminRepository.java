package com.gg.server.admin.coin.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.data.manage.CoinPolicy;

public interface CoinPolicyAdminRepository extends JpaRepository<CoinPolicy, Long> {
	Optional<CoinPolicy> findFirstByOrderByIdDesc();
}
