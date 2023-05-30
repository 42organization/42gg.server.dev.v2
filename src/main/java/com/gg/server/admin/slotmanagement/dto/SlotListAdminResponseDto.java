package com.gg.server.admin.slotmanagement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SlotListAdminResponseDto {
    List<SlotAdminDto> slotList;

    public SlotListAdminResponseDto(List<SlotAdminDto> slotList) {
        this.slotList = slotList;
    }
}
