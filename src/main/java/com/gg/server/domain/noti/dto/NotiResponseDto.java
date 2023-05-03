package com.gg.server.domain.noti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NotiResponseDto<T> {
    private List<T> notifications;
    @Override
    public String toString() {
        return "NotiResponseDto{" +
                "notifications=" + notifications +
                '}';
    }
}