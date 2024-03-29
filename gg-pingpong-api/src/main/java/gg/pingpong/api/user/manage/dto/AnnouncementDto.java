package gg.pingpong.api.user.manage.dto;

import gg.data.manage.Announcement;
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
