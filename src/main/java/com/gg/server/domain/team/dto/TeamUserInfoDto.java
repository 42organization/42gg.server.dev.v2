package com.gg.server.domain.team.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamUserInfoDto {
    private String intraId;
    private String userImageUri;
    private Integer level;

    @Builder
    public TeamUserInfoDto(String intraId, String userImageUri, Integer level) {
        this.intraId = intraId;
        this.userImageUri = userImageUri;
        this.level = level;
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
