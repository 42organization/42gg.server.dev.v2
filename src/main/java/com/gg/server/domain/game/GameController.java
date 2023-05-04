package com.gg.server.domain.game;

import com.gg.server.domain.game.dto.GameListReqDto;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.game.dto.RankGameListReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/games")
public class GameController {
    private final GameService gameService;

    @GetMapping("/")
    GameListResDto allGameList(@ModelAttribute @Valid GameListReqDto gameReq) {
        return gameService.allGameList(gameReq.getCount(), gameReq.getPageSize());
    }
    @GetMapping("/normal")
    GameListResDto normalGameList(@ModelAttribute @Valid GameListReqDto gameReq) {
        return gameService.normalGameList(gameReq.getCount(), gameReq.getPageSize());
    }

    @GetMapping("/rank")
    GameListResDto rankGameList(@ModelAttribute @Valid RankGameListReqDto gameReq) {
        return gameService.rankGameList(gameReq.getCount(), gameReq.getPageSize(), gameReq.getSeasonId());
    }
}
