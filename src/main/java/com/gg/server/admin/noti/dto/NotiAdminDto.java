package com.gg.server.admin.noti.dto;

import com.gg.server.admin.user.dto.UserAdminDto;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.type.NotiType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class NotiAdminDto {
    private Long id;
    private String intraId;
    private NotiType type;
    private Boolean isChecked;
    private String message;
    private LocalDateTime createdAt;

    public NotiAdminDto(Noti noti)
    {
        if (noti != null) {
            this.id = noti.getId();
            this.intraId = noti.getUser().getIntraId();
            this.type = noti.getType();
            this.isChecked = noti.getIsChecked();
            this.message = noti.getMessage();
            this.createdAt = noti.getCreatedAt();
        }
    }

    @Override
    public String toString() {
        return "NotiAdminDto{" +
                "id=" + id +
                ", user=" + intraId +
                ", type=" + type +
                ", isChecked=" + isChecked +
                ", message='" + message + '\'' +
                ", creatdDate=" + createdAt +
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
                && Objects.equals(intraId, other.intraId)
                && Objects.equals(type, other.type)
                && Objects.equals(isChecked, other.isChecked)
                && Objects.equals(message, other.message)
                && Objects.equals(createdAt, other.createdAt);
    }
}
