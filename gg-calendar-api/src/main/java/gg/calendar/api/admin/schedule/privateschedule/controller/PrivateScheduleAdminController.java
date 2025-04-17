package gg.calendar.api.admin.schedule.privateschedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.calendar.api.admin.schedule.privateschedule.controller.response.PrivateScheduleAdminDetailResDto;
import gg.calendar.api.admin.schedule.privateschedule.service.PrivateScheduleAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar/admin/private")
public class PrivateScheduleAdminController {

	private final PrivateScheduleAdminService privateScheduleAdminService;

	@GetMapping("/{id}")
	public ResponseEntity<PrivateScheduleAdminDetailResDto> privateScheduleDetail(@PathVariable Long id) {
		PrivateScheduleAdminDetailResDto responseDto = privateScheduleAdminService.detailPrivateSchedule(id);
		return ResponseEntity.ok(responseDto);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Void> privateScheduleDelete(@PathVariable Long id) {
		privateScheduleAdminService.deletePrivateSchedule(id);
		return ResponseEntity.ok().build();
	}
}
