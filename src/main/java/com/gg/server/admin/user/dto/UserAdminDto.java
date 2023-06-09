package com.gg.server.admin.user.dto;

import com.gg.server.domain.user.User;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAdminDto {
    private Long id;
    private String intraId;
    private String eMail;
    private String imageUri;
    private RacketType racketType;
    private RoleType roleType;
    private Integer totalExp;
    private SnsType snsNotiOpt;

    static public UserAdminDto from (User user) {
        UserAdminDto userDto;
        if (user == null) {
            userDto = null;
        } else {
            userDto = UserAdminDto.builder()
                    .id(user.getId())
                    .intraId(user.getIntraId())
                    .eMail(user.getEMail())
                    .imageUri(user.getImageUri())
                    .racketType(user.getRacketType())
                    .roleType(user.getRoleType())
                    .totalExp(user.getTotalExp())
                    .snsNotiOpt(user.getSnsNotiOpt())
                    .build();
        }
        return userDto;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", intraId='" + intraId + '\'' +
                ", eMail='" + eMail + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", racketType=" + racketType + '\'' +
                ", roleType=" + roleType + '\'' +
                ", totalExp=" + totalExp +
                '}';
    }
}
