package gg.party.api.user.room.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.party.api.user.room.controller.response.CommentResDto;
import gg.party.api.user.room.controller.response.RoomDetailResDto;
import gg.party.api.user.room.controller.response.RoomListResDto;
import gg.party.api.user.room.controller.response.RoomResDto;
import gg.party.api.user.room.controller.response.UserRoomResDto;
import gg.repo.party.CommentRepository;
import gg.repo.party.RoomRepository;
import gg.repo.party.UserRoomRepository;
import gg.utils.exception.party.RoomNotFoundException;
import gg.utils.exception.party.RoomReportedException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomFindService {
	private final RoomRepository roomRepository;
	private final UserRoomRepository userRoomRepository;
	private final CommentRepository commentRepository;

	/**
	 * 시작하지 않은 방과 시작한 방을 모두 조회한다
	 * @return 시작하지 않은 방 (최신순) + 시작한 방(끝나는 시간이 빠른 순) 전체 List
	 */
	@Transactional(readOnly = true)
	public RoomListResDto findRoomList() {
		List<RoomType> statuses = Arrays.asList(RoomType.OPEN, RoomType.START, RoomType.FINISH);
		List<Room> rooms = roomRepository.findByStatusIn(statuses);

		List<Room> openRooms = rooms.stream()
			.filter(room -> room.getStatus().equals(RoomType.OPEN))
			.sorted(Comparator.comparing(Room::getCreatedAt).reversed())
			.collect(Collectors.toList());

		List<Room> startRooms = rooms.stream()
			.filter(room -> room.getStatus().equals(RoomType.START))
			.sorted(Comparator.comparing(Room::getStartDate).reversed())
			.collect(Collectors.toList());

		List<Room> finishRooms = rooms.stream()
			.filter(room -> room.getStatus().equals(RoomType.FINISH))
			.sorted(Comparator.comparing(Room::getStartDate).reversed())
			.limit(10)
			.collect(Collectors.toList());

		List<Room> combinedRooms = Stream.of(openRooms, startRooms, finishRooms)
			.flatMap(List::stream)
			.collect(Collectors.toList());

		List<RoomResDto> roomListResDto = combinedRooms.stream()
			.map(RoomResDto::new)
			.collect(Collectors.toList());

		return new RoomListResDto(roomListResDto);
	}

	/**
	 * 현재 참여중인 방을 모두 조회한다(만든 방 포함)
	 * 시작한 방 뒤에 시작하지 않은 방이 오게 작성
	 * @param userId 자신의 id
	 * @return 참여한 방 전체 List
	 */
	@Transactional(readOnly = true)
	public RoomListResDto findJoinedRoomList(Long userId) {
		List<UserRoom> userRooms = userRoomRepository.findByUserIdAndIsExistTrue(userId);
		List<Room> joinedRooms = userRooms.stream()
			.map(UserRoom::getRoom)
			.collect(Collectors.toList());

		List<Room> openRoom = joinedRooms.stream()
			.filter(room -> room.getStatus().equals(RoomType.OPEN))
			.sorted(Comparator.comparing(Room::getDueDate))
			.collect(Collectors.toList());

		List<Room> startRoom = joinedRooms.stream()
			.filter(room -> room.getStatus().equals(RoomType.START))
			.sorted(Comparator.comparing(Room::getStartDate))
			.collect(Collectors.toList());

		openRoom.addAll(startRoom);

		List<RoomResDto> roomListResDto = openRoom.stream()
			.map(RoomResDto::new)
			.collect(Collectors.toList());

		return new RoomListResDto(roomListResDto);
	}

	/**
	 * 시간이 지나 보이지 않게 된 내가 플레이한(FINISH) 방을 모두 조회한다
	 * @param userId 자신의 id
	 * isExist이 true(나가지 않았음)이면서 status가 FINISH인 경우
	 * @return 시작시간으로 정렬된 끝난 방 전체 List
	 */
	@Transactional(readOnly = true)
	public RoomListResDto findMyHistoryRoomList(Long userId) {
		List<Room> finishRooms = userRoomRepository.findByUserIdAndStatusAndIsExistTrue(userId, RoomType.FINISH);

		List<RoomResDto> roomListResDto = finishRooms.stream()
			.sorted(Comparator.comparing(Room::getStartDate))
			.map(RoomResDto::new)
			.collect(Collectors.toList());

		return new RoomListResDto(roomListResDto);
	}

	/**
	 * 방의 상세정보를 조회한다
	 * @param userId 자신의 id
	 * @param roomId 방 id
	 * @return 방 상세정보 dto
	 * @throws RoomNotFoundException 유효하지 않은 방 입력 - 404
	 * @throws RoomReportedException 신고 받은 방 처리 - 403
	 * 익명성을 지키기 위해 nickname을 리턴
	 */
	@Transactional(readOnly = true)
	public RoomDetailResDto findRoomDetail(Long userId, Long roomId) {
		Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
		if (room.getStatus().equals(RoomType.HIDDEN)) {
			throw new RoomReportedException();
		}

		Optional<UserRoom> userRoomOptional = userRoomRepository.findByUserIdAndRoomIdAndIsExistTrue(userId, roomId);

		String myNickname = userRoomOptional.stream()
			.map(UserRoom::getNickname)
			.findFirst()
			.orElse(null);

		Optional<UserRoom> hostUserRoom = userRoomRepository.findByUserIdAndRoomIdAndIsExistTrue(room.getHost().getId(),
			roomId);
		String hostNickname = hostUserRoom.stream()
			.map(UserRoom::getNickname)
			.findFirst()
			.orElse(null);

		if ((room.getStatus().equals(RoomType.START) || room.getStatus().equals(RoomType.FINISH))
			&& userRoomOptional.isPresent()) {
			List<UserRoomResDto> roomUsers = userRoomRepository.findByRoomIdAndIsExistTrue(roomId).stream()
				.map(userRoom -> new UserRoomResDto(userRoom, userRoom.getUser().getIntraId(),
					userRoom.getUser().getImageUri()))
				.collect(Collectors.toList());

			List<CommentResDto> comments = commentRepository.findAllWithCommentFetchJoin(roomId).stream()
				.filter(comment -> !comment.isHidden())
				.map(comment -> new CommentResDto(comment, comment.getUser().getIntraId()))
				.collect(Collectors.toList());

			return new RoomDetailResDto(room, myNickname, hostNickname, roomUsers, comments);
		} else { // if 참여자 && Start or Finish 상태인 경우 intraID 제공 || else intraId == null
			List<UserRoomResDto> roomUsers = userRoomRepository.findByRoomIdAndIsExistTrue(roomId).stream()
				.map(UserRoomResDto::new)
				.collect(Collectors.toList());

			List<CommentResDto> comments = commentRepository.findAllWithCommentFetchJoin(roomId).stream()
				.filter(comment -> !comment.isHidden())
				.map(CommentResDto::new)
				.collect(Collectors.toList());

			return new RoomDetailResDto(room, myNickname, hostNickname, roomUsers, comments);
		}
	}
}
