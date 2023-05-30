package com.gg.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserJwtTokenDto {
    private String accessToken;

}
