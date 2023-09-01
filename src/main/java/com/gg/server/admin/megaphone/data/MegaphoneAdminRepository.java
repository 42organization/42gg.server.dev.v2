package com.gg.server.admin.megaphone.data;

import com.gg.server.domain.megaphone.data.Megaphone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MegaphoneAdminRepository extends JpaRepository<Megaphone, Long> {
    Page<Megaphone> findMegaphonesByUserIntraId(String intraId, Pageable pageable);
}
