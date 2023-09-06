package com.gg.server.domain.user.dto;

import com.gg.server.domain.user.type.EdgeType;
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
    EdgeType edge;
    String tierImage;
    LocalDateTime createdAt;
    Boolean isDeleted;

    public UserImageDto(Long id, String intraId, String imageUri, EdgeType edge, String tierImage, LocalDateTime now, boolean isDeleted) {
        this.id = id;
        this.intraId = intraId;
        this.imageUri = imageUri;
        this.edge = edge;
        this.tierImage = tierImage;
        this.createdAt = now;
        this.isDeleted = isDeleted;
    }
}
