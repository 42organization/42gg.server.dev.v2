package gg.party.api.user.room.service;

import static gg.party.api.user.room.utils.GenerateRandomNickname.*;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import gg.auth.UserDto;
import gg.data.party.Category;
import gg.data.party.PartyPenalty;
import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import gg.party.api.user.room.controller.request.RoomCreateReqDto;
import gg.party.api.user.room.controller.response.LeaveRoomResDto;
import gg.party.api.user.room.controller.response.RoomCreateResDto;
import gg.party.api.user.room.controller.response.RoomJoinResDto;
import gg.party.api.user.room.controller.response.RoomStartResDto;
import gg.pingpong.api.user.noti.service.PartyNotiService;
import gg.repo.party.CategoryRepository;
import gg.repo.party.PartyPenaltyRepository;
import gg.repo.party.RoomRepository;
import gg.repo.party.UserRoomRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.party.CategoryNotFoundException;
import gg.utils.exception.party.OnPenaltyException;
import gg.utils.exception.party.RoomMinMaxPeople;
import gg.utils.exception.party.RoomNotEnoughPeopleException;
import gg.utils.exception.party.RoomNotFoundException;
import gg.utils.exception.party.RoomNotOpenException;
import gg.utils.exception.party.RoomNotParticipantException;
import gg.utils.exception.party.UserAlreadyInRoom;
import gg.utils.exception.party.UserNotHostException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomManagementService {
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final UserRoomRepository userRoomRepository;
	private final PartyPenaltyRepository partyPenaltyRepository;
	private final PartyNotiService partyNotiService;

	/**
	 * 방 생성하고 닉네임 부여
	 * @param roomCreateReqDto 요청 DTO
	 * @param userDto user객체를 보내기 위한 DTO객체
	 * @throws OnPenaltyException 패널티 상태의 유저 입력 - 403
	 * @throws RoomMinMaxPeople 최소인원이 최대인원보다 큰 경우 - 400
	 * @throws CategoryNotFoundException 유효하지 않은 카테고리 입력 - 404
	 * @return 만들어진 방 ID값
	 */
	@Transactional
	public RoomCreateResDto addCreateRoom(RoomCreateReqDto roomCreateReqDto, UserDto userDto) {
		User user = userRepository.getById(userDto.getId());
		Optional<PartyPenalty> partyPenalty = partyPenaltyRepository.findTopByUserIdOrderByStartTimeDesc(user.getId());
		if (partyPenalty.isPresent() && PartyPenalty.isFreeFromPenalty(partyPenalty.get())) {
			throw new OnPenaltyException();
		}
		if (roomCreateReqDto.getMaxPeople() < roomCreateReqDto.getMinPeople()) {
			throw new RoomMinMaxPeople(ErrorCode.ROOM_MIN_MAX_PEOPLE);
		}
		Category category = categoryRepository.findByName(roomCreateReqDto.getCategoryName())
			.orElseThrow(CategoryNotFoundException::new);
		Room room = roomRepository.save(RoomCreateReqDto.toEntity(roomCreateReqDto, user, category));

		String randomNickname = generateRandomNickname();

		UserRoom userRoom = new UserRoom(user, room, randomNickname);
		userRoomRepository.save(userRoom);
		return new RoomCreateResDto(room.getId());
	}

	/**
	 * 유저가 방을 나간다
	 * 참가자가 방에 참가한 상태일때만 취소해 준다.
	 * @param roomId, user
	 * @param userDto 참여 유저(사용자 본인)
	 * @return 나간 사람의 닉네임
	 * @throws RoomNotFoundException 방 없음 - 404
	 * @throws RoomNotOpenException 방이 대기 상태가 아님 - 400
	 * @throws RoomNotParticipantException 방 참여자가 아님 - 400
	 */
	@Transactional
	public LeaveRoomResDto modifyLeaveRoom(Long roomId, UserDto userDto) {
		User user = userRepository.getById(userDto.getId());
		Room targetRoom = roomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);
		if (!targetRoom.getStatus().equals(RoomType.OPEN)) {
			throw new RoomNotOpenException();
		}
		UserRoom targetUserRoom = userRoomRepository.findByUserAndRoom(user, targetRoom)
			.orElseThrow(RoomNotParticipantException::new);

		// 모두 나갈 때 방 fail처리
		if (targetRoom.getCurrentPeople() == 1) {
			targetRoom.updateCurrentPeople(0);
			targetUserRoom.updateIsExist(false);
			targetRoom.roomFail();
			roomRepository.save(targetRoom);
			userRoomRepository.save(targetUserRoom);
			return new LeaveRoomResDto(targetUserRoom.getNickname());
		}

		targetRoom.updateCurrentPeople(targetRoom.getCurrentPeople() - 1);
		targetUserRoom.updateIsExist(false);

		// 방장 이권
		if (user.getId().equals(targetRoom.getHost().getId())) {
			List<User> existUser = userRoomRepository.findByIsExist(roomId);
			targetRoom.updateHost(existUser.get(0));
		}

		roomRepository.save(targetRoom);
		userRoomRepository.save(targetUserRoom);

		return new LeaveRoomResDto(targetUserRoom.getNickname());
	}

	/**
	 * 방을 시작 상태로 바꾼다
	 * 방의 상태를 시작 상태로 변경.
	 * @param roomId, user
	 * @return 방 id
	 * @throws RoomNotFoundException 방 없음 - 404
	 * @throws RoomNotParticipantException 방에 참가하지 않은 유저 - 400
	 * @throws UserNotHostException 방장이 아닌 경우 - 403
	 * @throws RoomNotOpenException 방이 열리지 않은 상태 - 400
	 * @throws RoomNotEnoughPeopleException 방에 충분한 인원이 없음 - 400
	 */
	@Transactional
	public RoomStartResDto modifyStartRoom(Long roomId, UserDto userDto) {
		User user = userRepository.getById(userDto.getId());
		Room targetRoom = roomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);
		UserRoom targetUserRoom = userRoomRepository.findByUserAndRoom(user, targetRoom)
			.orElseThrow(RoomNotParticipantException::new);
		if (!targetRoom.getHost().equals(targetUserRoom.getUser())) {
			throw new UserNotHostException();
		}
		if (!targetRoom.getStatus().equals(RoomType.OPEN)) {
			throw new RoomNotOpenException();
		}
		if (targetRoom.getMinPeople() > targetRoom.getCurrentPeople()) {
			throw new RoomNotEnoughPeopleException();
		}
		List<User> users = userRoomRepository.findByIsExist(roomId);
		targetRoom.roomStart();
		partyNotiService.sendPartyNotifications(users);
		roomRepository.save(targetRoom);

		return new RoomStartResDto(targetRoom.getId());
	}

	/**
	 * 방에 참여한다
	 * @param roomId 방 id
	 * @param userDto 유저 정보
	 * @return roomId
	 * @throws RoomNotFoundException 유효하지 않은 방 입력 - 404
	 * @throws OnPenaltyException 패널티 상태인 유저 - 403
	 * @throws RoomNotOpenException 모집중인 방이 아님 - 400
	 * @throws UserAlreadyInRoom 이미 참여한 방 입력 - 409
	 * 과거 참여 이력이 있을 경우 그 아이디로, 없을경우 새로운 아이디를 생성 후 currentPeople을 증가시킨다
	 */
	@Transactional
	public RoomJoinResDto addJoinRoom(Long roomId, UserDto userDto) {
		User user = userRepository.getById(userDto.getId());
		Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
		Optional<PartyPenalty> partyPenalty = partyPenaltyRepository.findTopByUserIdOrderByStartTimeDesc(user.getId());
		if (partyPenalty.isPresent() && PartyPenalty.isFreeFromPenalty(partyPenalty.get())) {
			throw new OnPenaltyException();
		}
		if (!room.getStatus().equals(RoomType.OPEN)) {
			throw new RoomNotOpenException();
		}
		UserRoom userRoom = userRoomRepository.findByUserAndRoom(user, room)
			.orElseGet(() -> {
				String randomNickname;
				do {
					randomNickname = generateRandomNickname();
				} while (userRoomRepository.existsByRoomAndNickname(room, randomNickname));

				UserRoom newUserRoom = new UserRoom(user, room, randomNickname);
				newUserRoom.updateIsExist(false);
				return newUserRoom;
			});
		if (userRoom.getIsExist()) {
			throw new UserAlreadyInRoom(ErrorCode.USER_ALREADY_IN_ROOM);
		} else {
			userRoom.updateIsExist(true);
			userRoomRepository.save(userRoom);
		}

		room.updateCurrentPeople(room.getCurrentPeople() + 1);
		if (room.getCurrentPeople() == room.getMaxPeople()) {
			List<User> users = userRoomRepository.findByIsExist(roomId);
			room.roomStart();
			partyNotiService.sendPartyNotifications(users);
		}
		roomRepository.save(room);
		return new RoomJoinResDto(roomId);
	}
}
