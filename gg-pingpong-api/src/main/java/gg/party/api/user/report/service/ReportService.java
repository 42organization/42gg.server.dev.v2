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
import gg.utils.exception.party.AlreadyReportedException;
import gg.utils.exception.party.CommentNotFoundException;
import gg.utils.exception.party.OnPenaltyException;
import gg.utils.exception.party.RoomNotFoundException;
import gg.utils.exception.party.RoomNotParticipantException;
import gg.utils.exception.party.SelfReportException;
import gg.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
	private static final int COMMENT_PENALTY_TIME = 60; // 댓글 패널티 시간 (분)
	private static final int NO_SHOW_PENALTY_TIME = 360; // 노쇼 패널티 시간 (분)
	private static final int ROOM_PENALTY_TIME = 1440; // 방 패널티 시간 (분)
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
	 * @param roomId 방 번호
	 * @param reportReqDto 신고 내용
	 * @param userDto 신고자
	 * @throws OnPenaltyException 패널티 상태의 유저 입력 - 403
	 * @throws RoomNotFoundException 방을 찾을 수 없음 - 404
	 * @throws AlreadyReportedException 이미 신고한 경우 - 409
	 * @throws SelfReportException 자신을 신고한 경우 - 400
	 */
	@Transactional
	public void addReportRoom(Long roomId, ReportReqDto reportReqDto, UserDto userDto) {
		User user = userRepository.getById(userDto.getId());
		Optional<PartyPenalty> partyPenalty = partyPenaltyRepository.findTopByUserIdOrderByStartTimeDesc(user.getId());
		if (partyPenalty.isPresent() && PartyPenalty.isFreeFromPenalty(partyPenalty.get())) {
			throw new OnPenaltyException();
		}
		Room targetRoom = roomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);
		if (Objects.equals(user.getId(), targetRoom.getCreator().getId())) {
			throw new SelfReportException();
		}
		roomReportRepository.findByReporterAndRoomId(user, targetRoom.getId())
			.ifPresent(report -> {
				throw new AlreadyReportedException();
			});
		RoomReport roomReport = new RoomReport(user, targetRoom.getCreator(), targetRoom,
			reportReqDto.getContent());
		roomReportRepository.save(roomReport);

		List<RoomReport> allReportRoom = roomReportRepository.findByRoomId(targetRoom.getId());
		if (allReportRoom.size() == 5) {
			targetRoom.roomHidden();
			roomRepository.save(targetRoom);
			User targetUser = targetRoom.getCreator();
			partyGivePenalty(targetUser.getIntraId(), ROOM_PENALTY_TIME, "방 패널티");
		}
	}

	/**
	 * 댓글을 신고한다.
	 * @param commentId 방 번호
	 * @param reportReqDto 신고 내용
	 * @param userDto 신고자
	 * @throws UserNotFoundException 유효하지 않은 유저 입력 - 404
	 * @throws OnPenaltyException 패널티 상태의 유저 입력 - 403
	 * @throws RoomNotFoundException 방을 찾을 수 없음 - 404
	 * @throws CommentNotFoundException 댓글을 찾을 수 없음 - 404
	 * @throws AlreadyReportedException 이미 신고한 경우 - 409
	 * @throws SelfReportException 자신을 신고한 경우 - 400
	 */
	@Transactional
	public void addReportComment(Long commentId, ReportReqDto reportReqDto, UserDto userDto) {
		User user = userRepository.findById(userDto.getId()).orElseThrow(UserNotFoundException::new);
		Optional<PartyPenalty> partyPenalty = partyPenaltyRepository.findTopByUserIdOrderByStartTimeDesc(user.getId());
		if (partyPenalty.isPresent() && PartyPenalty.isFreeFromPenalty(partyPenalty.get())) {
			throw new OnPenaltyException();
		}
		Comment targetComment = commentRepository.findById(commentId)
			.orElseThrow(CommentNotFoundException::new);
		if (Objects.equals(user.getId(), targetComment.getUser().getId())) {
			throw new SelfReportException();
		}
		commentReportRepository.findByReporterAndCommentId(user, targetComment.getId())
			.ifPresent(reporter -> {
				throw new AlreadyReportedException();
			});
		Room targetRoom = roomRepository.findById(targetComment.getRoom().getId())
			.orElseThrow(RoomNotFoundException::new);
		CommentReport commentReport = new CommentReport(user, targetComment, targetRoom,
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
	 * @param roomId 방 번호
	 * @param reportReqDto 신고 내용
	 * @param user 신고자
	 * @param userIntraId 피신고자
	 * @throws CommentNotFoundException 방을 찾을 수 없음 - 404
	 * @throws AlreadyReportedException 이미 신고한 경우 - 409
	 * @throws RoomNotParticipantException 방에 참여하지 않은 경우 - 400
	 * @throws SelfReportException 자신을 신고한 경우 - 400
	 */
	@Transactional
	public void addReportUser(Long roomId, ReportReqDto reportReqDto, String userIntraId, UserDto user) {
		// 신고자와 피신고자가 같은 경우
		if (Objects.equals(user.getIntraId(), userIntraId)) {
			throw new SelfReportException();
		}
		// 신고자와 피신고자가 같은 방에 있는지 확인
		User reporteeEntity = userRepository.getUserByIntraId(userIntraId)
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
		userReportRepository.findByReporterAndReporteeAndRoom(reporterEntity, reporteeEntity, targetRoom)
			.ifPresent(userReport -> {
				throw new AlreadyReportedException();
			});
		// 신고 저장
		UserReport userReport = new UserReport(reporterEntity, reporteeEntity, targetRoom, reportReqDto.getContent());
		userReportRepository.save(userReport);
		// 노쇼 패널티 판단
		List<UserReport> allReportUser = userReportRepository.findByReporteeAndRoomId(reporteeEntity, roomId);
		if (allReportUser.size() == targetRoom.getCurrentPeople() / 2) {
			partyGivePenalty(reporteeEntity.getIntraId(), NO_SHOW_PENALTY_TIME, "노쇼 패널티");
		}
	}

	/**
	 * 패널티 부여
	 * @param intraId 신고당한 유저 아이디
	 * @param penaltyTime 패널티 시간
	 * @param penaltyType 패널티 타입
	 * @throws UserNotFoundException 유효하지 않은 유저 입력 - 404
	 */
	@Transactional
	public void partyGivePenalty(String intraId, Integer penaltyTime, String penaltyType) {
		User user = userRepository.getUserByIntraId(intraId).orElseThrow(UserNotFoundException::new);
		Optional<PartyPenalty> pPenalty = partyPenaltyRepository.findTopByUserIdOrderByStartTimeDesc(user.getId());

		if (pPenalty.isPresent() && LocalDateTime.now().isBefore(pPenalty.get().getStartTime()
			.plusMinutes(pPenalty.get().getPenaltyTime()))) {
			LocalDateTime dueDate = pPenalty.get().getStartTime().plusMinutes(pPenalty.get().getPenaltyTime());
			partyPenaltyRepository.save(new PartyPenalty(
				user, penaltyType, penaltyType, dueDate, penaltyTime));
		} else {
			partyPenaltyRepository.save(new PartyPenalty(
				user, penaltyType, penaltyType, LocalDateTime.now(), penaltyTime));
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
