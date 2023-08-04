package com.gg.server.domain.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ItemStoreListResponseDto {
    private List<ItemStoreResponseDto> itemList;

    public ItemStoreListResponseDto(List<ItemStoreResponseDto> itemList){
        this.itemList = itemList;
    }
}
