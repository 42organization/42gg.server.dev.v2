package com.gg.server.admin.pchange.data;

import com.gg.server.domain.pchange.data.PChange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PChangeAdminRepository extends JpaRepository<PChange, Long>, PChangeAdminRepositoryCustom {
}
