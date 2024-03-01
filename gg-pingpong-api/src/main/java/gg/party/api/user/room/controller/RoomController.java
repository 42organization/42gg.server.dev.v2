package gg.party.api.user.room.controller;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.party.api.user.room.controller.request.RoomCreateReqDto;
import gg.party.api.user.room.controller.response.RoomListResDto;
import gg.party.api.user.room.service.RoomService;
import gg.pingpong.api.global.utils.argumentresolver.Login;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.repo.party.RoomRepository;
import gg.utils.exception.custom.InvalidParameterException;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/rooms")
public class RoomController {
	private final RoomService roomService;
	private final RoomRepository roomRepository;


	/**
	 * 시작하지 않은 방과 시작한 방을 모두 조회한다
	 * @return 시작하지 않은 방 (최신순) + 시작한 방(끝나는 시간이 빠른 순) 전체 List
	 */
	@Transactional
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
	@Transactional
	@PostMapping
	public ResponseEntity<Long> createRoom(@RequestBody RoomCreateReqDto roomCreateReqDto,
		@Parameter(hidden = true) @Login UserDto user) {
		Long longRoomId = roomService.addOrderCreateRoom(roomCreateReqDto, user);
		return ResponseEntity.status(HttpStatus.CREATED).body(longRoomId);
	}

	/**
	 * 시간이 지나 보이지 않게 된 방을 모두 조회한다
	 * @return 끝난 방 전체 List
	 */
	@Transactional
	@GetMapping("/history")
	public ResponseEntity<RoomListResDto> allHistoryRoomList() {
		RoomListResDto roomListResDto = roomService.findOrderHistoryRoomList();
		return ResponseEntity.status(HttpStatus.OK).body(roomListResDto);
	}

}
