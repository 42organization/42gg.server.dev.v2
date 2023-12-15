package com.gg.server.domain.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserItemListResponseDto {
    private List<UserItemResponseDto> storageItemList;
    private Integer totalPage;

    public UserItemListResponseDto(List<UserItemResponseDto> storageItemList, Integer totalPage){
        this.storageItemList = storageItemList;
        this.totalPage = totalPage;
    }

}