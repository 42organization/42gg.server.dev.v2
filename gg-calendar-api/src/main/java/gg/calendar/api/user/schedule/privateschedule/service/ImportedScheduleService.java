package gg.calendar.api.user.schedule.privateschedule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.auth.UserDto;
import gg.calendar.api.user.schedule.privateschedule.controller.request.ImportedScheduleUpdateReqDto;
import gg.calendar.api.user.schedule.privateschedule.controller.response.ImportedScheduleUpdateResDto;
import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.ScheduleGroup;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.ScheduleStatus;
import gg.repo.calendar.PrivateScheduleRepository;
import gg.repo.calendar.ScheduleGroupRepository;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImportedScheduleService {
	private final PrivateScheduleRepository privateScheduleRepository;
	private final ScheduleGroupRepository scheduleGroupRepository;

	@Transactional
	public ImportedScheduleUpdateResDto updateImportedSchedule(UserDto userDto,
		ImportedScheduleUpdateReqDto importedScheduleUpdateReqDto, Long privateScheduleId) {
		PrivateSchedule privateSchedule = privateScheduleRepository.findByIdAndStatusNot(privateScheduleId,
			ScheduleStatus.DELETE).orElseThrow(() -> new NotExistException(ErrorCode.PRIVATE_SCHEDULE_NOT_FOUND));
		validateAuthor(userDto.getIntraId(), privateSchedule.getUser().getIntraId());
		ScheduleGroup scheduleGroup = scheduleGroupRepository.findById(importedScheduleUpdateReqDto.getGroupId())
			.orElseThrow(() -> new NotExistException(ErrorCode.SCHEDULE_GROUP_NOT_FOUND));

		privateSchedule.update(importedScheduleUpdateReqDto.isAlarm(), scheduleGroup.getId());
		return ImportedScheduleUpdateResDto.toDto(privateSchedule);
	}

	@Transactional
	public void deleteImportedSchedule(UserDto userDto, Long privateScheduleId) {
		PrivateSchedule privateSchedule = privateScheduleRepository.findById(privateScheduleId)
			.orElseThrow(() -> new NotExistException(ErrorCode.PRIVATE_SCHEDULE_NOT_FOUND));
		validateAuthor(userDto.getIntraId(), privateSchedule.getUser().getIntraId());
		validateDetailClassification(privateSchedule.getPublicSchedule().getClassification());

		privateSchedule.delete();
	}

	public void validateAuthor(String intraId, String author) {
		if (!intraId.equals(author)) {
			throw new ForbiddenException(ErrorCode.CALENDAR_AUTHOR_NOT_MATCH);
		}
	}

	public void validateDetailClassification(DetailClassification classification) {
		if (classification == DetailClassification.PRIVATE_SCHEDULE) {
			throw new ForbiddenException(ErrorCode.CLASSIFICATION_NO_PRIVATE);
		}
	}
}
