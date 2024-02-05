package com.gg.server.domain.tournament.controller;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gg.server.domain.tournament.dto.TournamentFilterRequestDto;
import com.gg.server.domain.tournament.dto.TournamentGameListResponseDto;
import com.gg.server.domain.tournament.dto.TournamentListResponseDto;
import com.gg.server.domain.tournament.dto.TournamentResponseDto;
import com.gg.server.domain.tournament.dto.TournamentUserRegistrationResponseDto;
import com.gg.server.domain.tournament.service.TournamentService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.argumentresolver.Login;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/tournaments")
@Validated
public class TournamentController {

	private final TournamentService tournamentService;

	/**
	 * 토너먼트 리스트 조회
	 * @param tournamentFilterRequestDto Enum 필터 정보 (page, size, type, status)
	 * @return 토너먼트 리스트
	 */
	@GetMapping
	public ResponseEntity<TournamentListResponseDto> getAllTournamentList(
		@ModelAttribute @Valid TournamentFilterRequestDto tournamentFilterRequestDto) {
		Pageable pageRequest = PageRequest.of(tournamentFilterRequestDto.getPage() - 1,
			tournamentFilterRequestDto.getSize(), Sort.by("startTime").ascending());
		return ResponseEntity.status(HttpStatus.OK)
			.body(tournamentService.getAllTournamentList(pageRequest, tournamentFilterRequestDto.getType(),
				tournamentFilterRequestDto.getStatus()));
	}

	/**
	 * <p>유저 해당 토너먼트 참여 여부 확인 매서드</p>
	 * @param tournamentId 타겟 토너먼트
	 * @param user 확인하고자 하는 유저(로그인한 유저 본인)
	 * @return
	 */
	@GetMapping("/{tournamentId}/users")
	ResponseEntity<TournamentUserRegistrationResponseDto> getUserStatusInTournament(@PathVariable Long tournamentId,
		@Parameter(hidden = true) @Login UserDto user) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(tournamentService.getUserStatusInTournament(tournamentId, user));
	}

	/**
	 * 토너먼트 단일 조회
	 * @param tournamentId 토너먼트 id
	 * @return 토너먼트
	 */
	@GetMapping("/{tournamentId}")
	public ResponseEntity<TournamentResponseDto> getTournnament(@PathVariable @Positive Long tournamentId) {
		TournamentResponseDto tournamentResponseDto = tournamentService.getTournament(tournamentId);
		return ResponseEntity.status(HttpStatus.OK).body(tournamentResponseDto);
	}

	@DeleteMapping("/{tournamentId}/users")
	ResponseEntity<TournamentUserRegistrationResponseDto> cancelTournamentUserRegistration(
		@PathVariable Long tournamentId, @Parameter(hidden = true) @Login UserDto user) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(tournamentService.cancelTournamentUserRegistration(tournamentId, user));
	}

	/**
	 * 토너먼트 게임 리스트 조회
	 * @param tournamentId 토너먼트 id
	 * @return 토너먼트 게임 리스트
	 */
	@GetMapping("/{tournamentId}/games")
	public ResponseEntity<TournamentGameListResponseDto> getTournamentGames(@PathVariable @Positive Long tournamentId) {
		return ResponseEntity.status(HttpStatus.OK).body(tournamentService.getTournamentGames(tournamentId));
	}

	/**
	 * <p>토너먼트 참가 신청</p>
	 * <p>토너먼트 최대 인원보다 이미 많이 신청했다면 대기자로 들어간다</p>
	 * @param tournamentId 타겟 토너먼트 id
	 * @param user 신청 유저(본인)
	 * @return
	 */
	@PostMapping("/{tournamentId}/users")
	ResponseEntity<TournamentUserRegistrationResponseDto> registerTournamentUser(@PathVariable Long tournamentId,
		@Parameter(hidden = true) @Login UserDto user) {

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(tournamentService.registerTournamentUser(tournamentId, user));
	}
}
