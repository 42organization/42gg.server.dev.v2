package gg.pingpong.api.party.admin.room.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import gg.party.api.admin.room.controller.request.RoomShowChangeReqDto;
import gg.data.party.type.RoomType;
import gg.party.api.admin.room.service.RoomAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/admin/rooms")
public class RoomAdminController {

	private final RoomAdminService roomAdminService; // Assume this service can update rooms

	@PatchMapping("/{roomId}/visibility")
	public ResponseEntity<Void> changeRoomVisibility(@PathVariable Long roomId, @RequestBody RoomShowChangeReqDto reqDto) {
		RoomType newStatus = reqDto.isHidden() ? RoomType.HIDDEN : RoomType.OPEN; // if true, become HIDDEN, if false, become OPEN
		roomAdminService.updateRoomStatus(roomId, newStatus); // Assume this method exists
		return ResponseEntity.ok().build();
	}
}
