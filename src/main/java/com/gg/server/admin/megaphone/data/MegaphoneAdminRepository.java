package com.gg.server.admin.megaphone.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.data.store.Megaphone;

public interface MegaphoneAdminRepository extends JpaRepository<Megaphone, Long> {
	Page<Megaphone> findMegaphonesByUserIntraId(String intraId, Pageable pageable);
}
