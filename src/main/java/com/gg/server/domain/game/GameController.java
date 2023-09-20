package com.gg.server.domain.game;

import com.gg.server.domain.game.dto.ExpChangeResultResDto;
import com.gg.server.domain.game.dto.GameTeamInfo;
import com.gg.server.domain.game.dto.PPPChangeResultResDto;
import com.gg.server.domain.game.dto.req.*;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.game.exception.ScoreNotMatchedException;
import com.gg.server.domain.game.service.GameFindService;
import com.gg.server.domain.game.service.GameService;
import com.gg.server.domain.rank.redis.RankRedisService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/games")
public class GameController {
    private final GameService gameService;
    private final GameFindService gameFindService;
    private final RankRedisService rankRedisService;

    @GetMapping
    GameListResDto allGameList(@Valid GameListReqDto gameReq) {
        if (gameReq.getStatus() != null && !gameReq.getStatus().name().equals("LIVE")) {
            throw new InvalidParameterException("status not valid", ErrorCode.VALID_FAILED);
        }
        Pageable pageable = PageRequest.of(gameReq.getPage() - 1, gameReq.getSize(), Sort.by(Sort.Direction.DESC, "startTime"));
        if (gameReq.getIntraId() != null) {
            return gameFindService.allGameListUser(pageable, gameReq.getIntraId(), gameReq.getStatus() != null ? gameReq.getStatus().name() : "NULL");
        }
        return gameFindService.allGameList(pageable, gameReq.getStatus() != null ? gameReq.getStatus().name() : "NULL");
    }

    @GetMapping("/normal")
    GameListResDto normalGameList(@ModelAttribute @Valid NormalGameListReqDto gameReq) {
        Pageable pageable = PageRequest.of(gameReq.getPage() - 1, gameReq.getSize(), Sort.by(Sort.Direction.DESC, "startTime"));
        if (gameReq.getIntraId() == null) {
            return gameFindService.getNormalGameList(pageable);
        }
        return gameFindService.normalGameListByIntra(pageable, gameReq.getIntraId());
    }

    @GetMapping("/rank")
    GameListResDto rankGameList(@ModelAttribute @Valid RankGameListReqDto gameReq) {
        Pageable pageable = PageRequest.of(gameReq.getPage() - 1, gameReq.getSize(), Sort.by(Sort.Direction.DESC, "startTime"));
        if (gameReq.getIntraId() == null) {
            return gameFindService.rankGameList(pageable, gameReq.getSeasonId());
        }
        return gameFindService.rankGameListByIntra(pageable, gameReq.getSeasonId(), gameReq.getIntraId());
    }

    @GetMapping("/{gameId}")
    GameTeamInfo getGameInfo(@PathVariable Long gameId, @Parameter(hidden = true) @Login UserDto userDto) {
        return gameService.getUserGameInfo(gameId, userDto.getId());
    }

    @PostMapping("/rank")
    synchronized ResponseEntity createRankResult(@Valid @RequestBody RankResultReqDto reqDto, @Parameter(hidden = true) @Login UserDto user) {
        if (reqDto.getMyTeamScore() + reqDto.getEnemyTeamScore() > 3 || reqDto.getMyTeamScore() == reqDto.getEnemyTeamScore()) {
            throw new InvalidParameterException("점수를 잘못 입력했습니다.", ErrorCode.VALID_FAILED);
        }
        if (!gameService.createRankResult(reqDto, user.getId())) {
            throw new ScoreNotMatchedException();
        }
        rankRedisService.updateAllTier(reqDto.getGameId());
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping("/normal")
    ResponseEntity createNormalResult(@Valid @RequestBody NormalResultReqDto reqDto, @Parameter(hidden = true) @Login UserDto user) {
        if (gameService.normalExpResult(reqDto, user.getId()))
            return new ResponseEntity(HttpStatus.CREATED);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @GetMapping("/{gameId}/result/normal")
    ExpChangeResultResDto getNormalExpChange(@PathVariable Long gameId, @Parameter(hidden = true) @Login UserDto user) {
        return gameService.expChangeResult(gameId, user.getId());
    }

    @GetMapping("/{gameId}/result/rank")
    PPPChangeResultResDto getRankPPPChange(@PathVariable Long gameId, @Parameter(hidden = true) @Login UserDto user) {
        return gameService.pppChangeResult(gameId, user.getId());
    }
}
