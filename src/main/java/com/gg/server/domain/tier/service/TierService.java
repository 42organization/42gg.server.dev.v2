package com.gg.server.domain.tier.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.tier.data.Tier;
import com.gg.server.domain.tier.data.TierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TierService {
	private final TierRepository tierRepository;
	private final RankRepository rankRepository;

	@Transactional
	public void updateAllTier(Season season) {
		List<Rank> rankList = rankRepository.findAllBySeasonIdOrderByPppDesc(season.getId());
		Long totalRankPlayers = rankRepository.countRealRankPlayers(season.getId());
		List<Tier> tierList = tierRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

		int top30percentPpp = rankList.get((int)(totalRankPlayers * 0.3)).getPpp();
		int top10percentPpp = rankList.get((int)(totalRankPlayers * 0.1)).getPpp();

		for (int i = 0; i < rankList.size(); i++) {
			Rank rank = rankList.get(i);
			if (rank.getWins() == 0 && rank.getLosses() == 0) {
				rank.updateTier(tierList.get(0));
			} else {
				if (i < 3) {
					rank.updateTier(tierList.get(6));
					continue;
				}
				if (rank.getPpp() < 970) {
					// 970 미만
					rank.updateTier(tierList.get(1));
				} else if (rank.getPpp() < 1010) {
					// 970 - 1009
					rank.updateTier(tierList.get(2));
				} else if (rank.getPpp() < 1050) {
					// 1010 - 1049
					rank.updateTier(tierList.get(3));
				} else if (rank.getPpp() >= 1050) {
					if (rank.getPpp() >= top30percentPpp && rank.getPpp() < top10percentPpp) {
						// 1050 이상, 30% 이상, 10% 미만
						rank.updateTier(tierList.get(4));
					} else if (rank.getPpp() >= top10percentPpp) {
						// 1050 이상, 10% 이상
						rank.updateTier(tierList.get(5));
					} else {
						// 1050 이상, 30% 미만
						rank.updateTier(tierList.get(3));
					}
				}
			}
		}
	}
}
