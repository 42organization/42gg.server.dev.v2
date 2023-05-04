package com.gg.server.domain.user.dto;

import com.gg.server.domain.user.type.SnsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
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

}
