package gg.pingpong.api.user.match.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.api.user.match.controller.response.MatchStatusResponseListDto;
import gg.pingpong.api.user.match.controller.response.SlotStatusResponseListDto;
import gg.pingpong.api.user.match.dto.MatchStatusDto;
import gg.pingpong.api.user.match.utils.SlotGenerator;
import gg.pingpong.api.user.season.service.SeasonFindService;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.pingpong.data.game.Game;
import gg.pingpong.data.game.type.StatusType;
import gg.pingpong.data.manage.SlotManagement;
import gg.pingpong.data.match.RedisMatchTime;
import gg.pingpong.data.match.RedisMatchUser;
import gg.pingpong.data.match.type.Option;
import gg.pingpong.data.rank.Tier;
import gg.pingpong.data.rank.redis.RankRedis;
import gg.pingpong.data.season.Season;
import gg.pingpong.data.user.User;
import gg.pingpong.data.user.type.RoleType;
import gg.pingpong.repo.game.GameRepository;
import gg.pingpong.repo.manage.SlotManagementRepository;
import gg.pingpong.repo.match.RedisMatchTimeRepository;
import gg.pingpong.repo.match.RedisMatchUserRepository;
import gg.pingpong.repo.rank.TierRepository;
import gg.pingpong.repo.rank.redis.RankRedisRepository;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.RedisKeyManager;
import gg.pingpong.utils.exception.match.SlotNotFoundException;
import gg.pingpong.utils.exception.tier.TierNotFoundException;
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
			user = RankRedis.from(userDto.getId(), userDto.getIntraId(), userDto.getTextColor(),
				season.getStartPpp(), tier.getImageUri());
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
