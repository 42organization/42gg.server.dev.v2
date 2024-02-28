package gg.pingpong.api.admin.manage.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.pingpong.api.admin.manage.controller.request.SlotCreateRequestDto;
import gg.pingpong.api.admin.manage.controller.response.SlotListAdminResponseDto;
import gg.pingpong.api.admin.manage.service.SlotAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/slot-management")
public class SlotAdminController {
	private final SlotAdminService slotAdminService;

	@GetMapping
	public SlotListAdminResponseDto getSlotSetting() {

		return slotAdminService.getSlotSetting();
	}

	@PostMapping
	public synchronized ResponseEntity addSlotSetting(@Valid @RequestBody SlotCreateRequestDto requestDto) {
		slotAdminService.addSlotSetting(requestDto);
		return new ResponseEntity(HttpStatus.CREATED);
	}

	@DeleteMapping
	public synchronized ResponseEntity delSlotSetting() {
		slotAdminService.delSlotSetting();
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

}
