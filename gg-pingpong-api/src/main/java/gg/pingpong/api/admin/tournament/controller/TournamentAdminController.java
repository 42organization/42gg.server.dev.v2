package gg.pingpong.api.admin.tournament.controller;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.pingpong.api.admin.tournament.controller.request.TournamentAdminAddUserRequestDto;
import gg.pingpong.api.admin.tournament.controller.request.TournamentAdminCreateRequestDto;
import gg.pingpong.api.admin.tournament.controller.request.TournamentAdminUpdateRequestDto;
import gg.pingpong.api.admin.tournament.controller.request.TournamentGameUpdateRequestDto;
import gg.pingpong.api.admin.tournament.controller.response.TournamentAdminAddUserResponseDto;
import gg.pingpong.api.admin.tournament.service.TournamentAdminService;
import gg.pingpong.api.user.tournament.dto.TournamentUserListResponseDto;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/pingpong/admin/tournaments")
@Validated
public class TournamentAdminController {
	private final TournamentAdminService tournamentAdminService;

	/***
	 * 토너먼트 생성
	 * @param tournamentAdminCreateRequestDto 생성에 필요한 데이터
	 * @return "CREATED" 응답 코드
	 */
	@PostMapping()
	public ResponseEntity<Void> createTournament(
		@RequestBody @Valid TournamentAdminCreateRequestDto tournamentAdminCreateRequestDto) {
		tournamentAdminService.createTournament(tournamentAdminCreateRequestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * <p>토너먼트 정보 수정</p>
	 * @param tournamentId 업데이트 하고자 하는 토너먼트 id
	 * @param tournamentAdminUpdateRequestDto 요청 데이터
	 * @return HttpStatus.NO_CONTENT
	 */
	@PatchMapping("/{tournamentId}")
	public ResponseEntity<Void> updateTournamentInfo(@PathVariable @Positive Long tournamentId,
		@Valid @RequestBody TournamentAdminUpdateRequestDto tournamentAdminUpdateRequestDto) {
		tournamentAdminService.updateTournamentInfo(tournamentId, tournamentAdminUpdateRequestDto);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * <p>토너먼트 정보 삭제</p>
	 * @param tournamentId 삭제하고자 하는 토너먼트 id
	 * @return HttpStatus.NO_CONTENT
	 */
	@DeleteMapping("/{tournamentId}")
	public ResponseEntity<Void> deleteTournament(@PathVariable @Positive Long tournamentId) {
		tournamentAdminService.deleteTournament(tournamentId);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * <p>토너먼트 유저 추가</p>
	 * <p>이미 해당 토너먼트에 참여중이거나 대기자인 유저는 신청할 수 없다.</p>
	 * @param tournamentId 유저를 추가할 토너먼트 id
	 * @param tournamentAdminUserAddRequestDto 요청 데이터
	 * @return TournamentAdminAddUserResponseDto, HttpStatus.CREATED
	 */
	@PostMapping("/{tournamentId}/users")
	public ResponseEntity<TournamentAdminAddUserResponseDto> addTournamentUser(
		@PathVariable @Positive Long tournamentId,
		@Valid @RequestBody TournamentAdminAddUserRequestDto tournamentAdminUserAddRequestDto) {
		TournamentAdminAddUserResponseDto responseDto = tournamentAdminService.addTournamentUser(tournamentId,
			tournamentAdminUserAddRequestDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	/**
	 * <p>토너먼트 유저 조회</p>
	 * @param tournamentId 유저를 조회할 토너먼트 id
	 * @param isJoined 참여중인 유저만 조회할지 여부
	 * @return TournamentUserListResponseDto
	 */
	@GetMapping("/{tournamentId}/users")
	public ResponseEntity<TournamentUserListResponseDto> getTournamentUserList(
		@PathVariable @Positive Long tournamentId,
		@RequestParam(required = false) Boolean isJoined) {
		TournamentUserListResponseDto responseDto = tournamentAdminService.getTournamentUserList(tournamentId,
			isJoined);
		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}

	/**
	 * <p>관리자 토너먼트 유저 삭제</p>
	 * <p>토너먼트 유저를 삭제시켜 주며, 참가자가 삭제되는 경우 상황에 따라 대기자를 참가자로 바꾸어준다.</p>
	 * @param tournamentId 타겟 토너먼트 id
	 * @param userId 타겟 유저 id
	 */
	@DeleteMapping("/{tournamentId}/users/{userId}")
	public ResponseEntity<Void> deleteTournamentUser(@PathVariable @Positive Long tournamentId,
		@PathVariable @Positive Long userId) {
		tournamentAdminService.deleteTournamentUser(tournamentId, userId);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * <p>토너먼트 게임 점수 정보 수정</p>
	 * @param tournamentId 타겟 토너먼트 id
	 * @param tournamentGameUpdateReqDto 수정할 게임 정보
	 * @return HttpStatus.OK
	 */
	@PatchMapping("{tournamentId}/games")
	public ResponseEntity<Void> updateTournamentGame(@PathVariable @Positive Long tournamentId,
		@Valid @RequestBody TournamentGameUpdateRequestDto tournamentGameUpdateReqDto) {
		tournamentAdminService.updateTournamentGame(tournamentId, tournamentGameUpdateReqDto);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
