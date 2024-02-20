package gg.pingpong.api.admin.announcement.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AnnouncementAdminListResponseDto {
	private List<AnnouncementAdminResponseDto> announcementList;
	private int totalPage;
}
