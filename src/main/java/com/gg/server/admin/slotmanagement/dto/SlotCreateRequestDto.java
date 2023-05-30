package com.gg.server.admin.slotmanagement.dto;

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
    @NotNull(message="Nothing pastSlotTime")
    @PositiveOrZero(message="plz. Positive Or Zero")
    private Integer pastSlotTime;

    @NotNull(message="Nothing futureSlotTime")
    @PositiveOrZero(message="plz. Positive Or Zero")
    private Integer futureSlotTime;

    @NotNull(message="Nothing interval")
    @PositiveOrZero(message="plz. Positive Or Zero")
    private Integer interval;

    @NotNull(message="Nothing openMinute")
    @PositiveOrZero(message="plz. Positive Or Zero")
    private Integer openMinute;

    public SlotAdminDto(SlotManagement slotManagement) {
        this.pastSlotTime = slotManagement.getPastSlotTime();
        this.futureSlotTime = slotManagement.getFutureSlotTime();
        this.interval = slotManagement.getGameInterval();
        this.openMinute = slotManagement.getOpenMinute();
    }
}
