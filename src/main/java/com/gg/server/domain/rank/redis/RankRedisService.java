package com.gg.server.domain.rank.redis;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.service.GameService;
import com.gg.server.domain.pchange.data.PChange;
import com.gg.server.domain.pchange.data.PChangeRepository;
import com.gg.server.domain.pchange.service.PChangeService;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
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
    private final GameService gameService;
    private final RankRepository rankRepository;
    public void updateRankRedis(List<TeamUser> list, Long seasonId, Game game) {
        // 단식 -> 2명 기준
        String key = RedisKeyManager.getHashKey(seasonId);
        String zsetKey = RedisKeyManager.getZSetKey(seasonId);
        RankRedis myTeam = rankRedisRepository.findRankByUserId(key, list.get(0).getUser().getId());
        RankRedis enemyTeam = rankRedisRepository.findRankByUserId(key, list.get(1).getUser().getId());
        updatePPP(list.get(0), myTeam, enemyTeam, list.get(1).getTeam().getScore(), seasonId);
        updatePPP(list.get(1), enemyTeam, myTeam, list.get(0).getTeam().getScore(), seasonId);
        updateRankUser(key, zsetKey, list.get(0).getUser().getId(), myTeam);
        updateRankUser(key, zsetKey, list.get(1).getUser().getId(), enemyTeam);
        pChangeService.addPChange(game, list.get(0).getUser(), myTeam.getPpp());
        pChangeService.addPChange(game, list.get(1).getUser(), enemyTeam.getPpp());
    }

    public void updateRankUser(String hashKey, String zsetKey, Long userId, RankRedis userRank) {
        rankRedisRepository.updateRankData(hashKey, userId, userRank);
        rankRedisRepository.deleteFromZSet(zsetKey, userId);
        rankRedisRepository.addToZSet(zsetKey, userId, userRank.getPpp());
    }

    @Transactional
    void updatePPP(TeamUser teamuser, RankRedis myTeam, RankRedis enemyTeam, int enemyScore, Long seasonId) {
        int win = teamuser.getTeam().getWin() ? myTeam.getWins() + 1 : myTeam.getWins();
        int losses = !teamuser.getTeam().getWin() ? myTeam.getLosses() + 1: myTeam.getLosses();
        // rank table 수정
        Rank rank = rankRepository.findByUserIdAndSeasonId(myTeam.getUserId(), seasonId)
                .orElseThrow(() -> new NotExistException("rank 정보가 없습니다.", ErrorCode.NOT_FOUND));
        rank.updatePpp(EloRating.pppChange(myTeam.getPpp(), enemyTeam.getPpp(),
                teamuser.getTeam().getWin(), Math.abs(teamuser.getTeam().getScore() - enemyScore) == 2));
        myTeam.updateRank(rank.getPpp(),
                win, losses);
    }
}
