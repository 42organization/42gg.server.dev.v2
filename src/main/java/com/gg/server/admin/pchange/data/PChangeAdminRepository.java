package com.gg.server.admin.pchange.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.data.game.PChange;

public interface PChangeAdminRepository extends JpaRepository<PChange, Long>, PChangeAdminRepositoryCustom {
}
