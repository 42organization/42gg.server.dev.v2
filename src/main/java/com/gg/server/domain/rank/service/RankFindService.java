package com.gg.server.domain.rank.service;

import org.springframework.stereotype.Service;

import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.exception.RankNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankFindService {
	private final RankRepository rankRepository;

	public Rank findByUserIdAndSeasonId(Long userId, Long seasonId) {
		return rankRepository.findByUserIdAndSeasonId(userId, seasonId).orElseThrow(() -> new RankNotFoundException());
	}
}
