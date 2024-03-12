package gg.party.api.admin.room.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.data.party.type.RoomType;
import gg.party.api.admin.room.controller.request.PageReqDto;
import gg.party.api.admin.room.controller.request.RoomShowChangeReqDto;
import gg.party.api.admin.room.controller.response.AdminRoomDetailResDto;
import gg.party.api.admin.room.controller.response.AdminRoomListResDto;
import gg.party.api.admin.room.service.RoomAdminService;
import gg.utils.exception.party.RoomNotFoundException;
import gg.utils.exception.party.RoomStatNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/admin/rooms")
public class RoomAdminController {
	private final RoomAdminService roomAdminService;

	/**
	 * 방 Status 변경
	 * @param roomId 방 id
	 * @param reqDto 바꿀 status
	 * @exception RoomNotFoundException 유효하지 않은 방 입력
	 */
	@PatchMapping("/{roomId}")
	public ResponseEntity<Void> changeRoomVisibility(@PathVariable Long roomId,
		@Valid @RequestBody RoomShowChangeReqDto reqDto) throws RoomStatNotFoundException {

		RoomType roomType;
		try {
			roomType = RoomType.valueOf(reqDto.getStatus().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new RoomStatNotFoundException();
		}

		roomAdminService.modifyRoomStatus(roomId, roomType);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * 방 전체 조회
	 * @param pageReqDto page번호 및 사이즈(10)
	 * @return 방 정보 리스트 + totalpages dto
	 */
	@GetMapping
	public ResponseEntity<AdminRoomListResDto> adminAllRoomList(@ModelAttribute @Valid PageReqDto pageReqDto) {
		AdminRoomListResDto adminRoomListResDto = roomAdminService.findAllRoomList(pageReqDto);
		return ResponseEntity.status(HttpStatus.OK).body(adminRoomListResDto);
	}

	/**
	 * 방 전체 조회
	 * @return 방 상세정보 (들어와 있지 않은 사람의 intraId 포함)
	 */
	@GetMapping("/{room_id}")
	public ResponseEntity<AdminRoomDetailResDto> adminRoomDetailInfo(@PathVariable("room_id") Long roomId) {
		AdminRoomDetailResDto adminRoomDetailResDto = roomAdminService.findAdminDetailRoom(roomId);
		return ResponseEntity.status(HttpStatus.OK).body(adminRoomDetailResDto);
	}
}
