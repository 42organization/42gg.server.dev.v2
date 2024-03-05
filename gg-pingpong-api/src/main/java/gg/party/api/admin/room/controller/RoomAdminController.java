package gg.party.api.admin.room.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.data.party.type.RoomType;
import gg.party.api.admin.room.controller.request.RoomShowChangeReqDto;
import gg.party.api.admin.room.service.RoomAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/admin/rooms")
public class RoomAdminController {

	private final RoomAdminService roomAdminService;

	@PatchMapping("/{roomId}")
	public ResponseEntity<Void> changeRoomVisibility(@PathVariable Long roomId,
		@Valid @RequestBody RoomShowChangeReqDto reqDto) {

		RoomType roomType;
		try {
			roomType = RoomType.valueOf(reqDto.getStatus().toUpperCase());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}

		roomAdminService.modifyRoomStatus(roomId, roomType);
		return ResponseEntity.ok().build();
	}
}
