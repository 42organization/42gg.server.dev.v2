package gg.party.api.user.room.controller;

import javax.transaction.Transactional;

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
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/rooms")
public class RoomController {
	private final RoomService roomService;
	private final RoomRepository roomRepository;

	@Transactional
	@GetMapping
	public ResponseEntity<RoomListResDto> allActiveRoomList() {
		RoomListResDto roomListResDto = roomService.findOrderRoomList();
		return ResponseEntity.ok(roomListResDto);
	}

	@Transactional
	@GetMapping("/history")
	public ResponseEntity<RoomListResDto> allHistoryRoomList() {
		RoomListResDto roomListResDto = roomService.findOrderHistoryRoomList();
		return ResponseEntity.ok(roomListResDto);
	}

	@Transactional
	@PostMapping
	public ResponseEntity<Long> createRoom(@RequestBody RoomCreateReqDto roomCreateReqDto,
		@Parameter(hidden = true) @Login UserDto user) {
		Long roomId = roomService.addOrderCreateRoom(roomCreateReqDto, user);
		return ResponseEntity.ok(roomId);
	}
}
