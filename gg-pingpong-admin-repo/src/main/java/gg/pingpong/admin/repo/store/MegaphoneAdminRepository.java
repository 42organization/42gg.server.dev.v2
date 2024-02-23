package gg.pingpong.admin.repo.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import gg.pingpong.data.store.Megaphone;

public interface MegaphoneAdminRepository extends JpaRepository<Megaphone, Long> {
	Page<Megaphone> findMegaphonesByUserIntraId(String intraId, Pageable pageable);
}
