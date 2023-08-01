package com.gg.server.domain.user.dto;

import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.global.utils.ExpLevelCalculator;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDetailResponseDto {
    private String intraId;
    private String userImageUri;
    private String racketType;
    private String statusMessage;
    private Integer level;
    private Integer currentExp;
    private Integer maxExp;
    private Double expRate;
    private SnsType snsNotiOpt;

    public UserDetailResponseDto(User user, String statusMessage) {
        this.intraId = user.getIntraId();
        this.userImageUri = user.getImageUri();
        this.racketType = user.getRacketType().getCode();
        this.statusMessage = statusMessage;
        this.snsNotiOpt = user.getSnsNotiOpt();
        calculateExpAndLevel(user);
    }

    private void calculateExpAndLevel(User user) {
        this.currentExp = ExpLevelCalculator.getCurrentLevelMyExp(user.getTotalExp());
        this.maxExp = ExpLevelCalculator.getLevelMaxExp(ExpLevelCalculator.getLevel(user.getTotalExp()));
        this.level = ExpLevelCalculator.getLevel(user.getTotalExp());
        this.expRate = (double)(currentExp * 10000 / maxExp) / 100;
    }

}
