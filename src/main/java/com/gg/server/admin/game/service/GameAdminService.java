package com.gg.server.admin.game.service;

import com.gg.server.admin.game.dto.GameLogAdminDto;
import com.gg.server.admin.game.dto.GameLogListAdminResponseDto;
import com.gg.server.admin.game.data.GameAdminRepository;
import com.gg.server.admin.game.dto.GameTeamAdminDto;
import com.gg.server.admin.season.data.SeasonAdminRepository;
import com.gg.server.admin.team.data.TeamAdminRepository;
import com.gg.server.admin.team.data.TeamUserAdminRepository;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.dto.GameResultResDto;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.exception.GameNotFoundException;
import com.gg.server.domain.pchange.data.PChange;
import com.gg.server.domain.pchange.data.PChangeRepository;

import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.exception.SeasonNotFoundException;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameAdminService {

    private final GameAdminRepository gameAdminRepository;
    private final SeasonAdminRepository seasonAdminRepository;
    private final UserAdminRepository userAdminRepository;
    private final PChangeRepository pChangeRepository;
    private final EntityManagerFactory emF;

    @Transactional(readOnly = true)
    public GameLogListAdminResponseDto findAllGamesByAdmin(Pageable pageable) {
        Page<Game> gamePage = gameAdminRepository.findAll(pageable); //모든 게임 정보 가져오기
        return new GameLogListAdminResponseDto(getGameLogList(gamePage.getContent().stream().map(Game::getId).collect(Collectors.toList())), gamePage.getTotalPages());
    }


    @Transactional(readOnly = true)
    public GameLogListAdminResponseDto findGamesBySeasonId(Long seasonId, Pageable pageable){
        Season season = seasonAdminRepository.findById(seasonId).orElseThrow(()-> new SeasonNotFoundException());
        Page<Game> games = gameAdminRepository.findBySeason(pageable, season);   //시즌 id로 게임들 찾아오기
        return new GameLogListAdminResponseDto(getGameLogList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())), games.getTotalPages());
    }

    @Transactional(readOnly = true)
    public List<GameLogAdminDto> getGameLogList(List<Long> gameIdList){
        List<GameTeamUser> teamViews = gameAdminRepository.findTeamsByGameIsIn(gameIdList);
        return teamViews.stream().map(GameLogAdminDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GameLogListAdminResponseDto findGamesByIntraId(String intraId, Pageable pageable){
        User user = userAdminRepository.findByIntraId(intraId).orElseThrow(() -> new UserNotFoundException());
        List<PChange> pChangeList = pChangeRepository.findAllByUserId(user.getId());
        List<Game> gameList = new ArrayList<>();

        for(PChange pChange : pChangeList)
            gameList.add(pChange.getGame());

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), gameList.size());
        Page<Game> games = new PageImpl<>(gameList.subList(start, end), pageable, gameList.size());
        return new GameLogListAdminResponseDto(getGameLogList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())), games.getTotalPages());
    }
}