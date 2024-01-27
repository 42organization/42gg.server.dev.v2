package com.gg.server.admin.announcement.controller;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gg.server.admin.announcement.dto.AnnouncementAdminAddDto;
import com.gg.server.admin.announcement.dto.AnnouncementAdminListResponseDto;
import com.gg.server.admin.announcement.service.AnnouncementAdminService;
import com.gg.server.global.dto.PageRequestDto;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("pingpong/admin")
@Validated
public class AnnouncementAdminController {
	private final AnnouncementAdminService announcementAdminService;

	@GetMapping("/announcement")
	public ResponseEntity<AnnouncementAdminListResponseDto> getAnnouncementList(
		@ModelAttribute @Valid PageRequestDto anReq) {

		Pageable pageable = PageRequest.of(anReq.getPage() - 1, anReq.getSize(), Sort.by("createdAt").descending());

		return ResponseEntity.ok()
			.body(announcementAdminService.findAllAnnouncement(pageable));
	}

	@PostMapping("/announcement")
	public ResponseEntity addaAnnouncement(@Valid @RequestBody AnnouncementAdminAddDto addDto) {
		announcementAdminService.addAnnouncement(addDto);

		return new ResponseEntity(HttpStatus.CREATED);
	}

	@DeleteMapping("/announcement/{deleterIntraId}")
	public ResponseEntity announcementModify(@PathVariable String deleterIntraId) {
		announcementAdminService.modifyAnnouncementIsDel(deleterIntraId);

		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
