package com.gg.server.admin.noti.dto;

import com.gg.server.admin.user.dto.UserAdminDto;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.type.NotiType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
public class NotiAdminDto {
    private Long id;
    private UserAdminDto user;
    private NotiType type;
    private Boolean isChecked;
    private String message;
    private LocalDateTime creatdAt;

    static public NotiAdminDto from (Noti noti)
    {
        NotiAdminDto notiDto;
        if (noti == null)
            notiDto = null;
        else {
            notiDto = NotiAdminDto.builder()
                    .id(noti.getId())
                    .user(UserAdminDto.from(noti.getUser()))
                    .type(noti.getType())
                    .isChecked(noti.getIsChecked())
                    .message(noti.getMessage())
                    .creatdAt(noti.getCreatedAt())
                    .build();
        }
        return notiDto;
    }

    @Override
    public String toString() {
        return "NotiAdminDto{" +
                "id=" + id +
                ", user=" + user +
                ", type=" + type +
                ", isChecked=" + isChecked +
                ", message='" + message + '\'' +
                ", creatdDate=" + creatdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof NotiAdminDto)) {
            return false;
        }
        NotiAdminDto other = (NotiAdminDto) o;
        return Objects.equals(id, other.id)
                && Objects.equals(user, other.user)
                && Objects.equals(type, other.type)
                && Objects.equals(isChecked, other.isChecked)
                && Objects.equals(message, other.message)
                && Objects.equals(creatdAt, other.creatdAt);
    }
}
