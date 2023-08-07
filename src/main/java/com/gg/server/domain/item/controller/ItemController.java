package com.gg.server.domain.item.controller;

import com.gg.server.domain.item.dto.ItemStoreListResponseDto;
import com.gg.server.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/store")
    public ItemStoreListResponseDto getAllItems() {
        return itemService.getAllItems();
    }
}
