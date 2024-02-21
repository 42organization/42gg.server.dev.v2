package gg.pingpong.api.user.noti.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.pingpong.api.global.utils.argumentresolver.Login;
import gg.pingpong.api.user.noti.dto.NotiListResponseDto;
import gg.pingpong.api.user.noti.dto.NotiResponseDto;
import gg.pingpong.api.user.noti.service.NotiService;
import gg.pingpong.api.user.user.dto.UserDto;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/pingpong/notifications")
public class NotiController {
	private final NotiService notiService;

	@GetMapping
	public NotiListResponseDto notiFindByUser(@Login UserDto user) {
		List<NotiResponseDto> notiResponseDtoList = notiService.findNotiByUser(user);
		return new NotiListResponseDto(notiResponseDtoList);
	}

	@PutMapping(value = "/check")
	public ResponseEntity checkNotiByUser(@Login UserDto user) {
		notiService.modifyNotiCheckedByUser(user);
		return ResponseEntity.status(204).build();
	}

	@DeleteMapping
	public ResponseEntity notiRemoveAll(@Login UserDto user) {
		notiService.removeAllNotisByUser(user);
		return ResponseEntity.status(204).build();
	}
}
