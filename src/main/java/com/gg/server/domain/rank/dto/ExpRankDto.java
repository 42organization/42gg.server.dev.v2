package com.gg.server.domain.rank.dto;

import com.gg.server.domain.user.data.User;
import com.gg.server.global.utils.ExpLevelCalculator;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpRankDto {
    private String intraId;
    private Integer rank;
    private String statusMessage;
    private Integer level;
    private Integer exp;
    private String userImageUri;
    private String textColor;

    public static ExpRankDto from (User user, Integer rank, String statusMessage){
        ExpRankDto dto = ExpRankDto.builder()
                .intraId(user.getIntraId())
                .rank(user.getTotalExp() == 0 ? -1 : rank)
                .statusMessage(statusMessage)
                .level(ExpLevelCalculator.getLevel(user.getTotalExp()))
                .exp(user.getTotalExp())
                .userImageUri(user.getImageUri())
                .textColor(user.getTextColor())
                .build();
        return dto;
    }
    public static ExpRankDto from(ExpRankV2Dto dto) {
        return ExpRankDto.builder()
                .intraId(dto.getIntraId())
                .rank(dto.getRanking())
                .statusMessage(dto.getStatusMessage())
                .level(ExpLevelCalculator.getLevel(dto.getTotalExp()))
                .exp(dto.getTotalExp())
                .userImageUri(dto.getImageUri())
                .textColor(dto.getTextColor())
                .build();
    }
}