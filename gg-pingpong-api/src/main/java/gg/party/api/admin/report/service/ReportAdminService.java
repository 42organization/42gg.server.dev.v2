package gg.party.api.admin.report.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.party.api.admin.report.controller.response.CommentReportListAdminResDto;
import gg.party.api.admin.report.controller.response.RoomReportListAdminResDto;
import gg.party.api.admin.report.controller.response.UserReportListAdminResDto;
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
	public List<CommentReportListAdminResDto> getCommentReports() {
		return commentReportRepository.findAll(Sort.by(Sort.Direction.ASC, "createdAt"))
			.stream()
			.map(CommentReportListAdminResDto::new)
			.collect(Collectors.toList());
	}

	/**
	 * 방 신고 전체 리스트를 조회한다
	 * @return 전체 방 신고 리스트 (시간순 정렬)
	 */
	@Transactional(readOnly = true)
	public List<RoomReportListAdminResDto> getRoomReports() {
		return roomReportRepository.findAll(Sort.by(Sort.Direction.ASC, "createdAt"))
			.stream()
			.map(RoomReportListAdminResDto::new)
			.collect(Collectors.toList());
	}

	/**
	 * 노쇼 신고 전체 리스트를 조회한다
	 * @return 전체 노쇼 신고 리스트 (시간순 정렬)
	 */
	@Transactional(readOnly = true)
	public List<UserReportListAdminResDto> getUserReports() {
		return userReportRepository.findAll(Sort.by(Sort.Direction.ASC, "createdAt"))
			.stream()
			.map(UserReportListAdminResDto::new)
			.collect(Collectors.toList());
	}
}
