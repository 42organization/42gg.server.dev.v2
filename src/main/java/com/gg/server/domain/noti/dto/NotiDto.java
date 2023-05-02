package com.gg.server.domain.noti.dto;

import com.gg.server.domain.noti.Noti;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.noti.type.NotiType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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
}
