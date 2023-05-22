package com.gg.server.domain.noti.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotiListResponseDto {
    private List<NotiResponseDto> notifications;
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
        NotiListResponseDto other = (NotiListResponseDto) o;
        return Objects.equals(notifications, other.notifications);
    }
}