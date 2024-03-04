package gg.party.api.user.room.service;

import static gg.party.api.user.room.utils.GenerateRandomNickname.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gg.data.party.Category;
import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import gg.party.api.user.room.controller.request.RoomCreateReqDto;
import gg.party.api.user.room.controller.response.CommentResDto;
import gg.party.api.user.room.controller.response.RoomDetailResDto;
import gg.party.api.user.room.controller.response.RoomListResDto;
import gg.party.api.user.room.controller.response.RoomResDto;
import gg.party.api.user.room.controller.response.UserRoomResDto;
import gg.party.api.user.room.controller.response.LeaveRoomResponseDto;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.repo.party.CategoryRepository;
import gg.repo.party.CommentRepository;
import gg.repo.party.RoomRepository;
import gg.repo.party.UserRoomRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.party.CategoryNotFoundException;
import gg.utils.exception.party.RoomNotFoundException;
import gg.utils.exception.party.RoomReportedException;
import gg.utils.exception.party.RoomNotOpenException;
import gg.utils.exception.party.RoomNotParticipantException;
import gg.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final UserRoomRepository userRoomRepository;
	private final CommentRepository commentRepository;

	/**
	 * 시작하지 않은 방과 시작한 방을 모두 조회한다
	 * @return 시작하지 않은 방 (최신순) + 시작한 방(끝나는 시간이 빠른 순) 전체 List
	 */
	@Transactional
	public RoomListResDto findOrderRoomList() {
		Sort sortForNotStarted = Sort.by("createdAt").descending();
		Sort sortForStarted = Sort.by("dueDate").ascending();

		List<Room> notStartedRooms = roomRepository.findByStatus(RoomType.OPEN, sortForNotStarted);
		List<Room> startedRooms = roomRepository.findByStatus(RoomType.START, sortForStarted);

		notStartedRooms.addAll(startedRooms);
		List<RoomResDto> roomListResDto = notStartedRooms.stream()
			.map(room -> new RoomResDto(room))
			.collect(Collectors.toList());

		return new RoomListResDto(roomListResDto);
	}

	/**
	 * 방 생성하고 닉네임 부여
	 * @param roomCreateReqDto 요청 DTO
	 * @param userDto user객체를 보내기 위한 DTO객체
	 * @exception UserNotFoundException 유효하지 않은 유저 입력
	 * @exception CategoryNotFoundException 유효하지 카테고리 입력
	 * @return 만들어진 방 ID값
	 */
	@Transactional
	public Long addOrderCreateRoom(RoomCreateReqDto roomCreateReqDto, UserDto userDto) {
		User user = userRepository.findById(userDto.getId()).get();
		Category category = categoryRepository.findById(roomCreateReqDto.getCategoryId())
			.orElseThrow(CategoryNotFoundException::new);
		Room room = roomRepository.save(RoomCreateReqDto.toEntity(roomCreateReqDto, user, category));

		String randomNickname = generateRandomNickname();

		UserRoom userRoom = new UserRoom(user, room, randomNickname);
		userRoomRepository.save(userRoom);
		return room.getId();
	}

	/**
	 * 현재 참여중인 방을 모두 조회한다(만든 방 포함)
	 * @param userId 자신의 id
	 * @return 참여한 방 전체 List
	 */
	@Transactional
	public RoomListResDto findOrderJoinedRoomList(Long userId) {
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
	 * 이면서 room.status가 FINISH인 경우를 마감기한 최신순으로 정렬
	 * @return 끝난 방 전체 List
	 */
	@Transactional
	public RoomListResDto findOrderMyHistoryRoomList(Long userId) {
		List<Room> finishRooms = userRoomRepository.findFinishRoomsByUserId(userId, RoomType.FINISH);

		List<RoomResDto> roomListResDto = finishRooms.stream()
			.map(RoomResDto::new)
			.collect(Collectors.toList());

		return new RoomListResDto(roomListResDto);
	}

	/**
	 * 유저가 방을 나간다
	 * 참가자가 방에 참가한 상태일때만 취소해 준다.
	 * @param roomId
	 * @param user 참여 유저(사용자 본인)
	 * @throws RoomNotFoundException 방 없음 || 방 입장자가 아님
	 * @return
	 */
	@Transactional
	public LeaveRoomResponseDto leaveRoom(Long roomId, UserDto user) {
		Room targetRoom = roomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);
		if (!targetRoom.getStatus().equals(RoomType.OPEN)) {
			throw new RoomNotOpenException();
		}

		List<UserRoom> userRoomList = userRoomRepository.findByUserId(user.getId());
		UserRoom targetUserRoom = userRoomList.stream()
			.filter(tu -> (tu.getUser().getId().equals(user.getId())))
			.findAny()
			.orElseThrow(() -> new RoomNotParticipantException(ErrorCode.ROOM_NOT_PARTICIPANT));

		// 모두 나갈 때 방 fail처리
		if (targetRoom.getCurrentPeople() == 1) {
			targetRoom.updateCurrentPeople(0);
			targetRoom.updateStatus(RoomType.FAIL);
			return new LeaveRoomResponseDto(targetUserRoom.getNickname());
		}

		// 방장 이권
		if (user.getId().equals(targetRoom.getHost().getId())) {
			List<UserRoom> existUserRooms = userRoomRepository.findByIsExist(roomId);
			targetRoom.updateHost(existUserRooms.get(0).getUser());
		}

		targetRoom.updateCurrentPeople(targetRoom.getCurrentPeople() - 1);
		targetUserRoom.updateIsExist(false);
		userRoomRepository.save(targetUserRoom);

		return new LeaveRoomResponseDto(targetUserRoom.getNickname());
	}

	/**
	 * 방의 상세정보를 조회한다
	 * @param userId 자신의 id
	 * @param roomId 방 id
	 * @exception RoomNotFoundException 유효하지 않은 방 입력
	 * @exception RoomReportedException 신고 받은 방 처리
	 * 익명성을 지키기 위해 nickname을 리턴
	 * @return 방 상세정보 dto
	 */
	@Transactional
	public RoomDetailResDto findOrderRoomDetail(Long userId, Long roomId) {
		Room room = roomRepository.findById(roomId)
			.orElseThrow(() -> new RoomNotFoundException(ErrorCode.ROOM_NOT_FOUND));
		if (room.getStatus() == RoomType.HIDDEN) {
			throw new RoomReportedException(roomId + "번방은 신고 상태로 접근이 불가능합니다.");
		}

		List<UserRoomResDto> roomUsers = userRoomRepository.findByRoomId(roomId).stream()
			.filter(userRoom -> userRoom.getIsExist())
			.map(userRoom -> new UserRoomResDto(userRoom.getId(), userRoom.getNickname()))
			.collect(Collectors.toList());

		List<CommentResDto> comments = commentRepository.findByRoomId(roomId).stream()
			.map(
				comment -> new CommentResDto(comment.getId(), comment.getUserRoom().getNickname(), comment.getContent(),
					comment.isHidden(), comment.getCreatedAt()))
			.collect(Collectors.toList());

		Optional<UserRoom> userRoomOptional = userRoomRepository.findByUserIdAndRoomIdAndIsExistTrue(userId, roomId);
		String myNickname = null;
		if (userRoomOptional.isPresent()) {
			UserRoom userRoom = userRoomOptional.get();
			myNickname = userRoom.getNickname();
		}

		Optional<UserRoom> hostUserRoomOptional = userRoomRepository.findByUserIdAndRoomIdAndIsExistTrue(
			room.getHost().getId(), roomId);
		String hostNickname = hostUserRoomOptional.get().getNickname();

		return new RoomDetailResDto(room, myNickname, hostNickname, roomUsers, comments);
	}
}
