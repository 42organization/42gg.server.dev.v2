package gg.party.api.user.report.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import gg.auth.UserDto;
import gg.data.party.Comment;
import gg.data.party.CommentReport;
import gg.data.party.PartyPenalty;
import gg.data.party.Room;
import gg.data.party.RoomReport;
import gg.data.party.UserReport;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import gg.party.api.user.report.controller.request.ReportReqDto;
import gg.repo.party.CommentReportRepository;
import gg.repo.party.CommentRepository;
import gg.repo.party.PartyPenaltyRepository;
import gg.repo.party.RoomReportRepository;
import gg.repo.party.RoomRepository;
import gg.repo.party.UserReportRepository;
import gg.repo.party.UserRoomRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.party.AlredayReportedException;
import gg.utils.exception.party.CommentNotFoundException;
import gg.utils.exception.party.RoomNotFoundException;
import gg.utils.exception.party.RoomNotParticipantException;
import gg.utils.exception.party.SelfReportException;
import gg.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
	private static final int COMMENT_PENALTY_TIME = 1; // 댓글 패널티 시간 (분)
	private static final int NO_SHOW_PENALTY_TIME = 6; // 노쇼 패널티 시간 (시간)
	private static final int ROOM_PENALTY_TIME = 24; // 방 패널티 시간 (시간)
	private final RoomRepository roomRepository;
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final RoomReportRepository roomReportRepository;
	private final CommentReportRepository commentReportRepository;
	private final PartyPenaltyRepository partyPenaltyRepository;
	private final UserRoomRepository userRoomRepository;
	private final UserReportRepository userReportRepository;

	/**
	 * 방을 신고한다.
	 *
	 * @param roomId       방 번호
	 * @param reportReqDto 신고 내용
	 * @param user         신고자
	 * @return 방 번호
	 * @throws RoomNotFoundException    방을 찾을 수 없음
	 * @throws AlredayReportedException 이미 신고한 경우
	 */
	@Transactional
	public void addReportRoom(Long roomId, ReportReqDto reportReqDto, UserDto user) {
		Room targetRoom = roomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);
		User userEntity = userRepository.findById(user.getId()).get();
		if (Objects.equals(user.getId(), targetRoom.getCreator().getId())) {
			throw new SelfReportException();
		}
		Optional<RoomReport> existingReport = roomReportRepository.findByReporterAndRoomId(userEntity,
			targetRoom.getId());
		if (existingReport.isPresent()) {
			throw new AlredayReportedException();
		}
		RoomReport roomReport = new RoomReport(userEntity, targetRoom.getCreator(), targetRoom,
			reportReqDto.getContent());
		roomReportRepository.save(roomReport);

		List<RoomReport> allReportRoom = roomReportRepository.findByRoomId(targetRoom.getId());
		if (allReportRoom.size() == 3) {
			targetRoom.updateRoomStatus(RoomType.HIDDEN);
			roomRepository.save(targetRoom);
			User targetUser = targetRoom.getCreator();
			partyGivePenalty(targetUser.getIntraId(), ROOM_PENALTY_TIME, "방 패널티");
		}
	}

	/**
	 * 댓글을 신고한다.
	 *
	 * @param commentId    방 번호
	 * @param reportReqDto 신고 내용
	 * @param user         신고자
	 * @return 방 번호
	 * @throws CommentNotFoundException 방을 찾을 수 없음
	 * @throws AlredayReportedException 이미 신고한 경우
	 */
	@Transactional
	public void addReportComment(Long commentId, ReportReqDto reportReqDto, UserDto user) {
		Comment targetComment = commentRepository.findById(commentId)
			.orElseThrow(CommentNotFoundException::new);
		User userEntity = userRepository.findById(user.getId()).get();
		if (Objects.equals(user.getId(), targetComment.getUser().getId())) {
			throw new SelfReportException();
		}
		Optional<CommentReport> existingReport = commentReportRepository.findByReporterAndCommentId(userEntity,
			targetComment.getId());
		if (existingReport.isPresent()) {
			throw new AlredayReportedException();
		}
		Room targetRoom = roomRepository.findById(targetComment.getRoom().getId())
			.orElseThrow(RoomNotFoundException::new);
		CommentReport commentReport = new CommentReport(userEntity, targetComment, targetRoom,
			reportReqDto.getContent());
		commentReportRepository.save(commentReport);

		List<CommentReport> allReportComment = commentReportRepository.findByCommentId(targetComment.getId());
		if (allReportComment.size() == 3) {
			targetComment.updateHidden(true);
			commentRepository.save(targetComment);
			User targetUser = targetComment.getUser();
			partyGivePenalty(targetUser.getIntraId(), COMMENT_PENALTY_TIME, "댓글 패널티");
		}
	}

	/**
	 * 유저 노쇼 신고한다.
	 *
	 * @param roomId       방 번호
	 * @param reportReqDto 신고 내용
	 * @param user         신고자
	 * @param userIntraId  피신고자
	 * @return 방 번호
	 * @throws CommentNotFoundException 방을 찾을 수 없음
	 * @throws AlredayReportedException 이미 신고한 경우
	 */
	@Transactional
	public void addReportUser(Long roomId, ReportReqDto reportReqDto, String userIntraId, UserDto user) {
		// 신고자와 피신고자가 같은 경우
		if (Objects.equals(user.getIntraId(), userIntraId)) {
			throw new SelfReportException();
		}
		// 신고자와 피신고자가 같은 방에 있는지 확인
		User reporteeEntity = userRepository.findByIntraId(userIntraId)
			.orElseThrow(UserNotFoundException::new);
		UserRoom reporterUserRoom = userRoomRepository.findByUserIdAndRoomIdAndIsExistTrue(user.getId(), roomId)
			.orElseThrow(RoomNotParticipantException::new);
		userRoomRepository.findByUserIdAndRoomIdAndIsExistTrue(reporteeEntity.getId(), roomId)
			.orElseThrow(RoomNotParticipantException::new);
		User reporterEntity = reporterUserRoom.getUser();
		Room targetRoom = reporterUserRoom.getRoom();
		if (targetRoom == null) {
			throw new RoomNotFoundException();
		}
		// 이미 신고한 경우
		userReportRepository.findByReporterAndReportee(
			reporterEntity, reporteeEntity).orElseThrow(AlredayReportedException::new);
		// 신고 저장
		UserReport userReport = new UserReport(reporterEntity, reporteeEntity, targetRoom, reportReqDto.getContent());
		userReportRepository.save(userReport);
		// 노쇼 패널티 판단
		List<UserReport> allReportUser = userReportRepository.findByReporteeAndRoomId(reporteeEntity, roomId);
		if (allReportUser.size() == targetRoom.getMaxPeople() / 2) {
			partyGivePenalty(reporteeEntity.getIntraId(), NO_SHOW_PENALTY_TIME, "노쇼 패널티");
		}
	}

	/**
	 * 패널티 부여
	 *
	 * @param intraId     신고당한 유저 아이디
	 * @param penaltyTime 패널티 시간
	 * @param penaltyType 패널티 타입
	 */
	@Transactional
	public void partyGivePenalty(String intraId, Integer penaltyTime, String penaltyType) {
		User user = userRepository.findByIntraId(intraId).get();
		PartyPenalty pPenalty = partyPenaltyRepository.findByUserId(user.getId());

		if (pPenalty != null && LocalDateTime.now().isBefore(pPenalty.getStartTime()
			.plusHours(pPenalty.getPenaltyTime()))) {
			LocalDateTime dueDate = pPenalty.getStartTime().plusHours(pPenalty.getPenaltyTime());
			partyPenaltyRepository.save(new PartyPenalty(
				user, penaltyType, penaltyType, dueDate, penaltyTime * 60));
		} else {
			partyPenaltyRepository.save(new PartyPenalty(
				user, penaltyType, penaltyType, LocalDateTime.now(), penaltyTime * 60));
			List<Room> userRoomList = userRoomRepository.findOpenRoomsByUserId(user.getId());
			for (Room room : userRoomList) {
				UserRoom userRoom = userRoomRepository.findByUserIdAndRoomIdAndIsExistTrue(user.getId(), room.getId())
					.orElseThrow(RoomNotFoundException::new);
				userRoom.updateIsExist(false);
				userRoomRepository.save(userRoom);
			}
		}
	}
}
