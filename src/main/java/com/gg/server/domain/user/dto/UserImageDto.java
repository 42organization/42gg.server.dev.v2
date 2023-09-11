package com.gg.server.domain.user.dto;

import com.gg.server.domain.user.type.EdgeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
public class UserImageDto {
    String intraId;
    String imageUri;
    EdgeType edge;
    String tierImage;

    public UserImageDto(String intraId, String imageUri, EdgeType edge, String tierImage) {
        this.intraId = intraId;
        this.imageUri = imageUri;
        this.edge = edge;
        this.tierImage = tierImage;
    }
}
