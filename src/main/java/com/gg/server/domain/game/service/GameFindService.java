package com.gg.server.domain.game.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.game.dto.GameResultResDto;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.exception.GameNotExistException;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "normalGameListByIntra", cacheManager = "gameCacheManager")
    public GameListResDto normalGameListByIntra(Pageable pageable, String intra) {
        Slice<Long> games = gameRepository.findGamesByUserAndMode(intra, Mode.NORMAL.name(), StatusType.END.name(), pageable);
        return new GameListResDto(getNormalGameResultList(games.getContent()), games.isLast());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "normalGameList", cacheManager = "gameCacheManager")
    public GameListResDto getNormalGameList(Pageable pageable) {
        Slice<Game> games = gameRepository.findAllByModeAndStatus(Mode.NORMAL, StatusType.END, pageable);
        return new GameListResDto(getNormalGameResultList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())), games.isLast());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "rankGameListByIntra", cacheManager = "gameCacheManager")
    public GameListResDto rankGameListByIntra(Pageable pageable, Long seasonId, String intra) {
        Slice<Long> games = gameRepository.findGamesByUserAndModeAndSeason(intra, Mode.RANK.name(), seasonId, StatusType.END.name(), pageable);
        return new GameListResDto(getGameResultList(games.getContent()), games.isLast());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "rankGameList", cacheManager = "gameCacheManager")
    public GameListResDto rankGameList(Pageable pageable, Long seasonId) {
        Slice<Game> games = gameRepository.findAllByModeAndStatusAndSeasonId(Mode.RANK, StatusType.END, seasonId, pageable);
        return new GameListResDto(getGameResultList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())), games.isLast());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "allGameList", cacheManager = "gameCacheManager")
    public GameListResDto allGameList(Pageable pageable, StatusType status) {
        Slice<Game> games;
        if (status == StatusType.LIVE) {
            games = gameRepository.findAllByAndStatusIn(Arrays.asList(StatusType.END, StatusType.LIVE, StatusType.WAIT), pageable);
        } else {
            games = gameRepository.findAllByAndStatus(StatusType.END, pageable);
        }
        return new GameListResDto(getGameResultList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())), games.isLast());
    }

    private List<GameResultResDto> getGameResultList(List<Long> games) {
        List<GameTeamUser> teamViews = gameRepository.findTeamsByGameIsIn(games);
        return teamViews.stream().map(GameResultResDto::new).collect(Collectors.toList());
    }
    private List<GameResultResDto> getNormalGameResultList(List<Long> games) {
        List<GameTeamUser> teamViews = gameRepository.findTeamsByGameIsInAndNormalMode(games);
        return teamViews.stream().map(GameResultResDto::new).collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    @Cacheable(value = "allGameListByUser", cacheManager = "gameCacheManager")
    public GameListResDto allGameListUser(Pageable pageable, String intra, StatusType status) {
        List<String> statusTypes = Arrays.asList(StatusType.END.name());
        if (status == StatusType.LIVE) {
            statusTypes.add(StatusType.LIVE.name());
            statusTypes.add(StatusType.WAIT.name());
        }
        Slice<Long> games = gameRepository.findGamesByUser(intra, statusTypes, pageable);
        return new GameListResDto(getGameResultList(games.getContent()), games.isLast());
    }
    public Game findByGameId(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(GameNotExistException::new);
    }
}
