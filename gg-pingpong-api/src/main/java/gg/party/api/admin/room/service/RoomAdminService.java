package gg.party.api.admin.room.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.party.api.admin.room.controller.request.PageReqDto;
import gg.party.api.admin.room.controller.response.AdminCommentResDto;
import gg.party.api.admin.room.controller.response.AdminRoomDetailResDto;
import gg.party.api.admin.room.controller.response.AdminRoomListResDto;
import gg.party.api.admin.room.controller.response.AdminRoomResDto;
import gg.party.api.user.room.controller.response.UserRoomResDto;
import gg.repo.party.CommentRepository;
import gg.repo.party.RoomRepository;
import gg.repo.party.UserRoomRepository;
import gg.utils.exception.party.ChangeSameStatusException;
import gg.utils.exception.party.RoomNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomAdminService {
	private final RoomRepository roomRepository;
	private final UserRoomRepository userRoomRepository;
	private final CommentRepository commentRepository;

	/**
	 * 방 Status 변경
	 * @param roomId 방 id
	 * @param newStatus 바꿀 status
	 * @exception RoomNotFoundException 유효하지 않은 방 입력 - 404
	 * @exception ChangeSameStatusException 같은 상태로 변경 - 409
	 */
	@Transactional
	public void modifyRoomStatus(Long roomId, RoomType newStatus) {
		Room room = roomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);

		if (room.getStatus() == newStatus) {
			throw new ChangeSameStatusException();
		}

		room.changeRoomStatus(newStatus);
		roomRepository.save(room);
	}

	/**
	 * 방 전체 조회
	 * @param pageReqDto page번호 및 사이즈(10)
	 * @return 방 정보 리스트 + totalpages dto
	 */
	@Transactional(readOnly = true)
	public AdminRoomListResDto findAllRoomList(PageReqDto pageReqDto) {
		int page = pageReqDto.getPage();
		int size = pageReqDto.getSize();

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<Room> roomPage = roomRepository.findAll(pageable);

		List<AdminRoomResDto> adminRoomListResDto = roomPage.getContent().stream()
			.map(AdminRoomResDto::new)
			.collect(Collectors.toList());

		return new AdminRoomListResDto(adminRoomListResDto, roomPage.getTotalPages());
	}

	/**
	 * 방의 상세정보를 조회한다
	 * @param roomId 방 id
	 * @exception RoomNotFoundException 유효하지 않은 방 입력 - 404
	 * @return 방 상세정보 dto
	 */
	@Transactional(readOnly = true)
	public AdminRoomDetailResDto findAdminDetailRoom(Long roomId) {
		Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);

		List<AdminCommentResDto> comments = commentRepository.findAllWithCommentFetchJoin(roomId).stream()
			.map(AdminCommentResDto::new)
			.collect(Collectors.toList());

		UserRoom hostUserRoom = userRoomRepository.findByUserAndRoom(room.getHost(), room)
			.orElseThrow(RoomNotFoundException::new);

		List<UserRoomResDto> roomUsers = userRoomRepository.findByRoomId(roomId).stream()
			.filter(UserRoom::getIsExist)
			.map(userRoom -> new UserRoomResDto(userRoom, userRoom.getUser().getIntraId(),
				userRoom.getUser().getImageUri()))
			.collect(Collectors.toList());

		return new AdminRoomDetailResDto(room, hostUserRoom.getNickname(), roomUsers, comments);
	}
}
