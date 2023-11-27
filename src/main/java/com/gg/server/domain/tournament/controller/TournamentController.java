package com.gg.server.domain.tournament.controller;

import com.gg.server.domain.tournament.dto.TournamentFilterRequestDto;
import com.gg.server.domain.tournament.dto.TournamentListResponseDto;
import com.gg.server.domain.tournament.service.TournamentService;
import com.gg.server.global.dto.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/tournament")
public class TournamentController {

    private final TournamentService tournamentService;

    @GetMapping
    TournamentListResponseDto getAllTournamentList(@Valid PageRequestDto pageRequestDto,
                                                   @Valid TournamentFilterRequestDto tournamentFilterRequestDto){
        PageRequest pageRequest = PageRequest.of(pageRequestDto.getPage() - 1, pageRequestDto.getSize());

        return tournamentService.getAllTournamentList(pageRequest, tournamentFilterRequestDto.getType(), tournamentFilterRequestDto.getStatus());
    }
}
