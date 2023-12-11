package com.gg.server.domain.tournament.controller;

import com.gg.server.domain.tournament.dto.*;
import com.gg.server.domain.tournament.service.TournamentService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.index.qual.Positive;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    /**
     * 토너먼트 리스트 조회
     * @param tournamentFilterRequestDto Enum 필터 정보 (page, size, type, status)
     * @return 토너먼트 리스트
     */
    @GetMapping
    public ResponseEntity<TournamentListResponseDto> getAllTournamentList(@ModelAttribute @Valid TournamentFilterRequestDto tournamentFilterRequestDto){
        Pageable pageRequest = PageRequest.of(tournamentFilterRequestDto.getPage() - 1, tournamentFilterRequestDto.getSize(), Sort.by("startTime").descending());
        return ResponseEntity.ok().
                body(tournamentService.getAllTournamentList(pageRequest, tournamentFilterRequestDto.getType(), tournamentFilterRequestDto.getStatus()));
    }

    /**
     * <p>유저 해당 토너먼트 참여 여부 확인 매서드</p>
     * @param tournamentId 타겟 토너먼트
     * @param user 확인하고자 하는 유저(로그인한 유저 본인)
     * @return
     */
    @GetMapping("/{tournamentId}/users")
    ResponseEntity<TournamentUserRegistrationResponseDto> getUserStatusInTournament(@PathVariable Long tournamentId, @Parameter(hidden = true) @Login UserDto user) {

        return ResponseEntity.ok().body(tournamentService.getUserStatusInTournament(tournamentId, user));
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

    /**
     * 토너먼트 게임 리스트 조회
     * @param tournamentId 토너먼트 id
     * @return 토너먼트 게임 리스트
     */
    @GetMapping("/{tournamentId}/games")
    public ResponseEntity<TournamentGameListResponseDto> getTournamentGames(@PathVariable @Positive Long tournamentId){
        return ResponseEntity.ok().body(tournamentService.getTournamentGames(tournamentId));
    }
}
