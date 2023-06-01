package com.gg.server.admin.slotmanagement.dto;

import com.gg.server.domain.slotmanagement.SlotManagement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SlotCreateRequestDto {
    @NotNull(message = "Nothing pastSlotTime")
    @PositiveOrZero(message = "plz. Positive Or Zero")
    private Integer pastSlotTime;

    @NotNull(message = "Nothing futureSlotTime")
    @PositiveOrZero(message = "plz. Positive Or Zero")
    private Integer futureSlotTime;

    @NotNull(message = "Nothing interval")
    @PositiveOrZero(message = "plz. Positive Or Zero")
    private Integer interval;

    @NotNull(message = "Nothing openMinute")
    @PositiveOrZero(message = "plz. Positive Or Zero")
    private Integer openMinute;

    @NotNull(message = "Nothing startTime")
    @Future(message = "불가능한 예약시점입니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalDateTime startTime;

    public void updateStartTime(){
        LocalDateTime setStartTime = LocalDateTime.of(
                this.startTime.getYear(), this.startTime.getMonth(),
                this.startTime.getDayOfMonth(), this.startTime.getHour(), 0);
        this.startTime = setStartTime;
    }

}
