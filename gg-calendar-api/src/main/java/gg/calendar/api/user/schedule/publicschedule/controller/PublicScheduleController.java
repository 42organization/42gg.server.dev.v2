package gg.calendar.api.user.schedule.publicschedule.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.calendar.api.user.schedule.publicschedule.controller.request.PublicScheduleCreateEventReqDto;
import gg.calendar.api.user.schedule.publicschedule.controller.request.PublicScheduleCreateJobReqDto;
import gg.calendar.api.user.schedule.publicschedule.controller.request.PublicScheduleUpdateReqDto;
import gg.calendar.api.user.schedule.publicschedule.controller.response.PublicScheduleDetailRetrieveResDto;
import gg.calendar.api.user.schedule.publicschedule.controller.response.PublicSchedulePeriodRetrieveResDto;
import gg.calendar.api.user.schedule.publicschedule.controller.response.PublicScheduleUpdateResDto;
import gg.calendar.api.user.schedule.publicschedule.service.PublicScheduleService;
import gg.data.calendar.PublicSchedule;
import gg.utils.dto.ListResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar/public")
public class PublicScheduleController {
	private final PublicScheduleService publicScheduleService;

	@PostMapping("/event")
	public ResponseEntity<Void> publicScheduleCreateEvent(@RequestBody @Valid PublicScheduleCreateEventReqDto req,
		@Login @Parameter(hidden = true) UserDto userDto) {
		publicScheduleService.createEventPublicSchedule(req, userDto.getId());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/job")
	public ResponseEntity<Void> publicScheduleCreateJob(@RequestBody @Valid PublicScheduleCreateJobReqDto req,
		@Login @Parameter(hidden = true) UserDto userDto) {
		publicScheduleService.createJobPublicSchedule(req, userDto.getId());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<PublicScheduleUpdateResDto> publicScheduleUpdate(@PathVariable Long id,
		@RequestBody @Valid PublicScheduleUpdateReqDto req, @Login @Parameter(hidden = true) UserDto userDto) {
		PublicSchedule updateSchedule = publicScheduleService.updatePublicSchedule(id, req, userDto.getId());
		return ResponseEntity.ok(PublicScheduleUpdateResDto.toDto(updateSchedule));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Void> publicScheduleDelete(@PathVariable Long id,
		@Login @Parameter(hidden = true) UserDto userDto) {
		publicScheduleService.deletePublicSchedule(id, userDto.getId());
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<PublicScheduleDetailRetrieveResDto> publicScheduleDetailRetrieveGet(@PathVariable Long id,
		@Login @Parameter(hidden = true) UserDto userDto) {
		PublicSchedule publicSchedule = publicScheduleService.getPublicScheduleDetailRetrieve(id, userDto.getId());
		return ResponseEntity.ok(PublicScheduleDetailRetrieveResDto.toDto(publicSchedule));
	}

	@GetMapping
	public ResponseEntity<ListResponseDto<PublicSchedulePeriodRetrieveResDto>> publicSchedulePeriodRetrieveGet(
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
		LocalDateTime startTime = start.atStartOfDay();
		LocalDateTime endTime = end.atTime(LocalTime.MAX);
		List<PublicSchedulePeriodRetrieveResDto> res = publicScheduleService.retrieveNonPrivateSchedulePeriod(
			startTime, endTime);
		return ResponseEntity.ok(ListResponseDto.toDto(res));
	}

	@PostMapping("/{id}/{groupId}")
	public ResponseEntity<Void> publicScheduleToPrivateScheduleAdd(@PathVariable Long id, @PathVariable Long groupId,
		@Login @Parameter(hidden = true) UserDto userDto) {
		publicScheduleService.addPublicScheduleToPrivateSchedule(id, groupId, userDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}

