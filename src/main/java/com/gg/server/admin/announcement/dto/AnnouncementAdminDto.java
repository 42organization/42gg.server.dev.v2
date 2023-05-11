package com.gg.server.admin.announcement.dto;

import com.gg.server.domain.announcement.Announcement;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AnnouncementAdminDto {
    private String creatorIntraId;
    private String deleterIntraId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    static public AnnouncementAdminDto from(Announcement announcement) {
        return AnnouncementAdminDto.builder()
                .content(announcement.getContent())
                .creatorIntraId(announcement.getCreatorIntraId())
                .deleterIntraId(announcement.getDeleterIntraId())
                .createdAt(announcement.getCreatedAt())
                .deletedAt(announcement.getDeletedAt())
                .build();
    }
}
