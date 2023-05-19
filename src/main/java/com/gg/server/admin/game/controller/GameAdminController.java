package com.gg.server.admin.game.controller;

import com.gg.server.admin.game.dto.GameLogAdminRequestDto;
import com.gg.server.admin.game.dto.GameLogListAdminResponseDto;
import com.gg.server.admin.game.service.GameAdminService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/games")
@Validated
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
    public GameLogListAdminResponseDto gameFindByIntraId(@RequestParam @NotNull String intraId,
                                                         @RequestParam(defaultValue = "1") @Min(1) int page,
                                                         @RequestParam(defaultValue = "5") @Min(1) int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        return gameAdminService.findGamesByIntraId(intraId, pageable);
    }
}
