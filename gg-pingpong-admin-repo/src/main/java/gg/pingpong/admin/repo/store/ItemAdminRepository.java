package gg.pingpong.admin.repo.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import gg.pingpong.data.store.Item;

public interface ItemAdminRepository extends JpaRepository<Item, Long> {
	Page<Item> findAll(Pageable pageable);
}
