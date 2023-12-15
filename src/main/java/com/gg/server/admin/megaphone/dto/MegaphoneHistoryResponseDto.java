package com.gg.server.admin.megaphone.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MegaphoneHistoryResponseDto {
    private List<MegaphoneAdminResponseDto> megaphoneList;
    private int totalPage;

    public MegaphoneHistoryResponseDto(List<MegaphoneAdminResponseDto> newDtos, int totalPage){
        this.megaphoneList = newDtos;
        this.totalPage= totalPage;
    }
}
