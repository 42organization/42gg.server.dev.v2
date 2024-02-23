package gg.pingpong.api.user.rank.service;

import org.springframework.stereotype.Service;

import gg.pingpong.data.rank.Rank;
import gg.pingpong.repo.rank.RankRepository;
import gg.pingpong.utils.exception.rank.RankNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankFindService {
	private final RankRepository rankRepository;

	public Rank findByUserIdAndSeasonId(Long userId, Long seasonId) {
		return rankRepository.findByUserIdAndSeasonId(userId, seasonId).orElseThrow(RankNotFoundException::new);
	}
}
