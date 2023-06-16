package com.gg.server.domain.user.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class UserAuthorizationDto {
    @NotNull(message = "invalid access")
    private String accessToken;
    public UserAuthorizationDto(String accessToken, Integer maxAge) {
        this.accessToken = accessToken;
    }
}
