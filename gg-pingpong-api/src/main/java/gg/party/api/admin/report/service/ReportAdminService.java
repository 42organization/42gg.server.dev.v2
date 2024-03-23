package gg.party.api.admin.report.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.party.CommentReport;
import gg.data.party.RoomReport;
import gg.data.party.UserReport;
import gg.party.api.admin.report.controller.request.ReportPageReqDto;
import gg.party.api.admin.report.controller.response.CommentReportAdminResDto;
import gg.party.api.admin.report.controller.response.CommentReportListResDto;
import gg.party.api.admin.report.controller.response.RoomReportAdminResDto;
import gg.party.api.admin.report.controller.response.RoomReportListResDto;
import gg.party.api.admin.report.controller.response.UserReportAdminResDto;
import gg.party.api.admin.report.controller.response.UserReportListResDto;
import gg.repo.party.CommentReportRepository;
import gg.repo.party.RoomReportRepository;
import gg.repo.party.UserReportRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportAdminService {
	private final CommentReportRepository commentReportRepository;
	private final RoomReportRepository roomReportRepository;
	private final UserReportRepository userReportRepository;

	/**
	 * 댓글 신고 전체 리스트를 조회한다
	 * @return 전체 댓글 신고 리스트 (시간순 정렬)
	 */
	@Transactional(readOnly = true)
	public CommentReportListResDto getCommentReports(ReportPageReqDto reportPageReqDto) {
		int page = reportPageReqDto.getPage();
		int size = reportPageReqDto.getSize();

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<CommentReport> commentReportPage = commentReportRepository.findAllWithFetchJoin(pageable);

		List<CommentReportAdminResDto> commentReportPageResDto = commentReportPage.getContent().stream()
			.map(CommentReportAdminResDto::new)
			.collect(Collectors.toList());

		return new CommentReportListResDto(commentReportPageResDto, commentReportPage.getTotalPages());
	}

	/**
	 * 방 신고 전체 리스트를 조회한다
	 * @return 전체 방 신고 리스트 (시간순 정렬)
	 */
	@Transactional(readOnly = true)
	public RoomReportListResDto getRoomReports(ReportPageReqDto reportPageReqDto) {
		int page = reportPageReqDto.getPage();
		int size = reportPageReqDto.getSize();

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<RoomReport> roomReportPage = roomReportRepository.findAllWithFetchJoin(pageable);

		List<RoomReportAdminResDto> roomReportPageResDto = roomReportPage.getContent().stream()
			.map(RoomReportAdminResDto::new)
			.collect(Collectors.toList());

		return new RoomReportListResDto(roomReportPageResDto, roomReportPage.getTotalPages());
	}

	/**
	 * 노쇼 신고 전체 리스트를 조회한다
	 * @return 전체 노쇼 신고 리스트 (시간순 정렬)
	 */
	@Transactional(readOnly = true)
	public UserReportListResDto getUserReports(ReportPageReqDto reportPageReqDto) {
		int page = reportPageReqDto.getPage();
		int size = reportPageReqDto.getSize();

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<UserReport> userReportPage = userReportRepository.findAllWithFetchJoin(pageable);

		List<UserReportAdminResDto> userReportPageResDto = userReportPage.getContent().stream()
			.map(UserReportAdminResDto::new)
			.collect(Collectors.toList());

		return new UserReportListResDto(userReportPageResDto, userReportPage.getTotalPages());
	}
}
