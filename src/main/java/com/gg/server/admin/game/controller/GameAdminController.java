package com.gg.server.admin.game.controller;

import com.gg.server.admin.game.dto.GameLogAdminRequestDto;
import com.gg.server.admin.game.dto.GameLogListAdminResponseDto;
import com.gg.server.admin.game.dto.GameUserLogAdminReqDto;
import com.gg.server.admin.game.service.GameAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/games")
public class GameAdminController {
    private final GameAdminService gameAdminService;

    @GetMapping
    public GameLogListAdminResponseDto gameFindBySeasonId(@ModelAttribute GameLogAdminRequestDto gameLogAdminRequestDto) {
        Long seasonId = gameLogAdminRequestDto.getSeasonId();
        int page = gameLogAdminRequestDto.getPage();
        int size = gameLogAdminRequestDto.getSize();

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("startTime").descending());
        if (seasonId == null || seasonId == 0)
            return gameAdminService.findAllGamesByAdmin(pageable);
        else
            return gameAdminService.findGamesBySeasonId(seasonId, pageable);
    }

    @GetMapping("/users")
    public GameLogListAdminResponseDto gameFindByIntraId(@ModelAttribute GameUserLogAdminReqDto reqDto) {

        Pageable pageable = PageRequest.of(reqDto.getPage() - 1, reqDto.getSize());
        return gameAdminService.findGamesByIntraId(reqDto.getIntraId(), pageable);
    }
}
