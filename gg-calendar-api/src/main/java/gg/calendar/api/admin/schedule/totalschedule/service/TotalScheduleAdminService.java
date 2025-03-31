package gg.calendar.api.admin.schedule.totalschedule.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.calendar.PublicScheduleAdminRepository;
import gg.calendar.api.admin.schedule.totalschedule.controller.request.TotalScheduleAdminSearchReqDto;
import gg.calendar.api.admin.schedule.totalschedule.controller.response.TotalScheduleAdminResDto;
import gg.calendar.api.admin.schedule.totalschedule.controller.response.TotalScheduleAdminSearchListResDto;
import gg.calendar.api.admin.util.TotalScheduleAdminSpecification;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.type.DetailClassification;
import gg.utils.dto.PageResponseDto;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.InvalidParameterException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TotalScheduleAdminService {

	private final PublicScheduleAdminRepository publicScheduleAdminRepository;

	public TotalScheduleAdminSearchListResDto findAllByClassification(DetailClassification detailClassification) {

		List<PublicSchedule> scheduleList = publicScheduleAdminRepository.findAllByClassificationOrderByIdDesc(
			detailClassification);

		return TotalScheduleAdminSearchListResDto.builder()
			.schedules(scheduleList.stream()
				.map(TotalScheduleAdminResDto::new)
				.collect(Collectors.toList()))
			.build();
	}

	public PageResponseDto<TotalScheduleAdminResDto> findAll(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("id")));

		Page<PublicSchedule> publicSchedules = publicScheduleAdminRepository.findAll(pageable);

		List<TotalScheduleAdminResDto> publicScheduleList = publicSchedules.stream()
			.map(TotalScheduleAdminResDto::new)
			.collect(Collectors.toList());
		return PageResponseDto.of(publicSchedules.getTotalElements(), publicScheduleList);
	}

	public TotalScheduleAdminSearchListResDto searchTotalScheduleAdminList(TotalScheduleAdminSearchReqDto reqDto) {
		dateTimeErrorCheck(reqDto.getStartTime(), reqDto.getEndTime());

		Map<String, Function<PublicSchedule, String>> fieldExtractor = Map.of(
			"title", PublicSchedule::getTitle,
			"content", PublicSchedule::getContent,
			"author", PublicSchedule::getAuthor,
			"classification", schedule -> schedule.getClassification().name()
		);

		Function<PublicSchedule, String> extractor = fieldExtractor.get(reqDto.getType());
		if (extractor == null) {
			throw new IllegalArgumentException("Invalid type: " + reqDto.getType());
		}

		Specification<PublicSchedule> specification = TotalScheduleAdminSpecification.searchByField(
			reqDto.getContent(),
			reqDto.getStartTime(),
			reqDto.getEndTime(),
			reqDto.getType()
		);

		List<PublicSchedule> schedules = publicScheduleAdminRepository.findAll(specification,
			Sort.by(Sort.Order.desc("id")));
		return TotalScheduleAdminSearchListResDto.builder()
			.schedules(schedules.stream()
				.map(TotalScheduleAdminResDto::new)
				.collect(Collectors.toList()))
			.build();
	}

	public TotalScheduleAdminSearchListResDto totalScheduleAdminList() {
		List<PublicSchedule> schedules = publicScheduleAdminRepository.findAll();

		return TotalScheduleAdminSearchListResDto.builder()
			.schedules(schedules.stream()
				.map(TotalScheduleAdminResDto::new)
				.collect(Collectors.toList()))
			.build();
	}

	private void dateTimeErrorCheck(LocalDate startTime, LocalDate endTime) {
		if (startTime.isAfter(endTime)) {
			throw new InvalidParameterException(ErrorCode.CALENDAR_BEFORE_DATE);
		}
	}
}
