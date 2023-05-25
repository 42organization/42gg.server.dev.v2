package com.gg.server.domain.match.dto;

import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.slotmanagement.SlotManagement;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchUserInfoDto {
    private Long userId;
    private Option option;
    private Integer userPpp;
    private Integer pppGap;
    private SlotManagement slotManagement;

    @Builder
    public MatchUserInfoDto(Option option, RankRedis rankRedis, Season season,
                            SlotManagement slotManagement) {
        this.userId = rankRedis.getUserId();
        this.option = option;
        this.userPpp = rankRedis.getPpp();
        this.pppGap = season.getPppGap();
        this.slotManagement = slotManagement;
    }
}
