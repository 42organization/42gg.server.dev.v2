package gg.pingpong.api.admin.announcement.controller;

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

import gg.pingpong.api.admin.announcement.controller.response.AnnouncementAdminListResponseDto;
import gg.pingpong.api.admin.announcement.dto.AnnouncementAdminAddDto;
import gg.pingpong.api.admin.announcement.service.AnnouncementAdminService;
import gg.pingpong.api.global.dto.PageRequestDto;
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
	public ResponseEntity<Void> addAnnouncement(@Valid @RequestBody AnnouncementAdminAddDto addDto) {
		announcementAdminService.addAnnouncement(addDto);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/announcement/{deleterIntraId}")
	public ResponseEntity<Void> announcementModify(@PathVariable String deleterIntraId) {
		announcementAdminService.modifyAnnouncementIsDel(deleterIntraId);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
