package com.gg.server.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gg.server.domain.user.type.SnsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserDetailResponseDto {
    private String intraId;
    private String userImageUri;
    private String racketType;
    private String statusMessage;
    private Integer level;
    private Integer currentExp;
    private Integer maxExp;
    private Double expRate;
    private String snsNotiOpt;

    @Builder
    public UserDetailResponseDto(String intraId, String userImageUri, String racketType, String statusMessage, Integer level,
                                 Integer currentExp, Integer maxExp, String snsNotiOpt) {
        this.intraId = intraId;
        this.userImageUri = userImageUri;
        this.racketType = racketType;
        this.statusMessage = statusMessage;
        this.level = level;
        this.currentExp = currentExp;
        this.maxExp = maxExp;
        this.expRate = (double)(currentExp * 10000 / maxExp) / 100;
        this.snsNotiOpt = snsNotiOpt;
    }
}
