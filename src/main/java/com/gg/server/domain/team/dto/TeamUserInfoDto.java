package com.gg.server.domain.team.dto;

import com.gg.server.global.utils.ExpLevelCalculator;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamUserInfoDto {
    private String intraId;
    private String userImageUri;
    private Integer level;
    public TeamUserInfoDto(String intraId, String userImageUri, Integer exp) {
        this.intraId = intraId;
        this.userImageUri = userImageUri;
        this.level = ExpLevelCalculator.getLevel(exp);
    }

    @Override
    public String toString() {
        return "TeamUserInfoDto{" +
                "intraId='" + intraId + '\'' +
                ", userImageUri='" + userImageUri + '\'' +
                ", level=" + level +
                '}';
    }
}
