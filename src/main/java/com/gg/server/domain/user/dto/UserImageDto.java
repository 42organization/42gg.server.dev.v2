package com.gg.server.domain.user.dto;

import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.type.EdgeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
public class UserImageDto {
    private String intraId;
    private String imageUri;
    private EdgeType edge;
    private String tierImage;

    public UserImageDto(String intraId, String imageUri, EdgeType edge, String tierImage) {
        this.intraId = intraId;
        this.imageUri = imageUri;
        this.edge = edge;
        this.tierImage = tierImage;
    }

    public UserImageDto(User user){
        this.intraId = (user == null)? null : user.getIntraId();
        this.imageUri = (user == null)? null : user.getImageUri();
    }
}
