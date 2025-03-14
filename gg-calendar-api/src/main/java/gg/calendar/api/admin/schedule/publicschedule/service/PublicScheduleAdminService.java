package gg.calendar.api.admin.schedule.publicschedule.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.calendar.PrivateScheduleAdminRepository;
import gg.admin.repo.calendar.PublicScheduleAdminRepository;
import gg.calendar.api.admin.schedule.publicschedule.controller.request.PublicScheduleAdminCreateEventReqDto;
import gg.calendar.api.admin.schedule.publicschedule.controller.request.PublicScheduleAdminCreateJobReqDto;
import gg.calendar.api.admin.schedule.publicschedule.controller.request.PublicScheduleAdminUpdateReqDto;
import gg.calendar.api.admin.schedule.publicschedule.controller.response.PublicScheduleAdminResDto;
import gg.calendar.api.admin.schedule.publicschedule.controller.response.PublicScheduleAdminUpdateResDto;
import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.EventTag;
import gg.data.calendar.type.JobTag;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.calendar.type.TechTag;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.InvalidParameterException;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicScheduleAdminService {

	private final PublicScheduleAdminRepository publicScheduleAdminRepository;
	private final PrivateScheduleAdminRepository privateScheduleAdminRepository;

	@Transactional
	public void createPublicScheduleEvent(PublicScheduleAdminCreateEventReqDto publicScheduleAdminCreateEventReqDto) {

		dateTimeErrorCheck(publicScheduleAdminCreateEventReqDto.getStartTime(),
			publicScheduleAdminCreateEventReqDto.getEndTime());
		PublicSchedule publicSchedule = PublicScheduleAdminCreateEventReqDto.toEntity(
			publicScheduleAdminCreateEventReqDto);
		publicScheduleAdminRepository.save(publicSchedule);
	}

	@Transactional
	public void createPublicScheduleJob(PublicScheduleAdminCreateJobReqDto publicScheduleAdminCreateJobReqDto) {

		dateTimeErrorCheck(publicScheduleAdminCreateJobReqDto.getStartTime(),
			publicScheduleAdminCreateJobReqDto.getEndTime());
		PublicSchedule publicSchedule = PublicScheduleAdminCreateJobReqDto.toEntity(
			publicScheduleAdminCreateJobReqDto);
		publicScheduleAdminRepository.save(publicSchedule);
	}

	@Transactional
	public PublicScheduleAdminUpdateResDto updatePublicSchedule(
		PublicScheduleAdminUpdateReqDto publicScheduleAdminUpdateReqDto, Long id) {
		tagErrorCheck(publicScheduleAdminUpdateReqDto.getClassification(),
			publicScheduleAdminUpdateReqDto.getEventTag(),
			publicScheduleAdminUpdateReqDto.getJobTag(), publicScheduleAdminUpdateReqDto.getTechTag());

		dateTimeErrorCheck(publicScheduleAdminUpdateReqDto.getStartTime(),
			publicScheduleAdminUpdateReqDto.getEndTime());

		PublicSchedule publicSchedule = publicScheduleAdminRepository.findById(id)
			.orElseThrow(() -> new NotExistException(ErrorCode.PUBLIC_SCHEDULE_NOT_FOUND));

		publicSchedule.update(publicScheduleAdminUpdateReqDto.getClassification(),
			publicScheduleAdminUpdateReqDto.getEventTag(), publicScheduleAdminUpdateReqDto.getJobTag(),
			publicScheduleAdminUpdateReqDto.getTechTag(), publicScheduleAdminUpdateReqDto.getTitle(),
			publicScheduleAdminUpdateReqDto.getContent(), publicScheduleAdminUpdateReqDto.getLink(),
			publicScheduleAdminUpdateReqDto.getStartTime(), publicScheduleAdminUpdateReqDto.getEndTime(),
			checkStatus(publicScheduleAdminUpdateReqDto.getEndTime()));

		return PublicScheduleAdminUpdateResDto.toDto(publicSchedule);
	}

	@Transactional
	public void deletePublicSchedule(Long id) {

		PublicSchedule publicSchedule = publicScheduleAdminRepository.findById(id)
			.orElseThrow(() -> new NotExistException(ErrorCode.PUBLIC_SCHEDULE_NOT_FOUND));
		List<PrivateSchedule> privateSchedules = privateScheduleAdminRepository.findByPublicScheduleId(
			publicSchedule.getId());
		publicSchedule.delete();
		for (PrivateSchedule privateSchedule : privateSchedules) {
			privateSchedule.delete();
		}
	}

	public PublicScheduleAdminResDto detailPublicSchedule(Long id) {
		PublicSchedule publicSchedule = publicScheduleAdminRepository.findById(id)
			.orElseThrow(() -> new NotExistException(ErrorCode.PUBLIC_SCHEDULE_NOT_FOUND));

		return new PublicScheduleAdminResDto(publicSchedule);
	}

	private void dateTimeErrorCheck(LocalDateTime startTime, LocalDateTime endTime) {
		if (startTime.isAfter(endTime)) {
			throw new InvalidParameterException(ErrorCode.CALENDAR_BEFORE_DATE);
		}
	}

	private void tagErrorCheck(DetailClassification classification, EventTag eventTag, JobTag jobTag, TechTag techTag) {
		if (!classification.isValid(eventTag, jobTag, techTag)) {
			throw new InvalidParameterException(ErrorCode.CLASSIFICATION_NOT_MATCH);
		}
	}

	private ScheduleStatus checkStatus(LocalDateTime endTime) {
		return endTime.isBefore(LocalDateTime.now()) ? ScheduleStatus.DEACTIVATE : ScheduleStatus.ACTIVATE;
	}

}
