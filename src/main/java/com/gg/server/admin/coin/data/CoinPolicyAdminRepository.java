package com.gg.server.admin.coin.data;

import com.gg.server.domain.coin.data.CoinPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinPolicyAdminRepository extends JpaRepository<CoinPolicy, Long> {
}
