package com.gg.server.domain.megaphone.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gg.server.domain.megaphone.dto.MegaphoneDetailResponseDto;
import com.gg.server.domain.megaphone.dto.MegaphoneTodayListResponseDto;
import com.gg.server.domain.megaphone.dto.MegaphoneUseRequestDto;
import com.gg.server.domain.megaphone.service.MegaphoneService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.argumentresolver.Login;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/megaphones")
public class MegaphoneController {
	private final MegaphoneService megaphoneService;

	@PostMapping()
	public ResponseEntity useMegaphone(@RequestBody @Valid MegaphoneUseRequestDto megaphoneUseRequestDto,
		@Parameter(hidden = true) @Login UserDto user) {
		megaphoneService.useMegaphone(megaphoneUseRequestDto, user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/{megaphoneId}")
	public ResponseEntity deleteMegaphone(@PathVariable Long megaphoneId,
		@Parameter(hidden = true) @Login UserDto user) {
		megaphoneService.deleteMegaphone(megaphoneId, user);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/receipt/{receiptId}")
	public ResponseEntity<MegaphoneDetailResponseDto> getMegaphoneDetail(@PathVariable Long receiptId,
		@Parameter(hidden = true) @Login UserDto user) {
		return ResponseEntity.ok(megaphoneService.getMegaphoneDetail(receiptId, user));
	}

	@GetMapping()
	public ResponseEntity<List<MegaphoneTodayListResponseDto>> getMegaphoneTodayList(
		@Parameter(hidden = true) @Login UserDto user) {
		return ResponseEntity.ok(megaphoneService.getMegaphoneTodayList());
	}
}
