package com.gg.server.admin.item.data;

import com.gg.server.domain.item.data.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemAdminRepository extends JpaRepository<Item, Long> {
    Page<Item> findAll(Pageable pageable);
}
