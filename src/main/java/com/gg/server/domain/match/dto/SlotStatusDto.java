package com.gg.server.domain.match.dto;

import com.gg.server.domain.match.type.SlotStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SlotStatusDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SlotStatus status;

    @Override
    public String toString() {
        return "SlotStatusDto{" +
                "startTime = " + startTime +
                "endTime = " + endTime +
                "status = " + status.getCode() +
                "}";
    }
}
