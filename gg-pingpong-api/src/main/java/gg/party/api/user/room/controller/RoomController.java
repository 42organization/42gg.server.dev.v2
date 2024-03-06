package gg.party.api.user.room.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.party.api.user.room.controller.request.RoomCreateReqDto;
import gg.party.api.user.room.controller.response.LeaveRoomResDto;
import gg.party.api.user.room.controller.response.RoomDetailResDto;
import gg.party.api.user.room.controller.response.RoomJoinResDto;
import gg.party.api.user.room.controller.response.RoomListResDto;
import gg.party.api.user.room.service.RoomService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/rooms")
public class RoomController {
	private final RoomService roomService;

	/**
	 * 시작하지 않은 방과 시작한 방을 모두 조회한다
	 * @return 시작하지 않은 방 (최신순) + 시작한 방(끝나는 시간이 빠른 순) 전체 List
	 */
	@GetMapping
	public ResponseEntity<RoomListResDto> allActiveRoomList() {
		RoomListResDto roomListResDto = roomService.findRoomList();
		return ResponseEntity.status(HttpStatus.OK).body(roomListResDto);
	}

	/**
	 * 방 만들기
	 * @param roomCreateReqDto 요청 Dto
	 * @param user 사용자
	 * @return 201 created
	 */
	@PostMapping
	public ResponseEntity<Long> createRoom(@RequestBody RoomCreateReqDto roomCreateReqDto,
		@Parameter(hidden = true) @Login UserDto user) {
		Long roomId = roomService.addCreateRoom(roomCreateReqDto, user);
		return ResponseEntity.status(HttpStatus.CREATED).body(roomId);
	}

	/**
	 * 참여중인 방을 모두 조회한다(만든 방 포함)
	 * @return 참여중인 방 전체 List
	 */
	@GetMapping("/joined")
	public ResponseEntity<RoomListResDto> allJoinedRoomList(@Parameter(hidden = true) @Login UserDto user) {
		RoomListResDto roomListResDto = roomService.findOrderJoinedRoomList(user.getId());
		return ResponseEntity.status(HttpStatus.OK).body(roomListResDto);
	}

	/**
	 * 시간이 지나 보이지 않게 된 내가 플레이한(시작한) 방을 모두 조회한다
	 * @return 끝난 방 전체 List
	 */
	@GetMapping("/history")
	public ResponseEntity<RoomListResDto> myHistoryRoomList(@Parameter(hidden = true) @Login UserDto user) {
		RoomListResDto roomListResDto = roomService.findMyHistoryRoomList(user.getId());
		return ResponseEntity.status(HttpStatus.OK).body(roomListResDto);
	}

	/**
	 * 방의 상세정보를 조회한다
	 * @param roomId 방 id
	 * 익명성을 지키기 위해 nickname을 리턴
	 * @return 방 상세정보 dto
	 */
	@GetMapping("/{room_id}")
	public ResponseEntity<RoomDetailResDto> roomDetailInfo(@Parameter(hidden = true) @Login UserDto user,
		@PathVariable("room_id") Long roomId) {
		RoomDetailResDto roomDetailResDto = roomService.findRoomDetail(user.getId(), roomId);
		return ResponseEntity.status(HttpStatus.OK).body(roomDetailResDto);
	}

	/**
	 * 방에 참여한다
	 * @param roomId 방 id
	 * @param user 유저 정보
	 * @return roomId
	 */
	@PatchMapping("/{room_id}")
	public ResponseEntity<LeaveRoomResDto> leaveRoom(@PathVariable("room_id") Long roomId,
		@Parameter(hidden = true) @Login UserDto user) {
		return ResponseEntity.status(HttpStatus.OK).body(roomService.modifyLeaveRoom(roomId, user));
	}

	/**
	 * 방 시작하기
	 * @return 방 id
	 */
	@PostMapping("/{room_id}/start")
	public ResponseEntity<Long> startRoom(@PathVariable("room_id") Long roomId,
		@Parameter(hidden = true) @Login UserDto user) {
		return ResponseEntity.status(HttpStatus.CREATED).body(roomService.modifyStartRoom(roomId, user));
	}

	/**
	 * 방에 참여한다
	 * @param roomId 방 id
	 * @param user 유저 정보
	 * @return roomId
	 */
	@PostMapping("/{room_id}")
	public ResponseEntity<RoomJoinResDto> joinRoom(@Parameter(hidden = true) @Login UserDto user,
		@PathVariable("room_id") Long roomId) {
		RoomJoinResDto roomJoinResDto = roomService.addJoinRoom(roomId, user);
		return ResponseEntity.status(HttpStatus.CREATED).body(roomJoinResDto);
	}
}
