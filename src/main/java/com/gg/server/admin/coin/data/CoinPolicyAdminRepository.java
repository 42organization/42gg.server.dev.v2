package com.gg.server.admin.coin.data;

import com.gg.server.domain.coin.data.CoinPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoinPolicyAdminRepository extends JpaRepository<CoinPolicy, Long> {
    Optional<CoinPolicy> findFirstByOrderByIdDesc();
}
