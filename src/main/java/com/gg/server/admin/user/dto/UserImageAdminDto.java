package com.gg.server.admin.user.dto;

import com.gg.server.domain.user.data.UserImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserImageAdminDto {
    Long id;
    Long userId;
    String userIntraId;
    String imageUri;
    LocalDateTime createdAt;
    Boolean isDeleted;

    public UserImageAdminDto(UserImage userImage) {
        this.id = userImage.getId();
        this.userId = userImage.getUser().getId();
        this.userIntraId = userImage.getUser().getIntraId();
        this.imageUri = userImage.getImageUri();
        this.createdAt = userImage.getCreatedAt();
        this.isDeleted = userImage.getIsDeleted();
    }
}
