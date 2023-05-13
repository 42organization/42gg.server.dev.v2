package com.gg.server.admin.game.controller;

import com.gg.server.admin.game.dto.GameLogListAdminResponseDto;
import com.gg.server.admin.game.service.GameAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Size;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/games")
public class GameAdminController {

    private final GameAdminService gameAdminService;
    @GetMapping
    public GameLogListAdminResponseDto gameFindBySeasonId(@RequestParam(value = "season", required = false)
                                                          int seasonId,
                                                          @RequestParam(value = "page")
                                                          @Size(min=1)
                                                          int page,
                                                          @RequestParam(defaultValue = "20")
                                                          @Size(min=1)
                                                          int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (seasonId == 0)
            return gameAdminService.findAllGamesByAdmin(pageable);
        else
            return new GameLogListAdminResponseDto();
//            return gameAdminService.findGamesBySeasonId(seasonId, pageable);
    }
}
