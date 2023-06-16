package com.gg.server.admin.game.service;

import com.gg.server.admin.game.dto.GameLogAdminDto;
import com.gg.server.admin.game.dto.GameLogListAdminResponseDto;
import com.gg.server.admin.game.data.GameAdminRepository;
import com.gg.server.admin.game.dto.RankGamePPPModifyReqDto;
import com.gg.server.admin.game.exception.NotRecentlyGameException;
import com.gg.server.admin.pchange.data.PChangeAdminRepository;
import com.gg.server.admin.season.data.SeasonAdminRepository;
import com.gg.server.admin.team.data.TeamUserAdminRepository;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.exception.GameNotExistException;
import com.gg.server.domain.pchange.data.PChange;
import com.gg.server.domain.pchange.data.PChangeRepository;

import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.exception.RankNotFoundException;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisService;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.exception.SeasonNotFoundException;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameAdminService {

    private final GameAdminRepository gameAdminRepository;
    private final SeasonAdminRepository seasonAdminRepository;
    private final UserAdminRepository userAdminRepository;
    private final PChangeRepository pChangeRepository;
    private final PChangeAdminRepository pChangeAdminRepository;
    private final RankRedisService rankRedisService;
    private final TeamUserAdminRepository teamUserAdminRepository;
    private final RankRepository rankRepository;

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

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "rankGameListByIntra", allEntries = true),
            @CacheEvict(value = "rankGameList", allEntries = true),
            @CacheEvict(value = "allGameList", allEntries = true),
            @CacheEvict(value = "allGameListByUser", allEntries = true)
    })
    public void rankResultEdit(RankGamePPPModifyReqDto reqDto, Long gameId) {
        // 게임이 두명 다 가장 마지막 게임인지 확인 (그 game에 해당하는 팀이 맞는지 확인)
        List<TeamUser> teamUsers = teamUserAdminRepository.findUsersByTeamIdIn(List.of(reqDto.getTeam1Id(), reqDto.getTeam2Id()));
        Game game = gameAdminRepository.findById(gameId)
                .orElseThrow(GameNotExistException::new);
        Season season = seasonAdminRepository.findById(game.getSeason().getId())
                .orElseThrow(SeasonNotFoundException::new);
        if (!isRecentlyGame(teamUsers, gameId)) {
            throw new NotRecentlyGameException();
        }
        // pchange 가져와서 rank ppp 이전 값을 가지고 새 점수를 바탕으로 다시 계산
        for (TeamUser teamUser :
                teamUsers) {
            List<PChange> pChanges = pChangeAdminRepository.findByTeamUser(teamUser.getUser().getId());
            rollbackGameResult(reqDto, season, teamUser, pChanges);
            pChangeAdminRepository.delete(pChanges.get(0));
        }
        rankRedisService.updateRankRedis(teamUsers.get(0), teamUsers.get(1), game);
    }

    private void rollbackGameResult(RankGamePPPModifyReqDto reqDto, Season season, TeamUser teamUser, List<PChange> pChanges) {
        // pchange ppp도 update
        // rankredis 에 ppp 다시 반영
        // rank zset 도 update
        // 이전 ppp, exp 되돌리기
        // rank data 에 있는 ppp 되돌리기
        if (teamUser.getTeam().getId().equals(reqDto.getTeam1Id())) {
            teamUser.getTeam().updateScore(reqDto.getTeam1Score(), reqDto.getTeam1Score() > reqDto.getTeam2Score());
        } else if (teamUser.getTeam().getId().equals(reqDto.getTeam2Id())) {
            teamUser.getTeam().updateScore(reqDto.getTeam2Score(), reqDto.getTeam2Score() > reqDto.getTeam1Score());
        }
        if (pChanges.size() == 1) {
            rankRedisService.rollbackRank(teamUser, season.getStartPpp(), season.getId());
            teamUser.getUser().updateExp(0);
        } else {
            rankRedisService.rollbackRank(teamUser, pChanges.get(1).getPppResult(), season.getId());
            teamUser.getUser().updateExp(pChanges.get(1).getExp());
        }
    }

    private Boolean isRecentlyGame(List<TeamUser> teamUsers, Long gameId) {
        for (TeamUser teamUser : teamUsers) {
            List<PChange> pChanges = pChangeAdminRepository.findByTeamUser(teamUser.getUser().getId());
            if (!pChanges.get(0).getGame().getId().equals(gameId))
                return false;
        }
        return true;
    }
}