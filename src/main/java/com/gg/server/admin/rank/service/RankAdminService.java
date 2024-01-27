package com.gg.server.admin.rank.service;

import static com.gg.server.domain.user.type.RoleType.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.season.exception.SeasonForbiddenException;
import com.gg.server.domain.season.exception.SeasonTimeBeforeException;
import com.gg.server.domain.tier.data.Tier;
import com.gg.server.domain.tier.data.TierRepository;
import com.gg.server.domain.tier.exception.TierNotFoundException;
import com.gg.server.domain.user.data.User;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RankAdminService {
	private final RankRepository rankRepository;
	private final UserAdminRepository userAdminRepository;
	private final SeasonRepository seasonRepository;
	private final TierRepository tierRepository;

	@Transactional
	public void addAllUserRankByNewSeason(SeasonAdminDto seasonAdminDto) {
		if (LocalDateTime.now().isAfter(seasonAdminDto.getStartTime())) {
			throw new SeasonTimeBeforeException();
		}
		List<User> users = userAdminRepository.findAll();

		List<Rank> ranks = new ArrayList<>();
		Season season = seasonRepository.findById(seasonAdminDto.getSeasonId()).get();
		Tier tier = tierRepository.findStartTier().orElseThrow(TierNotFoundException::new);
		users.forEach(user -> {
			if (user.getRoleType() != GUEST) {
				Rank userRank = Rank.from(user, season, seasonAdminDto.getStartPpp(), tier);
				ranks.add(userRank);
			}
		});
		rankRepository.saveAll(ranks);
	}

	@Transactional
	public void deleteAllUserRankBySeason(SeasonAdminDto seasonAdminDto) {
		if (LocalDateTime.now().isAfter(seasonAdminDto.getStartTime())) {
			throw new SeasonForbiddenException();
		}

		rankRepository.deleteAllBySeasonId(seasonAdminDto.getSeasonId());
	}
}
