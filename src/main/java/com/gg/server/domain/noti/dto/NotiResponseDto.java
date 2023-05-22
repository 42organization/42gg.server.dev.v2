package com.gg.server.domain.noti.dto;

import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotiResponseDto {
    private Long id;
    private NotiType type;
    private Boolean isChecked;
    private String message;
    private LocalDateTime creatdAt;

    static public NotiResponseDto from (Noti noti)
    {
        NotiResponseDto notiResponseDto;
        if (noti == null)
            notiResponseDto = null;
        else {
            notiResponseDto = NotiResponseDto.builder()
                    .id(noti.getId())
                    .type(noti.getType())
                    .isChecked(noti.getIsChecked())
                    .message(noti.getMessage())
                    .creatdAt(noti.getCreatedAt())
                    .build();
        }
        return notiResponseDto;
    }

    @Override
    public String toString() {
        return "NotiDto{" +
                "id=" + id +
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
        if (!(o instanceof NotiResponseDto)) {
            return false;
        }
        NotiResponseDto other = (NotiResponseDto) o;
        return Objects.equals(id, other.id)
                && Objects.equals(type, other.type)
                && Objects.equals(isChecked, other.isChecked)
                && Objects.equals(message, other.message)
                && Objects.equals(creatdAt, other.creatdAt);
    }
}