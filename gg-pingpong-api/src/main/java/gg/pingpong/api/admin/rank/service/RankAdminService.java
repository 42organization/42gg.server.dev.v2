package gg.pingpong.api.admin.rank.service;

import static gg.pingpong.data.user.type.RoleType.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.admin.repo.user.UserAdminRepository;
import gg.pingpong.api.admin.season.dto.SeasonAdminDto;
import gg.pingpong.data.game.Rank;
import gg.pingpong.data.game.Season;
import gg.pingpong.data.game.Tier;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.rank.RankRepository;
import gg.pingpong.repo.season.SeasonRepository;
import gg.pingpong.repo.tier.TierRepository;
import gg.pingpong.utils.exception.season.SeasonForbiddenException;
import gg.pingpong.utils.exception.season.SeasonTimeBeforeException;
import gg.pingpong.utils.exception.tier.TierNotFoundException;
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
