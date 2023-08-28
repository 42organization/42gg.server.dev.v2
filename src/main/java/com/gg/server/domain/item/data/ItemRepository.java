package com.gg.server.domain.item.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i " + "LEFT JOIN Receipt r ON r.item = i " + "WHERE i.isVisible = true " +
            "GROUP BY i " + "ORDER BY COALESCE(COUNT(r), 0) DESC, i.createdAt DESC")
    List<Item> findAllByPopDesc();
}