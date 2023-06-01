package com.gg.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserNormalDetailResponseDto {
    private String intraId;
    private String userImageUri;
    private Boolean isAdmin;
}
