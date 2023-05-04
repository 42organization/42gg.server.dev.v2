package com.gg.server.domain.game;

import com.gg.server.domain.game.dto.GameListResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/games")
public class GameController {
    private final GameService gameService;
    @GetMapping("/normal")
    GameListResDto normalGameList(@RequestParam int count, @RequestParam int pageSize) {
        return gameService.normalGameList(count, pageSize);
    }

    @GetMapping("/rank")
    GameListResDto rankGameList(@RequestParam int count, @RequestParam int pageSize, @RequestParam Long seasonId) {
        return gameService.rankGameList(count, pageSize, seasonId);
    }
}
