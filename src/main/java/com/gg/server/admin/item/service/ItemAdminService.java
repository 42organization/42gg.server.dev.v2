package com.gg.server.admin.item.service;

import com.gg.server.admin.item.data.ItemAdminRepository;
import com.gg.server.admin.item.dto.ItemHistoryResponseDto;
import com.gg.server.admin.item.dto.ItemListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ItemAdminService {
    private final ItemAdminRepository itemRepository;
    @Transactional(readOnly = true)
    public ItemListResponseDto getAllItemHistory(Pageable pageable) {
        Page<ItemHistoryResponseDto> responseDtos = itemRepository.findAll(pageable).map(ItemHistoryResponseDto::new);
        return new ItemListResponseDto(responseDtos.getContent(), responseDtos.getTotalPages());
    }
}
