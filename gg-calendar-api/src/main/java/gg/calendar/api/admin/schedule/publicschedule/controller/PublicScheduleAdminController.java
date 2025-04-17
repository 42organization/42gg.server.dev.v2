package gg.calendar.api.admin.schedule.publicschedule.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.calendar.api.admin.schedule.publicschedule.controller.request.PublicScheduleAdminCreateEventReqDto;
import gg.calendar.api.admin.schedule.publicschedule.controller.request.PublicScheduleAdminCreateJobReqDto;
import gg.calendar.api.admin.schedule.publicschedule.controller.request.PublicScheduleAdminUpdateReqDto;
import gg.calendar.api.admin.schedule.publicschedule.controller.response.PublicScheduleAdminResDto;
import gg.calendar.api.admin.schedule.publicschedule.controller.response.PublicScheduleAdminUpdateResDto;
import gg.calendar.api.admin.schedule.publicschedule.service.PublicScheduleAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar/admin/public")
public class PublicScheduleAdminController {

	private final PublicScheduleAdminService publicScheduleAdminService;

	@PostMapping("/event")
	public ResponseEntity<Void> publicScheduleEventCreate(
		@RequestBody @Valid PublicScheduleAdminCreateEventReqDto publicScheduleAdminCreateEventReqDto) {
		publicScheduleAdminService.createPublicScheduleEvent(publicScheduleAdminCreateEventReqDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/job")
	public ResponseEntity<Void> publicScheduleJobCreate(
		@RequestBody @Valid PublicScheduleAdminCreateJobReqDto publicScheduleAdminCreateJobReqDto) {
		publicScheduleAdminService.createPublicScheduleJob(publicScheduleAdminCreateJobReqDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<PublicScheduleAdminUpdateResDto> publicScheduleUpdate(
		@RequestBody @Valid PublicScheduleAdminUpdateReqDto publicScheduleAdminUpdateReqDto,
		@PathVariable Long id) {
		PublicScheduleAdminUpdateResDto publicScheduleAdminUpdateRes = publicScheduleAdminService.updatePublicSchedule(
			publicScheduleAdminUpdateReqDto, id);
		return ResponseEntity.ok(publicScheduleAdminUpdateRes);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Void> publicScheduleDelete(@PathVariable Long id) {
		publicScheduleAdminService.deletePublicSchedule(id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<PublicScheduleAdminResDto> publicScheduleDetail(@PathVariable Long id) {
		PublicScheduleAdminResDto publicScheduleAdminResDto = publicScheduleAdminService.detailPublicSchedule(id);
		return ResponseEntity.ok(publicScheduleAdminResDto);
	}
}
