package com.gg.server.admin.slotmanagement.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class SlotListAdminResponseDto {
    List<SlotAdminDto> slotList;

    public SlotListAdminResponseDto(List<SlotAdminDto> slotList) {
        this.slotList = slotList;
    }
}
