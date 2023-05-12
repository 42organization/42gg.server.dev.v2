package com.gg.server.admin.penalty.dto;

import com.gg.server.admin.penalty.data.RedisPenaltyUser;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PenaltyUserResponseDto {
    private String intraId;
    private String reason;
    private LocalDateTime releaseTime;

    public PenaltyUserResponseDto(RedisPenaltyUser penaltyUser) {
        this.intraId = penaltyUser.getIntraId();
        this.reason = penaltyUser.getReason();
        this.releaseTime = penaltyUser.getReleaseTime();
    }
}
