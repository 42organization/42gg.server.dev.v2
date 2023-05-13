package com.gg.server.admin.noti.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotiAdminResponseDto {
    private List<NotiAdminDto> notifications;
    @Override
    public String toString() {
        return "NotiResponseDto{" +
                "notifications=" + notifications +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotiAdminResponseDto other = (NotiAdminResponseDto) o;
        return Objects.equals(notifications, other.notifications);
    }
}