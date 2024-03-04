package gg.party.api.admin.room.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import gg.data.party.type.RoomType;
import gg.party.api.admin.room.controller.request.RoomShowChangeReqDto;
import gg.party.api.admin.room.service.RoomAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/rooms")
public class RoomAdminController {

	private final RoomAdminService roomAdminService;

	@PatchMapping("/{roomId}")
	public ResponseEntity<Void> changeRoomVisibility(@PathVariable Long roomId, @RequestBody RoomShowChangeReqDto reqDto) {
		// System.out.println("Request received to change visibility for room ID: " + roomId);

		if (reqDto.getStatus() == null) {
			// Return a bad request response or handle the null status as needed
			return ResponseEntity.badRequest().build();
		}

		RoomType roomType;
		try {
			roomType = RoomType.valueOf(reqDto.getStatus().toUpperCase());
		} catch (IllegalArgumentException e) {
			// Handle invalid RoomType values appropriately
			return ResponseEntity.badRequest().build();
		}

		roomAdminService.updateRoomStatus(roomId, roomType);
		return ResponseEntity.ok().build();
	}
}
