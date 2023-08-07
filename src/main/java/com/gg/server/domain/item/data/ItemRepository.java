package com.gg.server.domain.item.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByIsVisible(boolean isVisible);
}