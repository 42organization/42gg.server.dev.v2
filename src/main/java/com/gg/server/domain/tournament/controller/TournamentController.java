package com.gg.server.domain.tournament.controller;

import com.gg.server.domain.tournament.dto.TournamentCheckParticipationResponseDto;
import com.gg.server.domain.tournament.dto.TournamentFilterRequestDto;
import com.gg.server.domain.tournament.dto.TournamentListResponseDto;
import com.gg.server.domain.tournament.dto.TournamentResponseDto;
import com.gg.server.domain.tournament.service.TournamentService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.index.qual.Positive;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    TournamentListResponseDto getAllTournamentList(@ModelAttribute @Valid TournamentFilterRequestDto tournamentFilterRequestDto){
        Pageable pageRequest = PageRequest.of(tournamentFilterRequestDto.getPage() - 1, tournamentFilterRequestDto.getSize());

        return tournamentService.getAllTournamentList(pageRequest, tournamentFilterRequestDto.getType(), tournamentFilterRequestDto.getStatus());
    }

    @GetMapping("/{tournamentId}/users")
    ResponseEntity<TournamentCheckParticipationResponseDto> getUserStatusInTournament(@PathVariable Long tournamentId, @Parameter(hidden = true) @Login UserDto user) {

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
}
