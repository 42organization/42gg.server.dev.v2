package gg.party.api.admin.room.service;

import java.util.List;
import java.util.Optional;
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
import gg.party.api.admin.room.controller.response.AdminRoomDetailResDto;
import gg.party.api.admin.room.controller.response.AdminRoomListResDto;
import gg.party.api.admin.room.controller.response.AdminRoomResDto;
import gg.party.api.user.room.controller.response.CommentResDto;
import gg.party.api.user.room.controller.response.UserRoomResDto;
import gg.repo.party.CommentRepository;
import gg.repo.party.RoomRepository;
import gg.repo.party.UserRoomRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.party.RoomNotFoundException;
import gg.utils.exception.party.RoomReportedException;
import gg.utils.exception.party.RoomSameStatusException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomAdminService {
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	private final UserRoomRepository userRoomRepository;
	private final CommentRepository commentRepository;

	/**
	 * 방 Status 변경
	 * @param roomId 방 id
	 * @param newStatus 바꿀 status
	 * @exception RoomNotFoundException 유효하지 않은 방 입력
	 * @exception RoomSameStatusException 같은 상태로 변경
	 */
	@Transactional
	public void modifyRoomStatus(Long roomId, RoomType newStatus) {
		Room room = roomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);

		if (room.getStatus() == newStatus) {
			throw new RoomSameStatusException();
		}

		room.updateRoomStatus(newStatus);
		roomRepository.save(room);
	}

	/**
	 * 방 전체 조회
	 * @param pageReqDto page번호 및 사이즈(10)
	 * @return 방 정보 리스트 + totalpages dto
	 */
	@Transactional
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
	 * @param userId 자신의 id
	 * @param roomId 방 id
	 * @exception RoomNotFoundException 유효하지 않은 방 입력
	 * @exception RoomReportedException 신고 받은 방 처리 | 시작한 방도 볼 수 있게 해야하므로 별도처리
	 * 익명성을 지키기 위해 nickname을 리턴
	 * @return 방 상세정보 dto
	 */
	@Transactional
	public AdminRoomDetailResDto findAdminDetailRoom(Long userId, Long roomId) {
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

		List<CommentResDto> comments = commentRepository.findByRoomId(roomId).stream()
			.map(
				comment -> new CommentResDto(comment.getId(), comment.getUserRoom().getNickname(), comment.getContent(),
					comment.isHidden(), comment.getCreatedAt()))
			.collect(Collectors.toList());

		Optional<UserRoom> hostUserRoomOptional = userRoomRepository.findByUserIdAndRoomIdAndIsExistTrue(
			room.getHost().getId(), roomId);
		String hostNickname = hostUserRoomOptional.get().getNickname();

		List<UserRoomResDto> roomUsers = userRoomRepository.findByRoomId(roomId).stream()
			.map(userRoom -> new UserRoomResDto(userRoom.getId(), userRoom.getNickname(),
				userRoom.getUser().getIntraId()))
			.collect(Collectors.toList());

		return new AdminRoomDetailResDto(room, myNickname, hostNickname, roomUsers, comments);
	}

}
