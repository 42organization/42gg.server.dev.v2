package com.gg.server.domain.game;

import com.gg.server.domain.game.dto.req.GameListReqDto;
import com.gg.server.domain.game.dto.req.NormalGameListReqDto;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.game.dto.req.RankGameListReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/games")
public class GameController {
    private final GameService gameService;

    @GetMapping
    GameListResDto allGameList(@ModelAttribute @Valid GameListReqDto gameReq) {
        if (gameReq.getStatus() != null && !gameReq.getStatus().name().equals("LIVE")) {
            // 예외처리
        }
        return gameService.allGameList(gameReq.getPageNum(), gameReq.getPageSize(), gameReq.getStatus());
    }
    @GetMapping("/normal")
    GameListResDto normalGameList(@ModelAttribute @Valid NormalGameListReqDto gameReq) {
        return gameService.normalGameList(gameReq.getPageNum(), gameReq.getPageSize());
    }

    @GetMapping("/rank")
    GameListResDto rankGameList(@ModelAttribute @Valid RankGameListReqDto gameReq) {
        return gameService.rankGameList(gameReq.getPageNum(), gameReq.getPageSize(), gameReq.getSeasonId());
    }
}
