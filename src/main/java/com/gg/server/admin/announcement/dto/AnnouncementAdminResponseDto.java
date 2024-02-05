package com.gg.server.admin.announcement.dto;

import java.time.LocalDateTime;

import com.gg.server.data.manage.Announcement;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnnouncementAdminResponseDto {
	private String creatorIntraId;
	private String deleterIntraId;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
	private LocalDateTime deletedAt;

	public AnnouncementAdminResponseDto(Announcement announcementAdmin) {
		this.content = announcementAdmin.getContent();
		this.creatorIntraId = announcementAdmin.getCreatorIntraId();
		this.deleterIntraId = announcementAdmin.getDeleterIntraId();
		this.createdAt = announcementAdmin.getCreatedAt();
		this.modifiedAt = announcementAdmin.getModifiedAt();
		this.deletedAt = announcementAdmin.getDeletedAt();
	}
}
