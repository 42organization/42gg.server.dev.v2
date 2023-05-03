package com.gg.server.domain.game;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.dto.NormalGameResDto;
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
        List<StatusType> statusList = new ArrayList<>();
        statusList.add(StatusType.END);
        Slice<Game> games = gameRepository.findAllByModeAndStatusIsInOrderByStartTimeDesc(Mode.NORMAL, statusList,pageable);
        List<NormalGameResDto> gamelist = new ArrayList<>();
        List<GameTeamUser> teamViews = gameRepository.findTeamsByGameIsIn(games.stream().map(Game::getId).collect(Collectors.toList()));
        int i = 0;
        for (Game game: games) {
            NormalGameResDto dto = new NormalGameResDto(game);
            dto.setTeamList(teamViews.get(i++));
            gamelist.add(dto);
            if (i >= pageSize) {
                break;
            }
        }
        return new GameListResDto(gamelist, games.isLast());
    }
}
