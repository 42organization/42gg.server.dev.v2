package com.gg.server.domain.announcement.dto;

import com.gg.server.domain.announcement.data.Announcement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementDto {
	private String content;

	public static AnnouncementDto from(Announcement announcement) {
		return new AnnouncementDto(announcement.getContent());
	}
}
