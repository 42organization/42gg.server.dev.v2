package gg.calendar.api.admin.schedule.totalschedule.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.calendar.api.admin.schedule.totalschedule.controller.request.TotalScheduleAdminSearchReqDto;
import gg.calendar.api.admin.schedule.totalschedule.controller.response.TotalScheduleAdminResDto;
import gg.calendar.api.admin.schedule.totalschedule.controller.response.TotalScheduleAdminSearchListResDto;
import gg.calendar.api.admin.schedule.totalschedule.service.TotalScheduleAdminService;
import gg.data.calendar.type.DetailClassification;
import gg.utils.dto.PageRequestDto;
import gg.utils.dto.PageResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/calendar")
public class TotalScheduleAdminController {

	private final TotalScheduleAdminService totalScheduleAdminService;

	@GetMapping("/list/{detailClassification}")
	public ResponseEntity<TotalScheduleAdminSearchListResDto> totalScheduleAdminClassificationList(
		@PathVariable DetailClassification detailClassification) {
		TotalScheduleAdminSearchListResDto scheduleList = totalScheduleAdminService.findAllByClassification(
			detailClassification);
		return ResponseEntity.ok(scheduleList);
	}

	@GetMapping("/search")
	public ResponseEntity<TotalScheduleAdminSearchListResDto> totalScheduleAdminSearchList(
		@ModelAttribute @Valid TotalScheduleAdminSearchReqDto totalScheduleAdminSearchReqDto) {
		TotalScheduleAdminSearchListResDto scheduleList = totalScheduleAdminService.searchTotalScheduleAdminList(
			totalScheduleAdminSearchReqDto);

		return ResponseEntity.ok(scheduleList);
	}

	// @GetMapping("/total")
	// public ResponseEntity<TotalScheduleAdminSearchListResDto> totalScheduleAdminList() {
	// 	TotalScheduleAdminSearchListResDto scheduleList = totalScheduleAdminService.totalScheduleAdminList();
	//
	// 	return ResponseEntity.ok(scheduleList);
	// }

	@GetMapping("/total")
	public PageResponseDto<TotalScheduleAdminResDto> totalScheduleAdminList(
		@ModelAttribute @Valid PageRequestDto pageRequest) {
		int page = pageRequest.getPage();
		int size = 10;
		PageResponseDto<TotalScheduleAdminResDto> scheduleList = totalScheduleAdminService.findAll(page, size);

		return scheduleList;
	}

}
