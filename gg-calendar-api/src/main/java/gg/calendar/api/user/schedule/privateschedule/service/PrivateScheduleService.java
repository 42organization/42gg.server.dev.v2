package gg.calendar.api.user.schedule.privateschedule.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.auth.UserDto;
import gg.calendar.api.user.schedule.privateschedule.controller.request.PrivateScheduleCreateReqDto;
import gg.calendar.api.user.schedule.privateschedule.controller.request.PrivateScheduleUpdateReqDto;
import gg.calendar.api.user.schedule.privateschedule.controller.response.PrivateScheduleDetailResDto;
import gg.calendar.api.user.schedule.privateschedule.controller.response.PrivateSchedulePeriodResDto;
import gg.calendar.api.user.schedule.privateschedule.controller.response.PrivateScheduleUpdateResDto;
import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.ScheduleGroup;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.user.User;
import gg.repo.calendar.PrivateScheduleRepository;
import gg.repo.calendar.PublicScheduleRepository;
import gg.repo.calendar.ScheduleGroupRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.InvalidParameterException;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateScheduleService {
	private final PrivateScheduleRepository privateScheduleRepository;
	private final PublicScheduleRepository publicScheduleRepository;
	private final ScheduleGroupRepository scheduleGroupRepository;
	private final UserRepository userRepository;

	@Transactional
	public void createPrivateSchedule(UserDto userDto, PrivateScheduleCreateReqDto privateScheduleCreateReqDto) {
		validateTimeRange(privateScheduleCreateReqDto.getStartTime(), privateScheduleCreateReqDto.getEndTime());
		PublicSchedule publicSchedule = PrivateScheduleCreateReqDto.toEntity(userDto.getIntraId(),
			privateScheduleCreateReqDto);
		publicScheduleRepository.save(publicSchedule);
		ScheduleGroup scheduleGroup = scheduleGroupRepository.findById(privateScheduleCreateReqDto.getGroupId())
			.orElseThrow(() -> new NotExistException(ErrorCode.SCHEDULE_GROUP_NOT_FOUND));
		User user = userRepository.getById(userDto.getId());
		PrivateSchedule privateSchedule = new PrivateSchedule(user, publicSchedule,
			privateScheduleCreateReqDto.isAlarm(), scheduleGroup.getId(), publicSchedule.getStatus());

		privateScheduleRepository.save(privateSchedule);
	}

	@Transactional
	public PrivateScheduleUpdateResDto updatePrivateSchedule(UserDto userDto,
		PrivateScheduleUpdateReqDto privateScheduleUpdateReqDto, Long privateScheduleId) {
		validateTimeRange(privateScheduleUpdateReqDto.getStartTime(), privateScheduleUpdateReqDto.getEndTime());
		PrivateSchedule privateSchedule = privateScheduleRepository.findById(privateScheduleId)
			.orElseThrow(() -> new NotExistException(ErrorCode.PRIVATE_SCHEDULE_NOT_FOUND));
		validateAuthor(userDto.getIntraId(), privateSchedule.getPublicSchedule().getAuthor());
		ScheduleGroup scheduleGroup = scheduleGroupRepository.findById(privateScheduleUpdateReqDto.getGroupId())
			.orElseThrow(() -> new NotExistException(ErrorCode.SCHEDULE_GROUP_NOT_FOUND));

		privateSchedule.updateCascade(privateScheduleUpdateReqDto.getTitle(), privateScheduleUpdateReqDto.getContent(),
			privateScheduleUpdateReqDto.getLink(), privateScheduleUpdateReqDto.getStartTime(),
			privateScheduleUpdateReqDto.getEndTime(), privateScheduleUpdateReqDto.isAlarm(), scheduleGroup.getId(),
			checkStatus(privateScheduleUpdateReqDto.getEndTime()));
		return PrivateScheduleUpdateResDto.toDto(privateSchedule);
	}

	@Transactional
	public void deletePrivateSchedule(UserDto userDto, Long privateScheduleId) {
		PrivateSchedule privateSchedule = privateScheduleRepository.findById(privateScheduleId)
			.orElseThrow(() -> new NotExistException(ErrorCode.PRIVATE_SCHEDULE_NOT_FOUND));
		validateAuthor(userDto.getIntraId(), privateSchedule.getPublicSchedule().getAuthor());
		validateDetailClassification(privateSchedule.getPublicSchedule().getClassification());

		privateSchedule.deleteCascade();
	}

	public PrivateScheduleDetailResDto getPrivateScheduleDetail(UserDto userDto, Long privateScheduleId) {
		PrivateSchedule privateSchedule = privateScheduleRepository.findByIdAndStatusNot(privateScheduleId,
			ScheduleStatus.DELETE).orElseThrow(() -> new NotExistException(ErrorCode.PRIVATE_SCHEDULE_NOT_FOUND));
		ScheduleGroup scheduleGroup = scheduleGroupRepository.findById(privateSchedule.getGroupId())
			.orElseThrow(() -> new NotExistException(ErrorCode.SCHEDULE_GROUP_NOT_FOUND));
		validateAuthor(userDto.getIntraId(), privateSchedule.getUser().getIntraId());
		return PrivateScheduleDetailResDto.toDto(privateSchedule, scheduleGroup);
	}

	public List<PrivateSchedulePeriodResDto> getPrivateSchedulePeriod(UserDto userDto, LocalDateTime startTime,
		LocalDateTime endTime) {
		validateTimeRange(startTime, endTime);
		User user = userRepository.getById(userDto.getId());
		List<PrivateSchedule> privateSchedules = privateScheduleRepository.findOverlappingSchedulesByUser(startTime,
			endTime, user);
		Map<Long, ScheduleGroup> scheduleGroups = scheduleGroupRepository.findByUserId(userDto.getId())
			.stream()
			.collect(Collectors.toMap(ScheduleGroup::getId, Function.identity()));

		List<PrivateSchedulePeriodResDto> response = new ArrayList<>();

		for (PrivateSchedule privateSchedule : privateSchedules) {
			ScheduleGroup scheduleGroup = scheduleGroups.get(privateSchedule.getGroupId());
			if (scheduleGroup == null) {
				throw new NotExistException(ErrorCode.SCHEDULE_GROUP_NOT_FOUND);
			}
			response.add(PrivateSchedulePeriodResDto.toDto(privateSchedule, scheduleGroup));
		}
		return response;
	}

	public void validateDetailClassification(DetailClassification classification) {
		if (classification != DetailClassification.PRIVATE_SCHEDULE) {
			throw new ForbiddenException(ErrorCode.CLASSIFICATION_NO_PRIVATE);
		}
	}

	public void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
		if (endTime.isBefore(startTime)) {
			throw new InvalidParameterException(ErrorCode.CALENDAR_BEFORE_DATE);
		}
	}

	public void validateAuthor(String intraId, String author) {
		if (!intraId.equals(author)) {
			throw new ForbiddenException(ErrorCode.CALENDAR_AUTHOR_NOT_MATCH);
		}
	}

	private ScheduleStatus checkStatus(LocalDateTime endTime) {
		return endTime.isBefore(LocalDateTime.now()) ? ScheduleStatus.DEACTIVATE : ScheduleStatus.ACTIVATE;
	}
}
