package com.gg.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserImageDto {
    Long id;
    String intraId;
    String imageUri;
    LocalDateTime createdAt;
    Boolean isDeleted;

    public UserImageDto(Long id, String intraId, String imageUri, LocalDateTime now, boolean isDeleted) {
        this.id = id;
        this.intraId = intraId;
        this.imageUri = imageUri;
        this.createdAt = now;
        this.isDeleted = isDeleted;
    }
}
