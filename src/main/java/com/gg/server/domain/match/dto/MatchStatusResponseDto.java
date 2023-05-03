package com.gg.server.domain.match.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchStatusResponseDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Integer normalCount;
    private Integer rankCount;
}
