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
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import gg.party.api.user.report.request.ReportReqDto;
import gg.repo.party.CommentReportRepository;
import gg.repo.party.CommentRepository;
import gg.repo.party.PartyPenaltyRepository;
import gg.repo.party.RoomReportRepository;
import gg.repo.party.RoomRepository;
import gg.repo.party.UserRoomRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.party.AlredayReportedException;
import gg.utils.exception.party.CommentNotFoundException;
import gg.utils.exception.party.RoomNotFoundException;
import gg.utils.exception.party.SelfReportException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {
	private final RoomRepository roomRepository;
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final RoomReportRepository roomReportRepository;
	private final CommentReportRepository commentReportRepository;
	private final PartyPenaltyRepository partyPenaltyRepository;
	private final UserRoomRepository userRoomRepository;

	/**
	 * 방을 신고한다.
	 * @param roomId 방 번호
	 * @param reportReqDto 신고 내용
	 * @param user 신고자
	 * @exception RoomNotFoundException 방을 찾을 수 없음
	 * @exception AlredayReportedException 이미 신고한 경우
	 * @return 방 번호
	 */
	@Transactional
	public Long addReportRoom(Long roomId, ReportReqDto reportReqDto, UserDto user) {
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
			partyGivePenalty(targetUser.getIntraId(), 24, "방 패널티");
		}
		return roomId;
	}

	/**
	 * 댓글을 신고한다.
	 * @param commentId 방 번호
	 * @param reportReqDto 신고 내용
	 * @param user 신고자
	 * @exception CommentNotFoundException 방을 찾을 수 없음
	 * @exception AlredayReportedException 이미 신고한 경우
	 * @return 방 번호
	 */
	@Transactional
	public Long addReportComment(Long commentId, ReportReqDto reportReqDto, UserDto user) {
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
			partyGivePenalty(targetUser.getIntraId(), 1, "댓글 패널티");
		}
		return commentId;
	}

	/**
	 * 패널티 부여
	 * @param intraId 신고당한 유저 아이디
	 * @param penaltyTime 패널티 시간
	 * @param penaltyType 패널티 타입
	 */
	@Transactional
	public void partyGivePenalty(String intraId, Integer penaltyTime, String penaltyType) {
		User user = userRepository.findByIntraId(intraId).get();
		PartyPenalty pPenalty = partyPenaltyRepository.findByUserId(user.getId());
		if (pPenalty != null) {
			pPenalty.updatePenaltyTime(pPenalty.getPenaltyTime() + penaltyTime * 60);
			pPenalty.updateMessage(pPenalty.getMessage() + penaltyType);
			pPenalty.updatePenaltyType("복합");
			partyPenaltyRepository.save(pPenalty);
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
