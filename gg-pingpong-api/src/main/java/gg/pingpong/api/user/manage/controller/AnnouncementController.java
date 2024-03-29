package gg.pingpong.api.user.manage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.pingpong.api.user.manage.controller.response.AnnouncementResponseDto;
import gg.pingpong.api.user.manage.service.AnnouncementService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/pingpong/announcement")
public class AnnouncementController {
	private final AnnouncementService announcementService;

	@GetMapping
	public AnnouncementResponseDto findLastAnnounceContent() {
		return new AnnouncementResponseDto(announcementService.findLastAnnouncement().getContent());
	}
}
