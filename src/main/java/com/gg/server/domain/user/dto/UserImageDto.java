package com.gg.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserImageDto {
    Long id;
    Long userId;
    String imageUri;
    LocalDateTime createdAt;
    Boolean isDeleted;

    public UserImageDto(Long id, String imageUri, LocalDateTime now, boolean isDeleted) {
    }
}
