package gg.party.api.user.comment.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import gg.data.party.Comment;
import gg.data.party.PartyPenalty;
import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import gg.party.api.user.comment.controller.request.CommentCreateReqDto;
import gg.repo.party.CommentRepository;
import gg.repo.party.PartyPenaltyRepository;
import gg.repo.party.RoomRepository;
import gg.repo.party.UserRoomRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.party.OnPenaltyException;
import gg.utils.exception.party.RoomNotFoundException;
import gg.utils.exception.party.RoomNotOpenException;
import gg.utils.exception.party.RoomNotParticipantException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	private final UserRoomRepository userRoomRepository;
	private final PartyPenaltyRepository partyPenaltyRepository;

	/**
	 * 댓글 생성
	 * @param roomId 방 번호
	 * @throws RoomNotFoundException 방을 찾을 수 없음 - 404
	 * @throws RoomNotOpenException 방이 열려있지 않음 - 400
	 * @throws OnPenaltyException 패널티 상태의 유저 입력 - 403
	 * @throws RoomNotParticipantException 방 참가자가 아님 - 400
	 * @param reqDto 댓글 정보
	 */
	@Transactional
	public void addCreateComment(Long roomId, CommentCreateReqDto reqDto, Long userId) {
		User user = userRepository.getById(userId);
		Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
		if (room.getStatus() != RoomType.OPEN) {
			throw new RoomNotOpenException();
		}

		Optional<PartyPenalty> partyPenalty = partyPenaltyRepository.findTopByUserIdOrderByStartTimeDesc(userId);
		if (partyPenalty.isPresent() && PartyPenalty.isFreeFromPenalty(partyPenalty.get())) {
			throw new OnPenaltyException();
		}

		UserRoom userRoom = userRoomRepository.findByUserIdAndRoomIdAndIsExistTrue(userId, roomId)
			.orElseThrow(RoomNotParticipantException::new);
		Comment comment = new Comment(user, userRoom, room, reqDto.getContent());
		commentRepository.save(comment);
	}

}
