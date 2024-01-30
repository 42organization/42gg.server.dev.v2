package com.gg.server.domain.match.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MatchIntegrationTestUtils {
	private final UserRepository userRepository;
	private final SeasonRepository seasonRepository;
	private final RankRedisRepository rankRedisRepository;
	private final SlotManagementRepository slotManagementRepository;
	private final GameRepository gameRepository;

	public User createUser() {
		User user = UserTestUtils.createUser();
		userRepository.save(user);
		return user;
	}

	public User createGuestUser() {
		User guest = UserTestUtils.createGuestUser();
		userRepository.save(guest);
		return guest;
	}

	public RankRedis addUsertoRankRedis(Long userId, Integer ppp, Long seasonId) {
		String randomId = UUID.randomUUID().toString();
		RankRedis rankRedis = new RankRedis(userId, randomId, ppp, 0, 0, "test",
			"https://42gg-public-image.s3.ap-northeast-2.amazonaws.com/images/nheo.jpeg", "#000000");
		rankRedisRepository.addRankData(RedisKeyManager.getHashKey(seasonId), userId, rankRedis);
		rankRedisRepository.addToZSet(RedisKeyManager.getZSetKey(seasonId), userId, ppp);
		return rankRedis;
	}

	public List<LocalDateTime> getTestSlotTimes(Integer interval) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime standard = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
			now.getHour(), 0);
		List<LocalDateTime> sampleSlots = new ArrayList<LocalDateTime>();
		for (int i = 0; i < 15; i++) {
			if (standard.plusMinutes(interval * i).isAfter(now)) {
				sampleSlots.add(standard.plusMinutes(interval * i));
			}
		}
		return sampleSlots;
	}

	public Season makeTestSeason(Integer pppGap) {
		Optional<Season> currentSeason = seasonRepository.findCurrentSeason(LocalDateTime.now());
		if (currentSeason.isPresent()) {
			return currentSeason.get();
		}
		Season season = new Season(
			"test",
			LocalDateTime.now().minusDays(1),
			LocalDateTime.of(9999, 12, 31, 23, 59, 59),
			1000,
			pppGap
		);
		seasonRepository.save(season);
		return season;
	}

	public SlotManagement makeTestSlotManagement(Integer interval) {
		SlotManagement slotManagement = SlotManagement.builder()
			.futureSlotTime(10)
			.pastSlotTime(0)
			.gameInterval(interval)
			.openMinute(5)
			.startTime(LocalDateTime.now().minusHours(2))
			.build();
		slotManagementRepository.save(slotManagement);
		return slotManagement;
	}

	/**
	 * 토너먼트에서 동일한 라운드의 경기들을 매칭 (생성)
	 * @param tournament 토너먼트
	 * @param round 해당 라운드와 동일한 라운드의 모든 경기를 매칭
	 *              ex ) 8강의 경우 8강의 4경기를 매칭
	 * @return 매칭된 토너먼트 게임
	 */
	public List<TournamentGame> matchTournamentGames(Tournament tournament, TournamentRound round) {
		Season season = seasonRepository.findCurrentSeason(LocalDateTime.now())
			.orElseThrow(() -> new IllegalArgumentException("현재 시즌이 존재하지 않습니다."));
		List<TournamentGame> sameRoundGames = TournamentGameTestUtils.matchTournamentGames(tournament,
			round.getRoundNumber(), season);
		for (TournamentGame tournamentGame : sameRoundGames) {
			Game game = tournamentGame.getGame();
			gameRepository.save(game);
		}
		return sameRoundGames;
	}

	/**
	 * 여러 경기에 대한 결과 수정
	 * @param tournamentGames
	 * @param scores
	 */
	public void updateTournamentGamesResult(List<TournamentGame> tournamentGames, List<Integer> scores) {
		int sum = scores.stream().mapToInt(Integer::intValue).sum();
		if (sum > 3 || sum < 0) {
			throw new IllegalArgumentException("게임 점수는 0 ~ 3 사이여야 합니다.");
		}
		List<Game> games = tournamentGames.stream().map(TournamentGame::getGame).collect(Collectors.toList());
		for (Game game : games) {
			updateTournamentGameResult(game, scores);
		}
	}

	/**
	 * 하나의 경기에 대한 결과 업데이트
	 * @param game
	 * @param scores
	 */
	public void updateTournamentGameResult(Game game, List<Integer> scores) {
		int sum = scores.stream().mapToInt(Integer::intValue).sum();
		if (sum > 3 || sum < 0) {
			throw new IllegalArgumentException("게임 점수는 0 ~ 3 사이여야 합니다.");
		}
		List<Team> teams = game.getTeams();
		teams.get(0).updateScore(scores.get(0), scores.get(0) > scores.get(1));
		teams.get(1).updateScore(scores.get(1), scores.get(0) < scores.get(1));
		// BEFORE -> LIVE -> WAIT -> END
		game.updateStatus();
		game.updateStatus();
		game.updateStatus();
	}

}
