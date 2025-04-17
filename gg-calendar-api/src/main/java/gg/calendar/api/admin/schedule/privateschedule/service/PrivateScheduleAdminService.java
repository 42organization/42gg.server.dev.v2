package gg.calendar.api.admin.schedule.privateschedule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.calendar.PrivateScheduleAdminRepository;
import gg.admin.repo.calendar.ScheduleGroupAdminRepository;
import gg.calendar.api.admin.schedule.privateschedule.controller.response.PrivateScheduleAdminDetailResDto;
import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.ScheduleGroup;
import gg.data.calendar.type.DetailClassification;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateScheduleAdminService {

	private final PrivateScheduleAdminRepository privateScheduleAdminRepository;

	private final ScheduleGroupAdminRepository scheduleGroupAdminRepository;

	public PrivateScheduleAdminDetailResDto detailPrivateSchedule(Long id) {
		PrivateSchedule privateSchedule = privateScheduleAdminRepository.findById(id)
			.orElseThrow(() -> new NotExistException(ErrorCode.PRIVATE_SCHEDULE_NOT_FOUND));
		ScheduleGroup scheduleGroup = scheduleGroupAdminRepository.findById(
				privateSchedule.getGroupId())
			.orElseThrow(() -> new NotExistException(ErrorCode.SCHEDULE_GROUP_NOT_FOUND));
		return PrivateScheduleAdminDetailResDto.toDto(privateSchedule, scheduleGroup);
	}

	@Transactional
	public void deletePrivateSchedule(Long id) {
		PrivateSchedule privateSchedule = privateScheduleAdminRepository.findById(id)
			.orElseThrow(() -> new NotExistException(ErrorCode.PRIVATE_SCHEDULE_NOT_FOUND));
		validateDetailClassification(privateSchedule.getPublicSchedule().getClassification());
		privateSchedule.deleteCascade();
	}

	public void validateDetailClassification(DetailClassification classification) {
		if (classification != DetailClassification.PRIVATE_SCHEDULE) {
			throw new ForbiddenException(ErrorCode.CLASSIFICATION_NO_PRIVATE);
		}
	}
}
