package com.gg.server.domain.game;

import com.gg.server.domain.game.dto.req.GameListReqDto;
import com.gg.server.domain.game.dto.req.NormalGameListReqDto;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.game.dto.req.RankGameListReqDto;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.springframework.validation.BindException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/games")
public class GameController {
    private final GameService gameService;

    @GetMapping
    GameListResDto allGameList(@ModelAttribute @Valid GameListReqDto gameReq) throws BindException {
        if (gameReq.getStatus() != null && !gameReq.getStatus().name().equals("LIVE")) {
            throw new InvalidParameterException("status not valid", ErrorCode.VALID_FAILED);
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
