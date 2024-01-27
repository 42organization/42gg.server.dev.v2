package com.gg.server.domain.rank.redis;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.pchange.service.PChangeService;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.exception.RankNotFoundException;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.service.SeasonFindService;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.tier.data.Tier;
import com.gg.server.domain.tier.data.TierRepository;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;
import com.gg.server.global.utils.EloRating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankRedisService {
	private final RankRedisRepository rankRedisRepository;
	private final TierRepository tierRepository;
	private final PChangeService pChangeService;
	private final RankRepository rankRepository;
	private final SeasonFindService seasonFindService;
	private final EntityManager entityManager;

	public Integer getUserPpp(Long userId, Long seasonId) {
		String hashKey = RedisKeyManager.getHashKey(seasonId);
		return rankRedisRepository.findRankByUserId(hashKey, userId).getPpp();
	}

	public void updateAdminRankData(TeamUser myTeamUser, TeamUser enemyTeamUser, Game game, RankRedis myTeam,
		RankRedis enemyTeam) {
		// 단식 -> 2명 기준
		String key = RedisKeyManager.getHashKey(game.getSeason().getId());
		String zsetKey = RedisKeyManager.getZSetKey(game.getSeason().getId());
		Integer myPPP = myTeam.getPpp();
		Integer enemyPPP = enemyTeam.getPpp();
		updatePPP(myTeamUser, myTeam, enemyTeamUser.getTeam().getScore(), myPPP, enemyPPP, game.getSeason().getId());
		updatePPP(enemyTeamUser, enemyTeam, myTeamUser.getTeam().getScore(), enemyPPP, myPPP, game.getSeason().getId());
		updateRankUser(key, zsetKey, myTeamUser.getUser().getId(), myTeam);
		updateRankUser(key, zsetKey, enemyTeamUser.getUser().getId(), enemyTeam);
		pChangeService.addPChange(game, myTeamUser.getUser(), myTeam.getPpp(), false);
		pChangeService.addPChange(game, enemyTeamUser.getUser(), enemyTeam.getPpp(), false);
	}

	public void updateRankRedis(TeamUser myTeamUser, TeamUser enemyTeamUser, Game game) {
		// 단식 -> 2명 기준
		String key = RedisKeyManager.getHashKey(game.getSeason().getId());
		String zsetKey = RedisKeyManager.getZSetKey(game.getSeason().getId());
		RankRedis myTeam = rankRedisRepository.findRankByUserId(key, myTeamUser.getUser().getId());
		RankRedis enemyTeam = rankRedisRepository.findRankByUserId(key, enemyTeamUser.getUser().getId());
		Integer myPPP = myTeam.getPpp();
		Integer enemyPPP = enemyTeam.getPpp();
		updatePPP(myTeamUser, myTeam, enemyTeamUser.getTeam().getScore(), myPPP, enemyPPP, game.getSeason().getId());
		updatePPP(enemyTeamUser, enemyTeam, myTeamUser.getTeam().getScore(), enemyPPP, myPPP, game.getSeason().getId());
		updateRankUser(key, zsetKey, myTeamUser.getUser().getId(), myTeam);
		updateRankUser(key, zsetKey, enemyTeamUser.getUser().getId(), enemyTeam);
		pChangeService.addPChange(game, myTeamUser.getUser(), myTeam.getPpp(), true);
		pChangeService.addPChange(game, enemyTeamUser.getUser(), enemyTeam.getPpp(), false);
	}

	private void updateRankUser(String hashKey, String zsetKey, Long userId, RankRedis userRank) {
		rankRedisRepository.updateRankData(hashKey, userId, userRank);
		rankRedisRepository.deleteFromZSet(zsetKey, userId);
		rankRedisRepository.addToZSet(zsetKey, userId, userRank.getPpp());
	}

	@Transactional
	public void updatePPP(TeamUser teamuser, RankRedis myTeam, int enemyScore, Integer myPPP, Integer enemyPPP,
		Long seasonId) {
		// rank table 수정
		Rank rank = rankRepository.findByUserIdAndSeasonId(myTeam.getUserId(), seasonId)
			.orElseThrow(() -> new NotExistException("rank 정보가 없습니다.", ErrorCode.NOT_FOUND));
		int win = teamuser.getTeam().getWin() ? rank.getWins() + 1 : rank.getWins();
		int losses = !teamuser.getTeam().getWin() ? rank.getLosses() + 1 : rank.getLosses();
		Integer changedPpp = EloRating.pppChange(myPPP, enemyPPP,
			teamuser.getTeam().getWin(),
			Math.abs(teamuser.getTeam().getScore() - enemyScore) == 2);
		log.info("update before: intraId: " + teamuser.getUser().getIntraId() + ", win: db(" + rank.getWins()
			+ "), redis(" + myTeam.getWins() + "), losses: db(" + rank.getLosses()
			+ "), redis(" + myTeam.getLosses() + ")");
		rank.modifyUserRank(rank.getPpp() + changedPpp, win, losses);
		myTeam.updateRank(changedPpp,
			win, losses);
		log.info("update after: intraId: " + teamuser.getUser().getIntraId() + ", win: db(" + rank.getWins()
			+ "), redis(" + myTeam.getWins() + "), losses: db(" + rank.getLosses()
			+ "), redis(" + myTeam.getLosses() + ")");
	}

	@Transactional
	public void updateAllTier(Long gameId) {
		// 전체 레디스 랭크 티어 새로고침하는 로직
		Season targetSeason = seasonFindService.findSeasonByGameId(gameId);
		String key = RedisKeyManager.getHashKey(targetSeason.getId());
		String zSetKey = RedisKeyManager.getZSetKey(targetSeason.getId());
		List<RankRedis> rankRedisList = rankRedisRepository.findAllRanksOrderByPppDesc(key);
		Long totalRankPlayers = rankRepository.countRealRankPlayers(targetSeason.getId());
		List<Tier> tierList = tierRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

		int top30percentPpp = rankRedisList.get((int)(totalRankPlayers * 0.3)).getPpp();
		int top10percentPpp = rankRedisList.get((int)(totalRankPlayers * 0.1)).getPpp();

		for (int i = 0; i < rankRedisList.size(); i++) {
			RankRedis rankRedis = rankRedisList.get(i);
			if (rankRedis.getWins() == 0 && rankRedis.getLosses() == 0) {
				rankRedis.updateTierImage(tierList.get(0).getImageUri());
			} else {
				if (i < 3) {
					rankRedis.updateTierImage(tierList.get(6).getImageUri());
					updateRankUser(key, zSetKey, rankRedis.getUserId(), rankRedis);
					continue;
				}
				if (rankRedis.getPpp() < 970) {
					// 970 미만
					rankRedis.updateTierImage(tierList.get(1).getImageUri());
				} else if (rankRedis.getPpp() < 1010) {
					// 970 ~ 1009
					rankRedis.updateTierImage(tierList.get(2).getImageUri());
				} else if (rankRedis.getPpp() < 1050) {
					// 1010 ~ 1049
					rankRedis.updateTierImage(tierList.get(3).getImageUri());
				} else {
					if ((rankRedis.getPpp() >= top30percentPpp) && (rankRedis.getPpp() < top10percentPpp)) {
						// 1050 이상, 30% 이상, 10% 미만
						rankRedis.updateTierImage(tierList.get(4).getImageUri());
					} else if (rankRedis.getPpp() >= top10percentPpp) {
						// 1050 이상, 10% 이상
						rankRedis.updateTierImage(tierList.get(5).getImageUri());
					} else {
						// 1050 이상, 30% 미만
						rankRedis.updateTierImage(tierList.get(3).getImageUri());
					}
				}
				updateRankUser(key, zSetKey, rankRedis.getUserId(), rankRedis);
			}
		}
	}

	@Transactional
	public RankRedis rollbackRank(TeamUser teamUser, int ppp, Long seasonId) {
		String hashkey = RedisKeyManager.getHashKey(seasonId);
		RankRedis myTeam = rankRedisRepository.findRankByUserId(hashkey, teamUser.getUser().getId());
		Rank rank = rankRepository.findByUserIdAndSeasonId(myTeam.getUserId(), seasonId)
			.orElseThrow(RankNotFoundException::new);
		int win = teamUser.getTeam().getWin() ? rank.getWins() - 1 : rank.getWins();
		int losses = !teamUser.getTeam().getWin() ? rank.getLosses() - 1 : rank.getLosses();
		log.info("Before: userId: " + teamUser.getUser().getIntraId() + ", " + "ppp: rank("
			+ rank.getPpp() + "), redis(" + myTeam.getPpp() + "), win: " + myTeam.getWins()
			+ ", losses: " + myTeam.getLosses());
		rank.modifyUserRank(ppp, win, losses);
		myTeam.changedRank(ppp, win, losses);
		updateRankUser(hashkey, RedisKeyManager.getZSetKey(seasonId), teamUser.getUser().getId(), myTeam);
		entityManager.flush();
		log.info("After: userId: " + teamUser.getUser().getIntraId() + ", " + "ppp: rank("
			+ rank.getPpp() + "), redis(" + myTeam.getPpp() + "), win: " + myTeam.getWins()
			+ ", losses: " + myTeam.getLosses());
		return myTeam;
	}
}
