package com.gg.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class UserHistoryResponseDto {
    private List<UserHistoryData> historics;
}
