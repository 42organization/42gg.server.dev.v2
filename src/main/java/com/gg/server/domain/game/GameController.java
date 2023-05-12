package com.gg.server.domain.game;

import com.gg.server.domain.game.dto.req.*;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            throw new InvalidParameterException("status not valid", ErrorCode.VALID_FAILED);
        }
        return gameService.allGameList(gameReq.getPageNum() - 1, gameReq.getPageSize(), gameReq.getStatus(), gameReq.getNickname());
    }

    @GetMapping("/normal")
    GameListResDto normalGameList(@ModelAttribute @Valid NormalGameListReqDto gameReq) {
        return gameService.normalGameList(gameReq.getPageNum() - 1, gameReq.getPageSize(), gameReq.getNickname());
    }

    @GetMapping("/rank")
    GameListResDto rankGameList(@ModelAttribute @Valid RankGameListReqDto gameReq) {
        return gameService.rankGameList(gameReq.getPageNum() - 1, gameReq.getPageSize(), gameReq.getSeasonId(), gameReq.getNickname());
    }

    @PostMapping("/rank")
    ResponseEntity createRankResult(@Valid @RequestBody RankResultReqDto reqDto, @Parameter(hidden = true) @Login UserDto user) {
        if (!gameService.createRankResult(reqDto, user.getId())) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping("/normal")
    ResponseEntity createNormalResult(@Valid @RequestBody NormalResultReqDto reqDto) {
        if (gameService.normalExpResult(reqDto))
            return new ResponseEntity(HttpStatus.CREATED);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
