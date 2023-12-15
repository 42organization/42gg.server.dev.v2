package com.gg.server.domain.item.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i WHERE i.isVisible = true ORDER BY i.createdAt DESC")
    List<Item> findAllByCreatedAtDesc();
}
