package com.gg.server.domain.rank.redis;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.service.GameService;
import com.gg.server.domain.pchange.service.PChangeService;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.exception.RankNotFoundException;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;
import com.gg.server.global.utils.EloRating;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankRedisService {
    private final RankRedisRepository rankRedisRepository;
    private final PChangeService pChangeService;
    private final RankRepository rankRepository;

    public Integer getUserPpp(Long userId, Long seasonId) {
        String hashKey = RedisKeyManager.getHashKey(seasonId);
        return rankRedisRepository.findRankByUserId(hashKey, userId).getPpp();
    }
    public void updateRankRedis(List<TeamUser> list, Game game) {
        // 단식 -> 2명 기준
        String key = RedisKeyManager.getHashKey(game.getSeason().getId());
        String zsetKey = RedisKeyManager.getZSetKey(game.getSeason().getId());
        RankRedis myTeam = rankRedisRepository.findRankByUserId(key, list.get(0).getUser().getId());
        RankRedis enemyTeam = rankRedisRepository.findRankByUserId(key, list.get(1).getUser().getId());
        Integer myPPP = myTeam.getPpp();
        Integer enemyPPP = enemyTeam.getPpp();
        System.out.println("myTeam: " + myTeam);
        System.out.println("enemyTeam: " + enemyTeam);
        updatePPP(list.get(0), myTeam, list.get(1).getTeam().getScore(), myPPP, enemyPPP, game.getSeason().getId());
        updatePPP(list.get(1), enemyTeam, list.get(0).getTeam().getScore(), enemyPPP, myPPP, game.getSeason().getId());
        updateRankUser(key, zsetKey, list.get(0).getUser().getId(), myTeam);
        updateRankUser(key, zsetKey, list.get(1).getUser().getId(), enemyTeam);
        pChangeService.addPChange(game, list.get(0).getUser(), myTeam.getPpp());
        pChangeService.addPChange(game, list.get(1).getUser(), enemyTeam.getPpp());
        System.out.println("myTeam: " + myTeam);
        System.out.println("enemyTeam: " + enemyTeam);
    }

    private void updateRankUser(String hashKey, String zsetKey, Long userId, RankRedis userRank) {
        rankRedisRepository.updateRankData(hashKey, userId, userRank);
        rankRedisRepository.deleteFromZSet(zsetKey, userId);
        rankRedisRepository.addToZSet(zsetKey, userId, userRank.getPpp());
    }

    @Transactional
    public void updatePPP(TeamUser teamuser, RankRedis myTeam, int enemyScore, Integer myPPP, Integer enemyPPP, Long seasonId) {
        int win = teamuser.getTeam().getWin() ? myTeam.getWins() + 1 : myTeam.getWins();
        int losses = !teamuser.getTeam().getWin() ? myTeam.getLosses() + 1: myTeam.getLosses();
        // rank table 수정
        Rank rank = rankRepository.findByUserIdAndSeasonId(myTeam.getUserId(), seasonId)
                .orElseThrow(() -> new NotExistException("rank 정보가 없습니다.", ErrorCode.NOT_FOUND));
        Integer changedPpp = EloRating.pppChange(myPPP, enemyPPP,
                teamuser.getTeam().getWin(), Math.abs(teamuser.getTeam().getScore() - enemyScore) == 2);
        rank.addPpp(changedPpp);
        System.out.println("userId: " + myTeam.getUserId() +", " + rank.getPpp());
        myTeam.updateRank(changedPpp,
                win, losses);
    }

    public void rollbackRank(TeamUser teamUser, int ppp, Long seasonId) {
        String hashkey = RedisKeyManager.getHashKey(seasonId);
        RankRedis myTeam = rankRedisRepository.findRankByUserId(hashkey, teamUser.getUser().getId());
        int win = teamUser.getTeam().getWin() ? myTeam.getWins() - 1 : myTeam.getWins();
        int losses = !teamUser.getTeam().getWin() ? myTeam.getLosses() - 1: myTeam.getLosses();
        Rank rank = rankRepository.findByUserIdAndSeasonId(myTeam.getUserId(), seasonId)
                .orElseThrow(RankNotFoundException::new);
        rank.updatePpp(ppp);
        myTeam.updateRank(ppp, win, losses);
        updateRankUser(hashkey, RedisKeyManager.getZSetKey(seasonId), teamUser.getUser().getId(), myTeam);
    }
}
