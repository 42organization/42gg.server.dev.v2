package gg.party.api.user.comment.service;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import gg.data.party.Comment;
import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.user.User;
import gg.party.api.user.comment.controller.request.CommentCreateReqDto;
import gg.repo.party.CommentRepository;
import gg.repo.party.RoomRepository;
import gg.repo.party.UserRoomRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.party.CommentNotValidException;
import gg.utils.exception.party.RoomNotFoundException;
import gg.utils.exception.party.RoomUpdateException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	private final UserRoomRepository userRoomRepository;

	/**
	 * 댓글 생성
	 * @param roomId 방 번호
	 * @param reqDto 댓글 정보
	 */
	@Transactional
	public void createComment(Long roomId, CommentCreateReqDto reqDto, Long userId) {
		Room room = roomRepository.findById(roomId)
			.orElseThrow(() -> new RoomNotFoundException("유효하지 않은 방 입니다"));
		if (LocalDateTime.now().isAfter(room.getDueDate())) {
			throw new RoomUpdateException(ErrorCode.ROOM_FINISHED);
		}
		if (reqDto.getContent().length() > 100) {
			throw new CommentNotValidException(ErrorCode.COMMENT_TOO_LONG);
		}
		User user = userRepository.findById(userId).get();
		UserRoom userRoom = userRoomRepository.findByUserAndRoom(user, room)
			.orElseThrow(() -> new RoomUpdateException(ErrorCode.USER_NOT_IN_ROOM));
		if (!userRoom.getIsExist()) {
			throw new RoomUpdateException(ErrorCode.USER_NOT_IN_ROOM);
		}
		Comment comment = new Comment(user, userRoom, room, reqDto.getContent());
		commentRepository.save(comment);
	}

}
