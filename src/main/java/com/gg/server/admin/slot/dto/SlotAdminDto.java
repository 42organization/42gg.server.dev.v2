package com.gg.server.admin.slot.dto;

import com.gg.server.domain.slotmanagement.SlotManagement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SlotAdminDto {
    @NotNull
    @PositiveOrZero
    private Integer pastSlotTime;
    @NotNull
    @PositiveOrZero
    private Integer futureSlotTime;
    @NotNull
    @PositiveOrZero
    private Integer interval;
    @NotNull
    @PositiveOrZero
    private Integer openMinute;

    public SlotAdminDto(SlotManagement slotManagement) {
        this.pastSlotTime = slotManagement.getPastSlotTime();
        this.futureSlotTime = slotManagement.getFutureSlotTime();
        this.interval = slotManagement.getGameInterval();
        this.openMinute = slotManagement.getOpenMinute();
    }
}
