package com.gg.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserNormalDetailResponseDto {
    private String intraId;
    private String userImageUri;
    private Boolean isAdmin;
    private Boolean isAttended;
    private String tierName;
    private String tierImageUri;
    private Integer level;
}
