package com.gg.server.admin.item.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gg.server.data.store.Item;

public interface ItemAdminRepository extends JpaRepository<Item, Long> {
	Page<Item> findAll(Pageable pageable);
}
