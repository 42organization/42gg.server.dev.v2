package com.gg.server.domain.item.controller;

import com.gg.server.domain.item.dto.ItemStoreListResponseDto;
import com.gg.server.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

