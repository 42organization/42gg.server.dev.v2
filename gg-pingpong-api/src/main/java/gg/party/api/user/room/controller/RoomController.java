package gg.party.api.user.room.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.party.api.user.room.controller.request.RoomCreateReqDto;
import gg.party.api.user.room.controller.response.RoomDetailResDto;
import gg.party.api.user.room.controller.response.RoomListResDto;
import gg.party.api.user.room.service.RoomService;
import gg.pingpong.api.global.utils.argumentresolver.Login;
import gg.pingpong.api.user.user.dto.UserDto;
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
		RoomListResDto roomListResDto = roomService.findOrderRoomList();
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
		Long roomId = roomService.addOrderCreateRoom(roomCreateReqDto, user);
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
		RoomListResDto roomListResDto = roomService.findOrderMyHistoryRoomList(user.getId());
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
		RoomDetailResDto roomDetailResDto = roomService.findOrderRoomDetail(user.getId(), roomId);
		return ResponseEntity.status(HttpStatus.OK).body(roomDetailResDto);
	}
}
