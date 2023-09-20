package com.gg.server.admin.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemListResponseDto {
    private List<ItemHistoryResponseDto> historyList;
    private Integer totalPage;
}
