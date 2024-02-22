package gg.pingpong.api.admin.announcement.controller.response;

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
