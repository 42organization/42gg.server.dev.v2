package com.gg.server.admin.item.controller;

import com.gg.server.admin.item.dto.ItemListResponseDto;
import com.gg.server.admin.item.dto.ItemUpdateRequestDto;
import com.gg.server.admin.item.service.ItemAdminService;
import com.gg.server.domain.item.exception.ItemImageLargeException;
import com.gg.server.domain.item.exception.ItemImageTypeException;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.dto.PageRequestDto;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

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

    @PutMapping(path="/{itemId}")
    public ResponseEntity updateItem(@PathVariable("itemId") Long itemId,
                                     @RequestPart @Valid ItemUpdateRequestDto itemRequestDto,
                                     @RequestPart(required = false) MultipartFile imgData,
                                     @Parameter(hidden = true) @Login UserDto user) throws IOException {
        if (imgData != null) {
            if (imgData.getSize() > 50000) {
                throw new ItemImageLargeException();
            } else if (imgData.getContentType() == null || !imgData.getContentType().equals("image/jpeg")) {
                throw new ItemImageTypeException();
            }
            itemAdminService.updateItem(itemId, itemRequestDto, imgData, user);
        } else {
            itemAdminService.updateItem(itemId, itemRequestDto, user);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity deleteItem(@PathVariable("itemId") Long itemId, @Parameter(hidden = true) @Login UserDto user) {
        itemAdminService.deleteItem(itemId, user);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
