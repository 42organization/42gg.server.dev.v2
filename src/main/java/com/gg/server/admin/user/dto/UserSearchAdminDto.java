package com.gg.server.admin.user.dto;

import com.gg.server.domain.user.User;
import com.gg.server.domain.user.type.RoleType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSearchAdminDto {
    private Long id;
    private String intraId;
    private String statusMessage;
    private RoleType roleType;

    public static UserSearchAdminDto from(User user, String statusMessage) {
        UserSearchAdminDto userSearchResponseDto;
        if (user == null) {
            userSearchResponseDto = null;
        } else {
            userSearchResponseDto = UserSearchAdminDto.builder()
                    .id(user.getId())
                    .intraId(user.getIntraId())
                    .statusMessage(statusMessage)
                    .roleType(user.getRoleType())
                    .build();
        }
        return userSearchResponseDto;
    }

    @Override
    public String toString() {
        return "UserAdminDto{" +
                "id=" + id +
                ", intraId='" + intraId + '\'' +
                ", statusMessage='" + statusMessage + '\'' +
                ", roleType=" + roleType +
                '}';
    }
}
