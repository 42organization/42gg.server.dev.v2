package gg.pingpong.api.user.rank.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.data.game.Rank;
import gg.pingpong.data.game.Season;
import gg.pingpong.data.game.Tier;
import gg.pingpong.repo.rank.RankRepository;
import gg.pingpong.repo.tier.TierRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TierService {
	private final TierRepository tierRepository;
	private final RankRepository rankRepository;

	/**
	 * 해당 시즌 랭킹의 티어를 모두 업데이트한다.
	 * <p>
	 *     참여한적 없으면 0번 티어. <br/>
	 *     ppp 970 미만 1번 티어. <br/>
	 *     ppp 1010 미만 2번 티어. <br/>
	 *     ppp 1050 미만 || 상위 30프로 미만 3번 티어. <br/>
	 *     ppp 1050 이상 && 상위 10프로 미만 4번 티어. <br/>
	 *     ppp 1050 이상 && 상위 10프로 이상 5번 티어. <br/>
	 *     최상위 3명 6번티어.
	 * <p/>
	 *
	 * @param season
	 */
	@Transactional
	public void updateAllTier(Season season) {
		List<Rank> rankList = rankRepository.findAllBySeasonIdOrderByPppDesc(season.getId());
		Long totalRankPlayers = rankRepository.countRealRankPlayers(season.getId());
		List<Tier> tierList = tierRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

		int top30percentPpp = rankList.get((int)(totalRankPlayers * 0.3)).getPpp();
		int top10percentPpp = rankList.get((int)(totalRankPlayers * 0.1)).getPpp();

		for (int i = 0; i < rankList.size(); i++) {
			Rank rank = rankList.get(i);
			if (!rank.isParticipated()) {
				rank.updateTier(tierList.get(0));
			} else if (i < 3) {
				rank.updateTier(tierList.get(6));
			} else if (rank.getPpp() < 970) {
				rank.updateTier(tierList.get(1));
			} else if (rank.getPpp() < 1010) {
				rank.updateTier(tierList.get(2));
			} else if (rank.getPpp() < 1050 || rank.getPpp() < top30percentPpp) {
				rank.updateTier(tierList.get(3));
			} else if (rank.getPpp() < top10percentPpp) {
				rank.updateTier(tierList.get(4));
			} else {
				rank.updateTier(tierList.get(5));
			}
		}
	}
}
