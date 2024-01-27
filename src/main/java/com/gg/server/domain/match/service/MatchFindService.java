package com.gg.server.domain.match.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.data.RedisMatchTime;
import com.gg.server.domain.match.data.RedisMatchTimeRepository;
import com.gg.server.domain.match.data.RedisMatchUser;
import com.gg.server.domain.match.data.RedisMatchUserRepository;
import com.gg.server.domain.match.dto.MatchStatusDto;
import com.gg.server.domain.match.dto.MatchStatusResponseListDto;
import com.gg.server.domain.match.dto.SlotStatusResponseListDto;
import com.gg.server.domain.match.exception.SlotNotFoundException;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.match.utils.SlotGenerator;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.service.SeasonFindService;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.tier.data.Tier;
import com.gg.server.domain.tier.data.TierRepository;
import com.gg.server.domain.tier.exception.TierNotFoundException;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.type.RoleType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchFindService {
	private final SlotManagementRepository slotManagementRepository;
	private final GameRepository gameRepository;
	private final UserRepository userRepository;
	private final RedisMatchUserRepository redisMatchUserRepository;
	private final SeasonFindService seasonFindService;
	private final RankRedisRepository rankRedisRepository;
	private final RedisMatchTimeRepository redisMatchTimeRepository;
	private final TierRepository tierRepository;

	@Transactional(readOnly = true)
	public MatchStatusResponseListDto getCurrentMatch(UserDto userDto) {
		SlotManagement slotManagement = slotManagementRepository.findCurrent(LocalDateTime.now())
			.orElseThrow(SlotNotFoundException::new);
		Optional<Game> myGame = gameRepository.findByStatusTypeAndUserId(StatusType.BEFORE, userDto.getId());
		if (myGame.isPresent()) {
			List<User> enemyTeam = userRepository.findEnemyByGameAndUser(myGame.get().getId(), userDto.getId());
			return new MatchStatusResponseListDto(List.of(new MatchStatusDto(
				myGame.get(), userDto.getIntraId(), enemyTeam.get(0).getIntraId(), slotManagement
			)));
		}
		Set<RedisMatchTime> enrolledSlots = redisMatchUserRepository.getAllMatchTime(userDto.getId());
		List<MatchStatusDto> dtos = enrolledSlots.stream()
			.map(e -> new MatchStatusDto(e, slotManagement.getGameInterval()))
			.sorted(Comparator.comparing(MatchStatusDto::getStartTime))
			.collect(Collectors.toList());
		return new MatchStatusResponseListDto(dtos);
	}

	@Transactional(readOnly = true)
	public SlotStatusResponseListDto getAllMatchStatus(UserDto userDto, Option option) {
		SlotManagement slotManagement = slotManagementRepository.findCurrent(LocalDateTime.now())
			.orElseThrow(SlotNotFoundException::new);
		Season season = seasonFindService.findCurrentSeason(LocalDateTime.now());
		Tier tier = tierRepository.findStartTier().orElseThrow(TierNotFoundException::new);
		RankRedis user;
		if (userDto.getRoleType().equals(RoleType.GUEST)) {
			user = RankRedis.from(userDto, season.getStartPpp(), tier.getImageUri());
		} else {
			user = rankRedisRepository
				.findRankByUserId(RedisKeyManager.getHashKey(season.getId()), userDto.getId());
		}
		SlotGenerator slotGenerator = new SlotGenerator(user, slotManagement, season, option);
		List<Game> games = gameRepository.findAllBetween(slotGenerator.getNow(), slotGenerator.getMaxTime());
		slotGenerator.addPastSlots();
		slotGenerator.addMatchedSlots(games);

		Optional<Game> myGame = gameRepository.findByStatusTypeAndUserId(StatusType.BEFORE, userDto.getId());
		Set<LocalDateTime> gameTimes = games.stream().map(Game::getStartTime).collect(Collectors.toSet());
		if (myGame.isPresent()) {
			groupEnrolledSlots(slotGenerator, myGame.get(), gameTimes);
		} else {
			groupEnrolledSlots(slotGenerator, gameTimes);
		}
		return slotGenerator.getResponseListDto();
	}

	private void groupEnrolledSlots(SlotGenerator slotGenerator, Game myGame, Set<LocalDateTime> gameTimes) {
		Set<LocalDateTime> enrolledTimes = redisMatchTimeRepository.getAllEnrolledStartTimes();
		slotGenerator.addMySlots(myGame);
		Set<LocalDateTime> notMyEnrolledTimes = enrolledTimes.stream()
			.filter(e -> !e.equals(myGame.getStartTime()) && !gameTimes.contains(e))
			.collect(Collectors.toSet());
		notMyEnrolledTimes.stream()
			.forEach(
				time -> {
					List<RedisMatchUser> allMatchUsers = redisMatchTimeRepository.getAllMatchUsers(time);
					slotGenerator.groupEnrolledSlot(time, allMatchUsers);
				}
			);
	}

	private void groupEnrolledSlots(SlotGenerator slotGenerator, Set<LocalDateTime> gameTimes) {
		Set<LocalDateTime> enrolledTimes = redisMatchTimeRepository.getAllEnrolledStartTimes();
		Set<RedisMatchTime> allMatchTime = redisMatchUserRepository.getAllMatchTime(
			slotGenerator.getMatchUser().getUserId());
		slotGenerator.addMySlots(allMatchTime);
		Set<LocalDateTime> times = allMatchTime.stream().map(RedisMatchTime::getStartTime)
			.collect(Collectors.toSet());
		Set<LocalDateTime> notMyEnrolledTimes = enrolledTimes.stream()
			.filter(e -> !times.contains(e) && !gameTimes.contains(e))
			.collect(Collectors.toSet());
		notMyEnrolledTimes.stream().forEach(
			time -> {
				List<RedisMatchUser> allMatchUsers = redisMatchTimeRepository.getAllMatchUsers(time);
				slotGenerator.groupEnrolledSlot(time, allMatchUsers);
			}
		);
	}
}
