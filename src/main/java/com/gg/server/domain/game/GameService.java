package com.gg.server.domain.game;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.dto.GameResultResDto;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.team.data.TeamRepository;
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

    @Transactional(readOnly = true)
    public GameListResDto normalGameList(int count, int pageSize) {
        Pageable pageable = PageRequest.of(count, pageSize, Sort.by(Sort.Direction.DESC, "startTime"));
        Slice<Game> games = gameRepository.findAllByModeAndStatus(Mode.NORMAL, StatusType.END, pageable);
        return new GameListResDto(getGameResultList(games), games.isLast());
    }

    @Transactional(readOnly = true)
    public GameListResDto rankGameList(int count, int pageSize, Long seasonId) {
        Pageable pageable = PageRequest.of(count, pageSize, Sort.by(Sort.Direction.DESC, "startTime"));
        Slice<Game> games = gameRepository.findAllByModeAndStatusAndSeasonId(Mode.RANK, StatusType.END, seasonId, pageable);
        return new GameListResDto(getGameResultList(games), games.isLast());
    }

    private List<GameResultResDto> getGameResultList(Slice<Game> games) {
        List<GameTeamUser> teamViews = gameRepository.findTeamsByGameIsIn(games.stream().map(Game::getId).collect(Collectors.toList()));
        return teamViews.stream().map(GameResultResDto::new).collect(Collectors.toList());
    }
}
