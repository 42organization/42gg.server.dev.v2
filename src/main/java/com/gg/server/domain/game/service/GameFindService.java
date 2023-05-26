package com.gg.server.domain.game.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.game.dto.GameResultResDto;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.rank.redis.RankRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameFindService {
    private final GameRepository gameRepository;
    @Transactional(readOnly = true)
    public GameListResDto normalGameListByIntra(Pageable pageable, String intra) {
        Slice<Long> games = gameRepository.findGamesByUserAndMode(intra, Mode.NORMAL.name(), StatusType.END.name(), pageable);
        return new GameListResDto(getGameResultList(games.getContent()), games.isLast());
    }

    @Transactional(readOnly = true)
    public GameListResDto getNormalGameList(Pageable pageable) {
        Slice<Game> games = gameRepository.findAllByModeAndStatus(Mode.NORMAL, StatusType.END, pageable);
        return new GameListResDto(getGameResultList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())), games.isLast());
    }

    @Transactional(readOnly = true)
    public GameListResDto rankGameListByIntra(Pageable pageable, Long seasonId, String intra) {
        Slice<Long> games = gameRepository.findGamesByUserAndModeAndSeason(intra, Mode.RANK.name(), seasonId, StatusType.END.name(), pageable);
        return new GameListResDto(getGameResultList(games.getContent()), games.isLast());
    }

    @Transactional(readOnly = true)
    public GameListResDto rankGameList(Pageable pageable, Long seasonId) {
        Slice<Game> games = gameRepository.findAllByModeAndStatusAndSeasonId(Mode.RANK, StatusType.END, seasonId, pageable);
        return new GameListResDto(getGameResultList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())), games.isLast());
    }

    @Transactional(readOnly = true)
    public GameListResDto allGameList(Pageable pageable, StatusType status) {
        Slice<Game> games;
        if (status != null) {
            games = gameRepository.findAllByAndStatusIn(Arrays.asList(StatusType.END, StatusType.LIVE), pageable);
        } else {
            games = gameRepository.findAllByAndStatus(StatusType.END, pageable);
        }
        return new GameListResDto(getGameResultList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())), games.isLast());
    }

    private List<GameResultResDto> getGameResultList(List<Long> games) {
        List<GameTeamUser> teamViews = gameRepository.findTeamsByGameIsIn(games);
        return teamViews.stream().map(GameResultResDto::new).collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public GameListResDto allGameListUser(Pageable pageable, String intra, StatusType status) {
        List<String> statusTypes = Arrays.asList(StatusType.END.name());
        if (status == StatusType.LIVE)
            statusTypes.add(StatusType.LIVE.name());
        Slice<Long> games = gameRepository.findGamesByUser(intra, statusTypes, pageable);
        return new GameListResDto(getGameResultList(games.getContent()), games.isLast());
    }
}
