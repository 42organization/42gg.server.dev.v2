package com.gg.server.domain.game;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.dto.GameResultResDto;
import com.gg.server.domain.game.dto.req.RankResultReqDto;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.team.data.TeamRepository;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;
import com.gg.server.global.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;

    @Transactional(readOnly = true)
    public GameListResDto normalGameList(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "startTime"));
        Slice<Game> games = gameRepository.findAllByModeAndStatus(Mode.NORMAL, StatusType.END, pageable);
        return new GameListResDto(getGameResultList(games), games.isLast());
    }

    @Transactional(readOnly = true)
    public GameListResDto rankGameList(int pageNum, int pageSize, Long seasonId) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "startTime"));
        Slice<Game> games = gameRepository.findAllByModeAndStatusAndSeasonId(Mode.RANK, StatusType.END, seasonId, pageable);
        return new GameListResDto(getGameResultList(games), games.isLast());
    }

    @Transactional(readOnly = true)
    public GameListResDto allGameList(int pageNum, int pageSize, StatusType status) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "startTime"));
        Slice<Game> games;
        if (status != null) {
            games = gameRepository.findAllByAndStatusIn(Arrays.asList(StatusType.END, StatusType.LIVE), pageable);
        } else {
            games = gameRepository.findAllByAndStatus(StatusType.END, pageable);
        }
        return new GameListResDto(getGameResultList(games), games.isLast());
    }
    private List<GameResultResDto> getGameResultList(Slice<Game> games) {
        List<GameTeamUser> teamViews = gameRepository.findTeamsByGameIsIn(games.stream().map(Game::getId).collect(Collectors.toList()));
        return teamViews.stream().map(GameResultResDto::new).collect(Collectors.toList());
    }

    @Transactional
    public Boolean createRankResult(RankResultReqDto scoreDto) {
        // rank 점수 입력받기
        if (scoreDto.getMyTeamScore() + scoreDto.getEnemyTeamScore() > 3) {
            throw new InvalidParameterException("점수를 잘못 입력했습니다.", ErrorCode.VALID_FAILED);
        }
        // 현재 게임 id
        Game game = gameRepository.findById(scoreDto.getGameId())
                .orElseThrow(() -> new NotExistException("존재하지 않는 게임 Id 입니다.", ErrorCode.BAD_ARGU));
        if (game.getStatus() != StatusType.WAIT) {
            return false;
        }
        // user 가 게임한 팀, 상대 팀 id
        if (!updateScore(game.getId(), scoreDto))
            return false;
        game.updateStatus();
        return true;
    }

    private Boolean updateScore(Long gameId, RankResultReqDto scoreDto) {
        List<TeamUser> teams = teamUserRepository.findAllByGameIdAndTeamId(gameId);
        for (TeamUser team : teams) {
            if (team.getTeam().getId().equals(scoreDto.getMyTeamId())) {
                if (team.getTeam().getScore() == -1)
                    team.getTeam().inputScore(scoreDto.getMyTeamScore());
                else return false;
            } else if (team.getTeam().getId().equals(scoreDto.getEnemyTeamId())) {
                if (team.getTeam().getScore() == -1)
                    team.getTeam().inputScore(scoreDto.getEnemyTeamScore());
                else return false;
            }
        }
        return true;
    }
}
