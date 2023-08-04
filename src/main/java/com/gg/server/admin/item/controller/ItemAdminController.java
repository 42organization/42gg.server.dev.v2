package com.gg.server.admin.item.controller;

import com.gg.server.admin.item.dto.ItemListResponseDto;
import com.gg.server.admin.item.dto.ItemRequestDto;
import com.gg.server.admin.item.service.ItemAdminService;
import com.gg.server.global.dto.PageRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/items")
public class ItemAdminController {
    private final ItemAdminService itemAdminService;

    @GetMapping("/history")
    public ItemListResponseDto getItemHistory(@ModelAttribute @Valid PageRequestDto pageRequestDto) {
        Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1, pageRequestDto.getSize(),
                Sort.by("createdAt").descending());
        return itemAdminService.getAllItemHistory(pageable);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity updateItem(@PathVariable("itemId") Long itemId, @RequestBody @Valid ItemRequestDto itemRequestDto) {
        itemAdminService.updateItem(itemId, itemRequestDto);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity deleteItem(@PathVariable("itemId") Long itemId) {
        itemAdminService.deleteItem(itemId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
