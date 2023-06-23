package com.gg.server.domain.user.dto;

import com.gg.server.domain.user.type.OauthType;
import com.gg.server.domain.user.type.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserOauthDto {
    private String oauthType;
}
