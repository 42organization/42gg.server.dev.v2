package com.gg.server.domain.item.service;

import com.gg.server.domain.item.data.ItemRepository;
import com.gg.server.domain.item.dto.ItemStoreListResponseDto;
import com.gg.server.domain.item.dto.ItemStoreResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemStoreListResponseDto getAllItems() {
        List<ItemStoreResponseDto> itemStoreListResponseDto = itemRepository.findAllByIsVisible(true)
                .stream().map(ItemStoreResponseDto::new).collect(Collectors.toList());
        return new ItemStoreListResponseDto(itemStoreListResponseDto);
    }
}
