package gg.calendar.api.user.schedule.publicschedule.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.auth.UserDto;
import gg.calendar.api.user.schedule.publicschedule.controller.request.PublicScheduleCreateEventReqDto;
import gg.calendar.api.user.schedule.publicschedule.controller.request.PublicScheduleCreateJobReqDto;
import gg.calendar.api.user.schedule.publicschedule.controller.request.PublicScheduleUpdateReqDto;
import gg.calendar.api.user.schedule.publicschedule.controller.response.PublicSchedulePeriodRetrieveResDto;
import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.EventTag;
import gg.data.calendar.type.JobTag;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.calendar.type.TechTag;
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
public class PublicScheduleService {
	private final PublicScheduleRepository publicScheduleRepository;
	private final UserRepository userRepository;
	private final PrivateScheduleRepository privateScheduleRepository;
	private final ScheduleGroupRepository scheduleGroupRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void createEventPublicSchedule(PublicScheduleCreateEventReqDto req, Long userId) {
		User user = userRepository.getById(userId);
		checkAuthor(req.getAuthor(), user);
		validateTimeRange(req.getStartTime(), req.getEndTime());
		PublicSchedule eventPublicSchedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(), req,
			checkStatus(req.getEndTime()));
		publicScheduleRepository.save(eventPublicSchedule);
	}

	@Transactional
	public void createJobPublicSchedule(PublicScheduleCreateJobReqDto req, Long userId) {
		User user = userRepository.getById(userId);
		checkAuthor(req.getAuthor(), user);
		validateTimeRange(req.getStartTime(), req.getEndTime());
		PublicSchedule jobPublicSchedule = PublicScheduleCreateJobReqDto.toEntity(user.getIntraId(), req,
			checkStatus(req.getEndTime()));
		publicScheduleRepository.save(jobPublicSchedule);
	}

	@Transactional
	public PublicSchedule updatePublicSchedule(Long scheduleId, PublicScheduleUpdateReqDto req, Long userId) {
		tagErrorCheck(req.getClassification(), req.getEventTag(), req.getJobTag(), req.getTechTag());
		User user = userRepository.getById(userId);
		PublicSchedule existingSchedule = publicScheduleRepository.findByIdAndStatusNot(scheduleId,
				ScheduleStatus.DELETE)
			.orElseThrow(() -> new NotExistException(ErrorCode.PUBLIC_SCHEDULE_NOT_FOUND));
		checkAuthor(existingSchedule.getAuthor(), user);
		checkAuthor(req.getAuthor(), user);
		validateTimeRange(req.getStartTime(), req.getEndTime());

		// PublicSchedule 업데이트
		existingSchedule.update(req.getClassification(), req.getEventTag(), req.getJobTag(), req.getTechTag(),
			req.getTitle(), req.getContent(), req.getLink(), req.getStartTime(), req.getEndTime(),
			checkStatus(req.getEndTime()));

		// 벌크 업데이트 적용 (SELECT 없이 바로 UPDATE 실행)
		ScheduleStatus status = checkStatus(req.getEndTime());
		privateScheduleRepository.bulkUpdateScheduleStatus(existingSchedule, status);

		return existingSchedule;
	}

	@Transactional
	public void deletePublicSchedule(Long scheduleId, Long userId) {
		User user = userRepository.getById(userId);
		PublicSchedule existingSchedule = publicScheduleRepository.findByIdAndStatusNot(scheduleId,
			ScheduleStatus.DELETE).orElseThrow(() -> new NotExistException(ErrorCode.PUBLIC_SCHEDULE_NOT_FOUND));
		checkAuthor(existingSchedule.getAuthor(), user);

		List<PrivateSchedule> privateSchedules = privateScheduleRepository.findByPublicSchedule(existingSchedule);
		existingSchedule.delete();
		if (!privateSchedules.isEmpty()) {
			for (PrivateSchedule privateSchedule : privateSchedules) {
				privateSchedule.delete();
			}
		}
	}

	public PublicSchedule getPublicScheduleDetailRetrieve(Long scheduleId, Long userId) {
		User user = userRepository.getById(userId);
		PublicSchedule publicRetrieveSchedule = publicScheduleRepository.findByIdAndStatusNot(scheduleId,
			ScheduleStatus.DELETE).orElseThrow(() -> new NotExistException(ErrorCode.PUBLIC_SCHEDULE_NOT_FOUND));
		return publicRetrieveSchedule;
	}

	public List<PublicSchedulePeriodRetrieveResDto> retrieveNonPrivateSchedulePeriod(LocalDateTime start,
		LocalDateTime end) {
		validateTimeRange(start, end);
		List<PublicSchedule> nonPrivateSchedules = publicScheduleRepository
			.findByStartTimeGreaterThanEqualAndStartTimeLessThanAndClassificationNotAndStatusNot(
				start, end, DetailClassification.PRIVATE_SCHEDULE, ScheduleStatus.DELETE);
		return nonPrivateSchedules.stream().map(PublicSchedulePeriodRetrieveResDto::toDto).collect(Collectors.toList());
	}

	@Transactional
	public void addPublicScheduleToPrivateSchedule(Long scheduleId, Long groupId, UserDto userDto) {
		User user = userRepository.getById(userDto.getId());
		Long userId = userDto.getId();
		PublicSchedule publicSchedule = publicScheduleRepository.findByIdAndStatusNot(scheduleId, ScheduleStatus.DELETE)
			.orElseThrow(() -> new NotExistException(ErrorCode.PUBLIC_SCHEDULE_NOT_FOUND));
		scheduleGroupRepository.findByIdAndUserId(groupId, userId)
			.orElseThrow(() -> new NotExistException(ErrorCode.SCHEDULE_GROUP_NOT_FOUND));
		PrivateSchedule privateSchedule = new PrivateSchedule(user, publicSchedule, false, groupId,
			publicSchedule.getStatus());
		privateScheduleRepository.save(privateSchedule);
		entityManager.flush();
		publicScheduleRepository.incrementSharedCount(publicSchedule.getId());
	}

	private void checkAuthor(String author, User user) {
		if (!user.getIntraId().equals(author)) {
			throw new ForbiddenException(ErrorCode.CALENDAR_AUTHOR_NOT_MATCH);
		}
	}

	private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
		if (endTime.isBefore(startTime)) {
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
