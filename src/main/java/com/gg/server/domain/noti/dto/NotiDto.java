package com.gg.server.domain.noti.dto;

import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.noti.type.NotiType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
public class NotiDto {
    private Long id;
    private UserDto user;
    private NotiType type;
    private Boolean isChecked;
    private String message;
    private LocalDateTime creatdAt;

    static public NotiDto from (Noti noti)
    {
        NotiDto notiDto;
        if (noti == null)
            notiDto = null;
        else {
            notiDto = NotiDto.builder()
                    .id(noti.getId())
                    .user(UserDto.from(noti.getUser()))
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
        return "NotiDto{" +
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
        if (!(o instanceof NotiDto)) {
            return false;
        }
        NotiDto other = (NotiDto) o;
        return Objects.equals(id, other.id)
                && Objects.equals(user, other.user)
                && Objects.equals(type, other.type)
                && Objects.equals(isChecked, other.isChecked)
                && Objects.equals(message, other.message)
                && Objects.equals(creatdAt, other.creatdAt);
    }
}
