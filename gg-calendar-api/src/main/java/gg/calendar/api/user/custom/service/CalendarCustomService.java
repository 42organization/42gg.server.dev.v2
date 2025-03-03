package gg.calendar.api.user.custom.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.auth.UserDto;
import gg.calendar.api.user.custom.controller.request.CalendarCustomCreateReqDto;
import gg.calendar.api.user.custom.controller.request.CalendarCustomUpdateReqDto;
import gg.calendar.api.user.custom.controller.response.CalendarCustomUpdateResDto;
import gg.calendar.api.user.custom.controller.response.CalendarCustomViewResDto;
import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.ScheduleGroup;
import gg.data.calendar.type.DetailClassification;
import gg.data.user.User;
import gg.repo.calendar.PrivateScheduleRepository;
import gg.repo.calendar.ScheduleGroupRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CalendarCustomService {
	private final ScheduleGroupRepository scheduleGroupRepository;
	private final UserRepository userRepository;
	private final PrivateScheduleRepository privateScheduleRepository;

	@Transactional
	public void createScheduleGroup(UserDto userDto, CalendarCustomCreateReqDto calendarCustomCreateReqDto) {
		User user = userRepository.getById(userDto.getId());
		ScheduleGroup scheduleGroup = CalendarCustomCreateReqDto.toEntity(user, calendarCustomCreateReqDto);
		scheduleGroupRepository.save(scheduleGroup);
	}

	@Transactional
	public CalendarCustomUpdateResDto updateScheduleGroup(UserDto userDto,
		CalendarCustomUpdateReqDto calendarCustomUpdateReqDto, Long scheduleGroupId) {
		ScheduleGroup scheduleGroup = scheduleGroupRepository.findByIdAndUserId(scheduleGroupId, userDto.getId())
			.orElseThrow(() -> new NotExistException(ErrorCode.SCHEDULE_GROUP_NOT_FOUND));
		scheduleGroup.update(calendarCustomUpdateReqDto.getTitle(), calendarCustomUpdateReqDto.getBackgroundColor());
		return CalendarCustomUpdateResDto.toDto(scheduleGroup);
	}

	public List<CalendarCustomViewResDto> getScheduleGroupView(UserDto userDto) {
		return scheduleGroupRepository.findByUserId(userDto.getId())
			.stream()
			.map(CalendarCustomViewResDto::toDto)
			.collect(Collectors.toList());
	}

	@Transactional
	public void deleteScheduleGroup(UserDto userDto, Long scheduleGroupId) {
		ScheduleGroup scheduleGroup = scheduleGroupRepository.findByIdAndUserId(scheduleGroupId, userDto.getId())
			.orElseThrow(() -> new NotExistException(ErrorCode.SCHEDULE_GROUP_NOT_FOUND));

		List<PrivateSchedule> privateSchedules = privateScheduleRepository.findByGroupId(scheduleGroupId);
		for (PrivateSchedule privateSchedule : privateSchedules) {
			if (privateSchedule.getPublicSchedule().getClassification().equals(DetailClassification.PRIVATE_SCHEDULE)) {
				privateSchedule.deleteCascade();
			} else {
				privateSchedule.delete();
			}
			scheduleGroupRepository.delete(scheduleGroup);
		}
	}
}
