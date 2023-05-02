package com.gg.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserSearchResponseDto {
    private List<String> users;
}
