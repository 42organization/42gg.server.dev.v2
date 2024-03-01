package gg.party.api.user.room.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.party.api.user.room.controller.response.RoomListResDto;
import gg.party.api.user.room.service.RoomService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/rooms")
public class RoomController {
	private final RoomService roomService;

	@GetMapping
	public ResponseEntity<RoomListResDto> allActiveRoomList() {
		RoomListResDto roomListResDto = roomService.findOrderRoomList();
		return ResponseEntity.ok(roomListResDto);
	}

	@GetMapping("/history")
	public ResponseEntity<RoomListResDto> allHistoryRoomList() {
		RoomListResDto roomListResDto = roomService.findOrderHistoryRoomList();
		return ResponseEntity.ok(roomListResDto);
	}
}

