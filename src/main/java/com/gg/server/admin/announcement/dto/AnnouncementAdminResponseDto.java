package com.gg.server.admin.announcement.dto;

import com.gg.server.domain.announcement.Announcement;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AnnouncementAdminResponseDto {
    private String creatorIntraId;
    private String deleterIntraId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public AnnouncementAdminResponseDto(Announcement announcementAdmin)
    {
        this.content = announcementAdmin.getContent();
        this.creatorIntraId = announcementAdmin.getCreatorIntraId();
        this.deleterIntraId = announcementAdmin.getDeleterIntraId();
        this.createdAt = announcementAdmin.getCreatedAt();
        this.deletedAt = announcementAdmin.getDeletedAt();
    }
}
