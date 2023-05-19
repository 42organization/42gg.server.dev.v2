package com.gg.server.admin.penalty.dto;

import com.gg.server.domain.penalty.data.RedisPenaltyUser;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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
