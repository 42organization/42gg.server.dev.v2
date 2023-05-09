package com.gg.server.domain.rank.redis;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.pchange.data.PChange;
import com.gg.server.domain.pchange.data.PChangeRepository;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.global.utils.EloRating;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankRedisService {
    private final RankRedisRepository rankRedisRepository;
    private final PChangeRepository pChangeRepository;
    public void updateRankRedis(List<TeamUser> list, Long seasonId, Game game) {
        // 단식 -> 2명 기준
        String key = RedisKeyManager.getHashKey(seasonId);
        String zsetKey = RedisKeyManager.getZSetKey(seasonId);
        RankRedis myTeam = rankRedisRepository.findRankByUserId(key, list.get(0).getUser().getId());
        RankRedis enemyTeam = rankRedisRepository.findRankByUserId(key, list.get(1).getUser().getId());
        updatePPP(game, list.get(0), myTeam, enemyTeam, list.get(1).getTeam().getScore());
        updatePPP(game, list.get(1), enemyTeam, myTeam, list.get(0).getTeam().getScore());
        updateRankUser(key, zsetKey, list.get(0).getUser().getId(), myTeam);
        updateRankUser(key, zsetKey, list.get(1).getUser().getId(), enemyTeam);
    }

    public void updateRankUser(String hashKey, String zsetKey, Long userId, RankRedis userRank) {
        rankRedisRepository.updateRankData(hashKey, userId, userRank);
        rankRedisRepository.deleteFromZSet(zsetKey, userId);
        rankRedisRepository.addToZSet(zsetKey, userId, userRank.getPpp());
    }

    @Transactional
    void updatePPP(Game game, TeamUser teamuser, RankRedis myTeam, RankRedis enemyTeam, int enemyScore) {
        int win = teamuser.getTeam().getWin() ? myTeam.getWins() + 1 : myTeam.getWins();
        int losses = !teamuser.getTeam().getWin() ? myTeam.getLosses() + 1: myTeam.getLosses();
        myTeam.updateRank(EloRating.pppChange(myTeam.getPpp(), enemyTeam.getPpp(),
                        teamuser.getTeam().getWin(), Math.abs(teamuser.getTeam().getScore() - enemyScore) == 2),
                win, losses);
        pChangeRepository.save(new PChange(game, teamuser.getUser(), myTeam.getPpp()));
    }
}
