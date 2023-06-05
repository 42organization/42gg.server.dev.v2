package com.gg.server.admin.noti.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SendNotiAdminRequestDto {

    @NotNull
    private String intraId;

    @NotNull
    @Size(max=255)
    private String message;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SendNotiAdminRequestDto)) {
            return false;
        }
        SendNotiAdminRequestDto other = (SendNotiAdminRequestDto) o;
        return Objects.equals(message, other.message);
    }
}
