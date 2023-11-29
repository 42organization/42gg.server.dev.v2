package com.gg.server.domain.tournament.controller;

import com.gg.server.domain.tournament.dto.TournamentFilterRequestDto;
import com.gg.server.domain.tournament.dto.TournamentListResponseDto;
import com.gg.server.domain.tournament.service.TournamentService;
import com.gg.server.global.dto.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/tournament")
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
}
