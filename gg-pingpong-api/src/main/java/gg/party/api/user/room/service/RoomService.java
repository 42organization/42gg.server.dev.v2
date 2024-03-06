package gg.party.api.user.room.service;

import static gg.party.api.user.room.utils.GenerateRandomNickname.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gg.auth.UserDto;
import gg.data.party.Category;
import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import gg.party.api.user.room.controller.request.RoomCreateReqDto;
import gg.party.api.user.room.controller.response.CommentResDto;
import gg.party.api.user.room.controller.response.LeaveRoomResDto;
import gg.party.api.user.room.controller.response.RoomDetailResDto;
import gg.party.api.user.room.controller.response.RoomJoinResDto;
import gg.party.api.user.room.controller.response.RoomListResDto;
import gg.party.api.user.room.controller.response.RoomResDto;
import gg.party.api.user.room.controller.response.UserRoomResDto;
import gg.repo.party.CategoryRepository;
import gg.repo.party.CommentRepository;
import gg.repo.party.RoomRepository;
import gg.repo.party.UserRoomRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.party.CategoryNotFoundException;
import gg.utils.exception.party.RoomNotEnoughPeopleException;
import gg.utils.exception.party.RoomNotFoundException;
import gg.utils.exception.party.RoomNotOpenException;
import gg.utils.exception.party.RoomNotParticipantException;
import gg.utils.exception.party.RoomReportedException;
import gg.utils.exception.party.UserAlreadyInRoom;
import gg.utils.exception.party.UserNotHostException;
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
	public RoomListResDto findRoomList() {
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
	 * @exception CategoryNotFoundException 유효하지 카테고리 입력
	 * @return 만들어진 방 ID값
	 */
	@Transactional
	public Long addCreateRoom(RoomCreateReqDto roomCreateReqDto, UserDto userDto) {
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
			.map(UserRoom::getRoom).sorted(Comparator.comparing(Room::getDueDate)).toList();

		List<Room> playingRoom = joinedRooms.stream()
			.filter(room -> room.getStatus() == RoomType.OPEN || room.getStatus() == RoomType.START)
			.sorted(Comparator.comparing(Room::getDueDate))
			.toList();

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
	public RoomListResDto findMyHistoryRoomList(Long userId) {
		List<Room> finishRooms = userRoomRepository.findFinishRoomsByUserId(userId, RoomType.FINISH);

		List<RoomResDto> roomListResDto = finishRooms.stream()
			.map(RoomResDto::new)
			.collect(Collectors.toList());

		return new RoomListResDto(roomListResDto);
	}

	/**
	 * 유저가 방을 나간다
	 * 참가자가 방에 참가한 상태일때만 취소해 준다.
	 * @param roomId, user
	 * @param user 참여 유저(사용자 본인)
	 * @throws RoomNotFoundException 방 없음
	 * @throws RoomNotOpenException 방이 대기 상태가 아님
	 * @throws RoomNotParticipantException 방 참여자가 아님
	 * @return 나간 사람의 닉네임
	 */
	@Transactional
	public LeaveRoomResDto modifyLeaveRoom(Long roomId, UserDto user) {
		Room targetRoom = roomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);
		if (!targetRoom.getStatus().equals(RoomType.OPEN)) {
			throw new RoomNotOpenException();
		}
		UserRoom targetUserRoom = userRoomRepository.findByUserAndRoom(userRepository.findById(user.getId()).get(),
			targetRoom).orElseThrow(RoomNotParticipantException::new);

		// 모두 나갈 때 방 fail처리
		if (targetRoom.getCurrentPeople() == 1) {
			targetRoom.updateCurrentPeople(0);
			targetRoom.updateStatus(RoomType.FAIL);
			targetUserRoom.updateIsExist(false);
			roomRepository.save(targetRoom);
			userRoomRepository.save(targetUserRoom);
			return new LeaveRoomResDto(targetUserRoom.getNickname());
		}

		// 방장 이권
		if (user.getId().equals(targetRoom.getHost().getId())) {
			List<User> existUser = userRoomRepository.findByIsExist(roomId);
			targetRoom.updateHost(existUser.get(0));
		}

		targetRoom.updateCurrentPeople(targetRoom.getCurrentPeople() - 1);
		targetUserRoom.updateIsExist(false);
		userRoomRepository.save(targetUserRoom);
		roomRepository.save(targetRoom);

		return new LeaveRoomResDto(targetUserRoom.getNickname());
	}

	/**
	 * 방을 시작 상태로 바꾼다
	 * 방의 상태를 시작 상태로 변경.
	 * @param roomId, user
	 * @throws RoomNotFoundException 방 없음
	 * @throws RoomNotOpenException 방이 열리지 않은 상태
	 * @throws RoomNotEnoughPeopleException 방에 충분한 인원이 없음
	 * @throws RoomNotParticipantException 방에 참가하지 않은 유저
	 * @throws UserNotHostException 방장이 아닌 경우
	 * @return 방 id
	 */
	@Transactional
	public Long modifyStartRoom(Long roomId, UserDto user) {
		Room targetRoom = roomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);
		if (!targetRoom.getStatus().equals(RoomType.OPEN)) {
			throw new RoomNotOpenException();
		}
		if (targetRoom.getMinPeople() > targetRoom.getCurrentPeople()
			|| targetRoom.getMaxPeople() < targetRoom.getCurrentPeople()) {
			throw new RoomNotEnoughPeopleException();
		}
		UserRoom targetUserRoom = userRoomRepository.findByUserAndRoom(userRepository.findById(user.getId()).get(),
			targetRoom).orElseThrow(RoomNotParticipantException::new);
		if (targetRoom.getHost() != targetUserRoom.getUser()) {
			throw new UserNotHostException();
		}
		targetRoom.updateStatus(RoomType.START);
		roomRepository.save(targetRoom);

		return roomId;
	}

	/**
	 * 방의 상세정보를 조회한다
	 * @param userId 자신의 id
	 * @param roomId 방 id
	 * @exception RoomNotFoundException 유효하지 않은 방 입력
	 * @exception RoomReportedException 신고 받은 방 처리 | 시작한 방도 볼 수 있게 해야하므로 별도처리
	 * 익명성을 지키기 위해 nickname을 리턴
	 * @return 방 상세정보 dto
	 */
	@Transactional
	public RoomDetailResDto findRoomDetail(Long userId, Long roomId) {
		Room room = roomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);
		if (room.getStatus() == RoomType.HIDDEN) {
			throw new RoomReportedException();
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

	/**
	 * 방에 참여한다
	 * @param roomId 방 id
	 * @param userDto 유저 정보
	 * @exception RoomNotFoundException 유효하지 않은 방 입력
	 * @exception RoomNotOpenException 모집중인 방이 아님
	 * @exception UserAlreadyInRoom 이미 참여한 방 입력
	 * 과거 참여 이력이 있을 경우 그 아이디로, 없을경우 currentPeople을 증가시킨다
	 * @return roomId
	 */
	@Transactional
	public RoomJoinResDto addJoinRoom(Long roomId, UserDto userDto) {
		User user = userRepository.findById(userDto.getId()).get();
		Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);

		if (room.getStatus() != RoomType.OPEN) {
			throw new RoomNotOpenException();
		}
		UserRoom userRoom = userRoomRepository.findByUserAndRoom(user, room)
			.orElseGet(() -> {
				String randomNickname = generateRandomNickname();
				UserRoom newUserRoom = new UserRoom(user, room, randomNickname);
				newUserRoom.updateIsExist(false);
				return newUserRoom;
			});
		if (userRoom.getIsExist() == true) {
			throw new UserAlreadyInRoom(ErrorCode.USER_ALREADY_IN_ROOM);
		} else {
			userRoom.updateIsExist(true);
			userRoomRepository.save(userRoom);
		}

		room.updateCurrentPeople(room.getCurrentPeople() + 1);
		// if (room.getCurrentPeople() + 1 == room.getMaxPeople()) 경우 게임 시작하는 로직 추가
		roomRepository.save(room);

		return new RoomJoinResDto(roomId);
	}
}
