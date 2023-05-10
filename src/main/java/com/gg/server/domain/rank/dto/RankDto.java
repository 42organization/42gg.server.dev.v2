package com.gg.server.domain.rank.dto;

import com.gg.server.domain.rank.redis.RankRedis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RankDto {
    private String intraId;
    private int rank;
    private int ppp;
    private String statusMessage;

    public static RankDto from(RankRedis rankRedis, int rank) {
        RankDto dto = RankDto.builder()
                .intraId(rankRedis.getIntraId())
                .rank(rank)
                .ppp(rankRedis.getPpp())
                .statusMessage(rankRedis.getStatusMessage())
                .build();
        return dto;
    }
}
