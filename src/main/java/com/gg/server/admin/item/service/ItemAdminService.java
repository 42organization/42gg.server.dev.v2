package com.gg.server.admin.item.service;

import com.gg.server.admin.item.data.ItemAdminRepository;
import com.gg.server.admin.item.dto.ItemHistoryResponseDto;
import com.gg.server.admin.item.dto.ItemListResponseDto;
import com.gg.server.admin.item.dto.ItemUpdateRequestDto;
import com.gg.server.admin.item.exception.ItemNotFoundException;
import com.gg.server.domain.item.data.Item;
import com.gg.server.domain.item.exception.ItemNotAvailableException;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.aws.AsyncNewItemImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class ItemAdminService {

    private final ItemAdminRepository itemAdminRepository;
    private final AsyncNewItemImageUploader asyncNewItemImageUploader;

    @Transactional(readOnly = true)
    public ItemListResponseDto getAllItemHistory(Pageable pageable) {
        Page<ItemHistoryResponseDto> responseDtos = itemAdminRepository.findAll(pageable).map(ItemHistoryResponseDto::new);
        return new ItemListResponseDto(responseDtos.getContent(), responseDtos.getTotalPages());
    }

    // 아이템 수정 시 신규 이미지가 존재하는 경우
    @Transactional
    public void updateItem(Long itemId, ItemUpdateRequestDto itemUpdateRequestDto,
                           MultipartFile itemImageFile, UserDto user) throws IOException {
        Item item = itemAdminRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        if (!item.getIsVisible()) {
            throw new ItemNotAvailableException();
        }
        item.setVisibility(user.getIntraId());
        Item newItem = new Item(itemUpdateRequestDto, user.getIntraId(), null);
        if (itemImageFile != null)
            asyncNewItemImageUploader.upload(newItem, itemImageFile);
        itemAdminRepository.save(newItem);
    }

    // 아이템 수정 시 신규 이미지가 존재하지 않는 경우
    @Transactional
    public void updateItem(Long itemId, ItemUpdateRequestDto itemUpdateRequestDto,
                           UserDto user) {
        Item item = itemAdminRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        if (!item.getIsVisible()) {
            throw new ItemNotAvailableException();
        }
        item.setVisibility(user.getIntraId());
        Item newItem = new Item(itemUpdateRequestDto, user.getIntraId(), item.getImageUri());
        itemAdminRepository.save(newItem);
    }

    @Transactional
    public void deleteItem(Long itemId, UserDto user) {
        Item item = itemAdminRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        item.setVisibility(user.getIntraId());
    }
}
