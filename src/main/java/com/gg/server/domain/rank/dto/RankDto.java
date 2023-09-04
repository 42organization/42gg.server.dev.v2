package com.gg.server.domain.rank.dto;

import com.gg.server.domain.rank.redis.RankRedis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RankDto {
    private String intraId;
    private int rank;
    private int ppp;
    private String statusMessage;
    private String tierImageUri;


    public static RankDto from(RankRedis rankRedis, int rank) {
        RankDto dto = RankDto.builder()
                .intraId(rankRedis.getIntraId())
                .rank(rank)
                .ppp(rankRedis.getPpp())
                .statusMessage(rankRedis.getStatusMessage())
                .tierImageUri(rankRedis.getTierImageUrl())
                .build();
        return dto;
    }
}
