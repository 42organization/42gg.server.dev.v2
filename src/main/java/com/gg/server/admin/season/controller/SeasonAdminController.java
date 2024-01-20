package com.gg.server.admin.season.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.season.dto.SeasonCreateRequestDto;
import com.gg.server.admin.season.dto.SeasonListAdminResponseDto;
import com.gg.server.admin.season.dto.SeasonUpdateRequestDto;
import com.gg.server.admin.season.service.SeasonAdminService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/pingpong/admin")
@AllArgsConstructor
public class SeasonAdminController {
	private final SeasonAdminService seasonAdminService;

	@GetMapping(value = "/seasons")
	public SeasonListAdminResponseDto rankSeasonList() {
		List<SeasonAdminDto> seasons = seasonAdminService.findAllSeasons();

		return new SeasonListAdminResponseDto(seasons);
	}

	@PostMapping(value = "/seasons")
	public synchronized ResponseEntity createSeason(@Valid @RequestBody SeasonCreateRequestDto seasonCreateReqeustDto) {
		seasonAdminService.createSeason(seasonCreateReqeustDto);

		return new ResponseEntity(HttpStatus.CREATED);
	}

	@DeleteMapping(value = "/seasons/{seasonId}")
	public synchronized ResponseEntity deleteSeason(@PathVariable Long seasonId) {
		seasonAdminService.deleteSeason(seasonId);

		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PutMapping(value = "/seasons/{seasonId}")
	public synchronized ResponseEntity updateSeason(@PathVariable Long seasonId,
		@RequestBody SeasonUpdateRequestDto seasonUpdateRequestDto) {
		seasonAdminService.updateSeason(seasonId, seasonUpdateRequestDto);

		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
