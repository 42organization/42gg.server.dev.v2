package com.gg.server.admin.game.service;

import com.gg.server.admin.game.dto.GameLogAdminDto;
import com.gg.server.admin.game.dto.GameLogListAdminResponseDto;
import com.gg.server.admin.game.data.GameAdminRepository;
import com.gg.server.admin.game.dto.RankGamePPPModifyReqDto;
import com.gg.server.admin.game.exception.NotRecentlyGameException;
import com.gg.server.admin.pchange.data.PChangeAdminRepository;
import com.gg.server.admin.pchange.exception.PChangeNotExistException;
import com.gg.server.admin.season.data.SeasonAdminRepository;
import com.gg.server.admin.team.data.TeamUserAdminRepository;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.exception.GameNotExistException;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.data.RedisMatchUserRepository;
import com.gg.server.domain.pchange.data.PChange;
import com.gg.server.domain.pchange.data.PChangeRepository;

import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisService;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.dto.CurSeason;
import com.gg.server.domain.season.exception.SeasonNotFoundException;
import com.gg.server.domain.season.service.SeasonService;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.tier.service.TierService;
import com.gg.server.domain.user.data.User;
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

import javax.persistence.EntityManager;

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
    private final RedisMatchUserRepository redisMatchUserRepository;
    private final TierService tierService;
    private final SeasonService seasonService;
    private final EntityManager entityManager;

    /**
     * <p>토너먼트 게임을 제외한 일반, 랭크 게임들을 찾아서 반환해준다.</p>
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public GameLogListAdminResponseDto findAllGamesByAdmin(Pageable pageable) {
        Page<Game> gamePage = gameAdminRepository.findAllByModeIn(pageable, List.of(Mode.NORMAL, Mode.RANK));
        return new GameLogListAdminResponseDto(getGameLogList(gamePage.getContent().stream().map(Game::getId).collect(Collectors.toList())), gamePage.getTotalPages());
    }

    /**
     * <p>토너먼트 게임을 제외한 해당 시즌의 일반, 랭크 게임들을 찾아서 반환해준다.</p>
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public GameLogListAdminResponseDto findGamesBySeasonId(Long seasonId, Pageable pageable){
        Season season = seasonAdminRepository.findById(seasonId).orElseThrow(SeasonNotFoundException::new);
        Page<Game> games = gameAdminRepository.findBySeasonAndModeIn(pageable, season, List.of(Mode.NORMAL, Mode.RANK));
        return new GameLogListAdminResponseDto(getGameLogList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())), games.getTotalPages());
    }

    @Transactional(readOnly = true)
    public List<GameLogAdminDto> getGameLogList(List<Long> gameIdList){
        List<GameTeamUser> teamViews = gameAdminRepository.findTeamsByGameIsIn(gameIdList);
        return teamViews.stream().map(GameLogAdminDto::new).collect(Collectors.toList());
    }

    /**
     * 특정 유저의 게임 목록 조회 (토너먼트 게임 제외)
     * @param intraId 조회할 유저의 intraId
     * @param pageable page size
     * @return GameLogListAdminResponseDto
     * @throws UserNotFoundException intraId에 해당하는 유저가 없을 경우
     */
    @Transactional(readOnly = true)
    public GameLogListAdminResponseDto findGamesByIntraId(String intraId, Pageable pageable){
        User user = userAdminRepository.findByIntraId(intraId).orElseThrow(UserNotFoundException::new);
        List<PChange> pChangeList = pChangeRepository.findAllByUserIdGameModeIn(user.getId(), List.of(Mode.NORMAL, Mode.RANK));
        List<Game> gameList = new ArrayList<>();

        for(PChange pChange : pChangeList)
            gameList.add(pChange.getGame());

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), gameList.size());
        Page<Game> games = new PageImpl<>(gameList.subList(start, end), pageable, gameList.size());
        return new GameLogListAdminResponseDto(getGameLogList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())), games.getTotalPages());
    }

    /**
     * 랭킹 점수 수정
     * @param reqDto team1Id team1Score team2Id team2Score
     * @param gameId 수정할 게임 id
     * @throws GameNotExistException gameId에 해당하는 게임이 없을 경우
     * @throws SeasonNotFoundException 게임에 해당하는 시즌이 없을 경우
     * @throws NotRecentlyGameException 게임이 두명 다 가장 마지막 게임이 아닐 경우
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "rankGameListByIntra", allEntries = true),
            @CacheEvict(value = "rankGameList", allEntries = true),
            @CacheEvict(value = "allGameList", allEntries = true),
            @CacheEvict(value = "allGameListByUser", allEntries = true),
            @CacheEvict(value = "ranking", allEntries = true)
    })
    public void rankResultEdit(RankGamePPPModifyReqDto reqDto, Long gameId) {
        // 게임이 두명 다 가장 마지막 게임인지 확인 (그 game에 해당하는 팀이 맞는지 확인)
        List<TeamUser> teamUsers = teamUserAdminRepository.findUsersByTeamIdIn(List.of(reqDto.getTeam1Id(), reqDto.getTeam2Id()));
        Game game = gameAdminRepository.findGameWithSeasonByGameId(gameId)
                .orElseThrow(GameNotExistException::new);
        CurSeason curSeason = seasonService.getCurSeason();
        if (!isRecentlyGame(teamUsers, gameId) || EnrollSlots(teamUsers) || !game.getSeason().getId().equals(curSeason.getId())) {
            throw new NotRecentlyGameException();
        }
        // pchange 가져와서 rank ppp 이전 값을 가지고 새 점수를 바탕으로 다시 계산
        // user 1
        List<PChange> pChanges = pChangeAdminRepository.findByTeamUser(teamUsers.get(0).getUser().getId());
        if (!pChanges.get(0).getGame().getId().equals(gameId)) {
            throw new PChangeNotExistException();
        }
        RankRedis rankRedis1 = rollbackGameResult(game.getSeason(), teamUsers.get(0), pChanges);
        pChangeAdminRepository.deleteById(pChanges.get(0).getId());
        // user 2
        pChanges = pChangeAdminRepository.findByTeamUser(teamUsers.get(1).getUser().getId());
        if (!pChanges.get(0).getGame().getId().equals(gameId)) {
            throw new PChangeNotExistException();
        }
        RankRedis rankRedis2 = rollbackGameResult(game.getSeason(), teamUsers.get(1), pChanges);
        pChangeAdminRepository.deleteById(pChanges.get(0).getId());
        entityManager.flush();
        for (int i = 0; i < teamUsers.size(); i++) {
            updateScore(reqDto, teamUsers.get(i));
        }
        teamUserAdminRepository.flush();
        rankRedisService.updateAdminRankData(teamUsers.get(0), teamUsers.get(1), game, rankRedis1, rankRedis2);
        tierService.updateAllTier(game.getSeason());
    }

    private RankRedis rollbackGameResult(Season season, TeamUser teamUser, List<PChange> pChanges) {
        // pchange ppp도 update
        // rankredis 에 ppp 다시 반영
        // rank zset 도 update
        // 이전 ppp, exp 되돌리기
        // rank data 에 있는 ppp 되돌리기
        RankRedis rankRedis;
        if (pChanges.size() == 1) {
            rankRedis = rankRedisService.rollbackRank(teamUser, season.getStartPpp(), season.getId());
            teamUser.getUser().updateExp(0);
        } else {
            rankRedis = rankRedisService.rollbackRank(teamUser, pChanges.get(1).getPppResult(), season.getId());
            teamUser.getUser().updateExp(pChanges.get(1).getExp());
        }
        return rankRedis;
    }

    private void updateScore(RankGamePPPModifyReqDto reqDto, TeamUser teamUser) {
        if (teamUser.getTeam().getId().equals(reqDto.getTeam1Id())) {
            teamUser.getTeam().updateScore(reqDto.getTeam1Score(), reqDto.getTeam1Score() > reqDto.getTeam2Score());
        } else if (teamUser.getTeam().getId().equals(reqDto.getTeam2Id())) {
            teamUser.getTeam().updateScore(reqDto.getTeam2Score(), reqDto.getTeam2Score() > reqDto.getTeam1Score());
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

    private Boolean EnrollSlots(List<TeamUser> teamUsers) {
        for (TeamUser teamUser : teamUsers) {
            Long userId = teamUser.getUser().getId();
            if (redisMatchUserRepository.countMatchTime(userId) > 0) {
                return true;
            }
            if (gameAdminRepository.findByStatusTypeAndUserId(StatusType.BEFORE, userId).isPresent()) {
                return true;
            }
        }
        return false;
    }
}