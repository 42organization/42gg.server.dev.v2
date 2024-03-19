package gg.party.api.user.room.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.party.api.user.room.controller.response.CommentResDto;
import gg.party.api.user.room.controller.response.RoomDetailResDto;
import gg.party.api.user.room.controller.response.RoomListResDto;
import gg.party.api.user.room.controller.response.RoomResDto;
import gg.party.api.user.room.controller.response.UserRoomResDto;
import gg.pingpong.api.user.noti.service.PartyNotiService;
import gg.repo.party.CategoryRepository;
import gg.repo.party.CommentRepository;
import gg.repo.party.PartyPenaltyRepository;
import gg.repo.party.RoomRepository;
import gg.repo.party.UserRoomRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.party.RoomNotFoundException;
import gg.utils.exception.party.RoomReportedException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomFindService {
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final UserRoomRepository userRoomRepository;
	private final CommentRepository commentRepository;
	private final PartyPenaltyRepository partyPenaltyRepository;
	private final PartyNotiService partyNotiService;

	/**
	 * 시작하지 않은 방과 시작한 방을 모두 조회한다
	 * @return 시작하지 않은 방 (최신순) + 시작한 방(끝나는 시간이 빠른 순) 전체 List
	 */
	@Transactional
	public RoomListResDto findRoomList() {
		Sort sortForNotStarted = Sort.by("createdAt").descending();
		Sort sortForStarted = Sort.by("startDate").descending();

		List<Room> notStartedRooms = roomRepository.findByStatus(RoomType.OPEN, sortForNotStarted);
		List<Room> startedRooms = roomRepository.findByStatus(RoomType.START, sortForStarted);

		notStartedRooms.addAll(startedRooms);
		List<RoomResDto> roomListResDto = notStartedRooms.stream()
			.map(RoomResDto::new)
			.collect(Collectors.toList());

		return new RoomListResDto(roomListResDto);
	}

	/**
	 * 현재 참여중인 방을 모두 조회한다(만든 방 포함)
	 * @param userId 자신의 id
	 * @return 참여한 방 전체 List
	 */
	@Transactional
	public RoomListResDto findJoinedRoomList(Long userId) {
		List<UserRoom> userRooms = userRoomRepository.findByUserId(userId);
		List<Room> joinedRooms = userRooms.stream()
			.map(UserRoom::getRoom)
			.collect(Collectors.toList());

		Collections.sort(joinedRooms, Comparator.comparing(Room::getDueDate));

		List<Room> playingRoom = joinedRooms.stream()
			.filter(room -> room.getStatus() == RoomType.OPEN || room.getStatus() == RoomType.START)
			.collect(Collectors.toList());

		Collections.sort(playingRoom, Comparator.comparing(Room::getDueDate));

		List<RoomResDto> roomListResDto = playingRoom.stream()
			.map(RoomResDto::new)
			.collect(Collectors.toList());

		return new RoomListResDto(roomListResDto);
	}

	/**
	 * 시간이 지나 보이지 않게 된 내가 플레이한(시작한) 방을 모두 조회한다
	 * @param userId 자신의 id
	 * user_room db에서 자신의 id와 isExist이 true(나가지 않았음)
	 * 이면서 status가 FINISH인 경우를 마감기한 최신순으로 정렬
	 * @return 끝난 방 전체 List
	 */
	@Transactional
	public RoomListResDto findMyHistoryRoomList(Long userId) {
		List<Room> finishRooms = userRoomRepository.findFinishRoomsByUserId(userId, RoomType.FINISH);

		List<RoomResDto> roomListResDto = finishRooms.stream()
			.map(RoomResDto::new)
			.collect(Collectors.toList());

		return new RoomListResDto(roomListResDto);
	}

	/**
	 * 방의 상세정보를 조회한다
	 * @param userId 자신의 id
	 * @param roomId 방 id
	 * @return 방 상세정보 dto
	 * @throws RoomNotFoundException 유효하지 않은 방 입력
	 * @throws RoomReportedException 신고 받은 방 처리 | 시작한 방도 볼 수 있게 해야하므로 별도처리
	 * 익명성을 지키기 위해 nickname을 리턴
	 */
	@Transactional
	public RoomDetailResDto findRoomDetail(Long userId, Long roomId) {
		Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
		if (room.getStatus() == RoomType.HIDDEN) {
			throw new RoomReportedException();
		}

		Optional<UserRoom> userRoomOptional = userRoomRepository.findByUserIdAndRoomIdAndIsExistTrue(userId, roomId);

		String myNickname = null;
		if (userRoomOptional.isPresent()) {
			UserRoom userRoom = userRoomOptional.get();
			myNickname = userRoom.getNickname();
		}

		Optional<UserRoom> hostUserRoomOptional = userRoomRepository.findByUserIdAndRoomIdAndIsExistTrue(
			room.getHost().getId(), roomId);
		String hostNickname = hostUserRoomOptional.get().getNickname();

		if (room.getStatus() == RoomType.START && userRoomOptional.isPresent()) {
			List<CommentResDto> comments = commentRepository.findByRoomId(roomId).stream()
				.map(comment -> new CommentResDto(comment, comment.getUser().getIntraId()))
				.collect(Collectors.toList());

			List<UserRoomResDto> roomUsers = userRoomRepository.findByRoomId(roomId).stream()
				.filter(UserRoom::getIsExist)
				.map(userRoom -> new UserRoomResDto(userRoom, userRoom.getUser().getIntraId(),
					userRoom.getUser().getImageUri()))
				.collect(Collectors.toList());
			return new RoomDetailResDto(room, myNickname, hostNickname, roomUsers, comments);
		} else { // if 참여자 && 시작했을경우 intraID || else intraId == null
			List<CommentResDto> comments = commentRepository.findByRoomId(roomId).stream()
				.map(CommentResDto::new)
				.collect(Collectors.toList());

			List<UserRoomResDto> roomUsers = userRoomRepository.findByRoomId(roomId).stream()
				.filter(UserRoom::getIsExist)
				.map(UserRoomResDto::new)
				.collect(Collectors.toList());
			return new RoomDetailResDto(room, myNickname, hostNickname, roomUsers, comments);
		}
	}

}
