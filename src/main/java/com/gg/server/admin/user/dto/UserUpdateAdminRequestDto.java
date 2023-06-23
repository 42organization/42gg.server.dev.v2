package com.gg.server.admin.user.dto;

import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserUpdateAdminRequestDto {
    private RacketType racketType;
    private String statusMessage;
    private Integer wins;
    private Integer losses;
    private Integer ppp;
    private String email;
    private String roleType;

    @Override
    public String toString() {
        return "UserUpdateRequestAdminDto{" + '\'' +
                ", racketType=" + racketType +
                ", statusMessage='" + statusMessage + '\'' +
                ", wins='" + wins + '\'' +
                ", losses'" + losses + '\'' +
                ", ppp=" + ppp + '\'' +
                ", email=" + email + '\'' +
                ", roleType=" + roleType +
                '}';
    }
}
