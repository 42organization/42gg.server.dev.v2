package com.gg.server.domain.user.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.gg.server.data.game.Game;
import com.gg.server.data.game.PChange;
import com.gg.server.data.game.Rank;
import com.gg.server.data.game.Season;
import com.gg.server.data.game.Tier;
import com.gg.server.data.game.redis.RankRedis;
import com.gg.server.data.game.type.StatusType;
import com.gg.server.data.store.Receipt;
import com.gg.server.data.store.type.ItemStatus;
import com.gg.server.data.store.type.ItemType;
import com.gg.server.data.user.User;
import com.gg.server.data.user.UserImage;
import com.gg.server.data.user.type.BackgroundType;
import com.gg.server.data.user.type.EdgeType;
import com.gg.server.data.user.type.RacketType;
import com.gg.server.data.user.type.RoleType;
import com.gg.server.data.user.type.SnsType;
import com.gg.server.domain.coin.service.CoinHistoryService;
import com.gg.server.domain.coin.service.UserCoinChangeService;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.item.service.ItemService;
import com.gg.server.domain.match.data.RedisMatchUserRepository;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.pchange.data.PChangeRepository;
import com.gg.server.domain.pchange.exception.PChangeNotExistException;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.dto.RankV2Dto;
import com.gg.server.domain.rank.exception.RankNotFoundException;
import com.gg.server.domain.rank.exception.RedisDataNotFoundException;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.rank.service.RankFindService;
import com.gg.server.domain.receipt.data.ReceiptRepository;
import com.gg.server.domain.receipt.exception.ReceiptNotFoundException;
import com.gg.server.domain.season.service.SeasonFindService;
import com.gg.server.domain.tier.data.TierRepository;
import com.gg.server.domain.tier.exception.TierNotFoundException;
import com.gg.server.domain.user.data.UserImageRepository;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserAttendanceResponseDto;
import com.gg.server.domain.user.dto.UserBackgroundDto;
import com.gg.server.domain.user.dto.UserDetailResponseDto;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.dto.UserEdgeDto;
import com.gg.server.domain.user.dto.UserHistoryData;
import com.gg.server.domain.user.dto.UserHistoryResponseDto;
import com.gg.server.domain.user.dto.UserImageDto;
import com.gg.server.domain.user.dto.UserImageResponseDto;
import com.gg.server.domain.user.dto.UserLiveResponseDto;
import com.gg.server.domain.user.dto.UserNormalDetailResponseDto;
import com.gg.server.domain.user.dto.UserProfileImageRequestDto;
import com.gg.server.domain.user.dto.UserRankResponseDto;
import com.gg.server.domain.user.dto.UserTextColorDto;
import com.gg.server.domain.user.exception.UserImageLargeException;
import com.gg.server.domain.user.exception.UserImageNullException;
import com.gg.server.domain.user.exception.UserImageTypeException;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.domain.user.exception.UserTextColorException;
import com.gg.server.global.utils.ExpLevelCalculator;
import com.gg.server.global.utils.aws.AsyncNewUserImageUploader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserFindService userFindService;
	private final UserRepository userRepository;
	private final NotiRepository notiRepository;
	private final GameRepository gameRepository;
	private final RankRedisRepository rankRedisRepository;
	private final SeasonFindService seasonFindService;
	private final PChangeRepository pChangeRepository;
	private final RankFindService rankFindService;
	private final RedisMatchUserRepository redisMatchUserRepository;
	private final UserCoinChangeService userCoinChangeService;
	private final CoinHistoryService coinHistoryService;
	private final ReceiptRepository receiptRepository;
	private final AsyncNewUserImageUploader asyncNewUserImageUploader;
	private final UserImageRepository userImageRepository;
	private final ItemService itemService;
	private final TierRepository tierRepository;
	private final RankRepository rankRepository;

	/**
	 * @param intraId
	 * @return intraId가 포함된 유저들의 intraId를 페이징 관계없이 최대 5개까지 검색하여 List로 return
	 */
	@Transactional(readOnly = true)
	public List<String> findByPartOfIntraId(String intraId) {
		Pageable pageable = PageRequest.of(0, 5, Sort.by("intraId").ascending());
		Page<User> pageUsers = userRepository.findByIntraIdContains(pageable, intraId);
		return pageUsers.getContent().stream().map(user -> user.getIntraId())
			.collect(Collectors.toList());
	}

	/**
	 * @param user - event:
	 *             - null → 로그인 유저가 잡힌 매칭이 하나도 없을 때
	 *             - match → 매칭은 되었으나 게임시작 전일 때 or 매칭중인 경우
	 *             - game → 유저가 게임이 잡혔고 현재 게임중인 경우
	 *             <p>
	 *             - currentMatchMode
	 *             - normal
	 *             - rank
	 *             - null -> 매칭이 안잡혔을 때 or 게임 전
	 */
	@Transactional()
	public UserLiveResponseDto getUserLiveDetail(UserDto user) {
		int notiCnt = notiRepository.countNotCheckedNotiByUser(user.getId());
		Optional<Game> optionalGame = gameRepository.getLatestGameByUser(user.getId());
		int userMatchCnt = redisMatchUserRepository.countMatchTime(user.getId());
		if (optionalGame.isPresent()) {
			Game game = optionalGame.get();
			if (game.getStatus() == StatusType.LIVE || game.getStatus() == StatusType.WAIT) {
				return new UserLiveResponseDto(notiCnt, "game", game.getMode(), game.getId());
			} else if (game.getStatus() == StatusType.END) {
				PChange userPChange = pChangeRepository.findPChangeByUserIdAndGameId(user.getId(), game.getId())
					.orElseThrow(() -> new PChangeNotExistException());
				if (!userPChange.getIsChecked()) {
					userPChange.checkPChange();
					return new UserLiveResponseDto(notiCnt, "game", game.getMode(), game.getId());
				}
			}

			if (game.getStatus() == StatusType.BEFORE) {
				return new UserLiveResponseDto(notiCnt, "match", null, null);
			}
		}
		if (userMatchCnt > 0) {
			return new UserLiveResponseDto(notiCnt, "match", null, null);
		}
		return new UserLiveResponseDto(notiCnt, null, null, null);
	}

	@Transactional(readOnly = true)
	public UserDetailResponseDto getUserDetail(String targetUserIntraId) {
		User targetUser = userFindService.findByIntraId(targetUserIntraId);
		String statusMessage = userFindService.getUserStatusMessage(targetUser);
		Tier tier;
		try {
			tier = rankFindService.findByUserIdAndSeasonId(targetUser.getId(),
				seasonFindService.findCurrentSeason(LocalDateTime.now()).getId()).getTier();
		} catch (RankNotFoundException e) {
			tier = tierRepository.findStartTier().orElseThrow(TierNotFoundException::new);
		}
		return new UserDetailResponseDto(targetUser, statusMessage, tier);
	}

	@Transactional
	public void updateUser(RacketType racketType, String statusMessage, SnsType snsNotiOpt, String intraId) {
		User user = userFindService.findByIntraId(intraId);
		Season currentSeason = seasonFindService.findCurrentSeason(LocalDateTime.now());
		updateRedisRankStatusMessage(statusMessage, user, currentSeason);
		updateRankTableStatusMessage(user.getId(), statusMessage, currentSeason.getId());
		user.updateTypes(racketType, snsNotiOpt);
	}

	private void updateRankTableStatusMessage(Long userId, String statusMessage, Long seasonId) {
		Rank rank = rankFindService.findByUserIdAndSeasonId(userId, seasonId);
		rank.setStatusMessage(statusMessage);
	}

	private void updateRedisRankStatusMessage(String statusMessage, User user, Season currentSeason) {
		String hashKey = RedisKeyManager.getHashKey(currentSeason.getId());

		RankRedis userRank = rankRedisRepository.findRankByUserId(hashKey, user.getId());
		userRank.setStatusMessage(statusMessage);
		rankRedisRepository.updateRankData(hashKey, user.getId(), userRank);
	}

	/**
	 * @param intraId
	 * @param seasonId seasonId == 0 -> current season, else -> 해당 Id를 가진 season의 데이터
	 *                 <p>
	 *                 기존 쿼리
	 * @return 유저의 최근 10개의 랭크 경기 기록
	 * @Query(nativeQuery = true, value = "SELECT * FROM pchange " +
	 * "where game_id in (SELECT id FROM game where season = :season and mode = :mode ) " +
	 * "AND user_id = :intraId ORDER BY id Desc limit :limit")
	 * -> Limit에는 10이 기본으로 들어감
	 */
	@Transactional(readOnly = true)
	public UserHistoryResponseDto getUserHistory(String intraId, Long seasonId) {
		Season season;
		if (seasonId == 0) {
			season = seasonFindService.findCurrentSeason(LocalDateTime.now());
		} else {
			season = seasonFindService.findSeasonById(seasonId);
		}
		List<PChange> pChanges = pChangeRepository.findPChangesHistory(intraId, season.getId());
		List<UserHistoryData> historyData = pChanges.stream().map(UserHistoryData::new).collect(Collectors.toList());
		Collections.reverse(historyData);
		return new UserHistoryResponseDto(historyData);
	}

	/**
	 * @param targetUserIntraId
	 * @param seasonId          seasonId == 0 -> current season, else -> 해당 Id를 가진 season의 데이터
	 * @return
	 */
	@Transactional(readOnly = true)
	public UserRankResponseDto getUserRankDetail(String targetUserIntraId, Long seasonId) {
		Season season;
		if (seasonId == 0) {
			season = seasonFindService.findCurrentSeason(LocalDateTime.now());
		} else {
			season = seasonFindService.findSeasonById(seasonId);
		}
		String zSetKey = RedisKeyManager.getZSetKey(season.getId());
		String hashKey = RedisKeyManager.getHashKey(season.getId());
		User user = userFindService.findByIntraId(targetUserIntraId);
		try {
			Long userRanking = rankRedisRepository.getRankInZSet(zSetKey, user.getId());
			userRanking += 1;
			RankRedis userRank = rankRedisRepository.findRankByUserId(hashKey, user.getId());
			double winRate = (double)(userRank.getWins() * 10000 / (userRank.getWins() + userRank.getLosses())) / 100;
			return new UserRankResponseDto(userRanking.intValue(), userRank.getPpp(), userRank.getWins(),
				userRank.getLosses(), winRate);
		} catch (RedisDataNotFoundException ex) {
			return new UserRankResponseDto(-1, season.getStartPpp(), 0, 0, 0);
		} catch (ArithmeticException ex2) {
			return new UserRankResponseDto(-1, season.getStartPpp(), 0, 0, 0);
		}
	}

	public User getUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
	}

	@Transactional
	public void deleteKakaoId(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		user.updateKakaoId(null);
	}

	@Transactional(readOnly = true)
	public UserImageResponseDto getRankedUserImagesByPPP(Long seasonId) {
		Season targetSeason;

		if (seasonId == 0) {
			targetSeason = seasonFindService.findCurrentSeason(LocalDateTime.now());
		} else {
			targetSeason = seasonFindService.findSeasonById(seasonId);
		}
		try {
			List<UserImageDto> userImages = new ArrayList<>();
			List<RankV2Dto> dtos = rankRepository.findPppRankBySeasonId(0, 3, targetSeason.getId());
			List<User> users = userRepository.findByIntraIdIn(
				dtos.stream().map(RankV2Dto::getIntraId).collect(Collectors.toList()));
			for (RankV2Dto dto : dtos) {
				User user = users.stream()
					.filter(u -> u.getIntraId().equals(dto.getIntraId()))
					.findFirst()
					.orElseThrow(UserNotFoundException::new);
				userImages.add(new UserImageDto(user.getIntraId(), user.getImageUri(),
					user.getEdge(), dto.getTierImageUri()));
			}
			return new UserImageResponseDto(userImages);
		} catch (RedisDataNotFoundException ex) {
			return new UserImageResponseDto(new ArrayList<>());
		}
	}

	public UserImageResponseDto getRankedUserImagesByExp(PageRequest pageRequest) {
		List<User> users = userRepository.findAll(pageRequest).getContent();
		List<UserImageDto> userImages = new ArrayList<>();
		for (User user : users) {
			Tier tier = rankFindService.findByUserIdAndSeasonId(user.getId(),
				seasonFindService.findCurrentSeason(LocalDateTime.now()).getId()).getTier();
			userImages.add(new UserImageDto(user.getIntraId(), user.getImageUri(), user.getEdge(), tier.getImageUri()));
		}
		return new UserImageResponseDto(userImages);
	}

	@Transactional
	public UserAttendanceResponseDto attendUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

		int plus = userCoinChangeService.addAttendanceCoin(user);

		return new UserAttendanceResponseDto(user.getGgCoin() - plus, user.getGgCoin(), plus);
	}

	@Transactional
	public UserNormalDetailResponseDto getUserNormalDetail(UserDto user) {
		User loginUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
		Boolean isAdmin = user.getRoleType() == RoleType.ADMIN;
		Boolean isAttended = coinHistoryService.hasAttendedToday(loginUser);
		Integer level = ExpLevelCalculator.getLevel(user.getTotalExp());
		Tier tier;
		try {
			tier = rankFindService.findByUserIdAndSeasonId(user.getId(),
				seasonFindService.findCurrentSeason(LocalDateTime.now()).getId()).getTier();
		} catch (RankNotFoundException ex) {
			// 카카오 유저나 Rank가 없는 유저
			tier = tierRepository.findStartTier().orElseThrow(TierNotFoundException::new);
		}
		return new UserNormalDetailResponseDto(user.getIntraId(), loginUser.getImageUri(), isAdmin, isAttended,
			loginUser.getEdge(), tier.getName(), tier.getImageUri(), level);
	}

	@Transactional()
	public void updateTextColor(Long userId, UserTextColorDto textColorDto) {
		String textColor = textColorDto.getTextColor();
		Receipt receipt = receiptRepository.findById(textColorDto.getReceiptId())
			.orElseThrow(ReceiptNotFoundException::new);
		User loginUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

		if (!UserTextColorCheckService.check(textColor)) {
			throw new UserTextColorException();
		}

		itemService.checkItemType(receipt, ItemType.TEXT_COLOR);
		itemService.checkItemOwner(loginUser, receipt);
		itemService.checkItemStatus(receipt);

		loginUser.updateTextColor(textColor);
		receipt.updateStatus(ItemStatus.USED);
	}

	@Transactional
	public String updateEdge(UserDto user, UserEdgeDto userEdgeDto) {
		User loginUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
		EdgeType edgeType = EdgeType.getRandomEdgeType();
		Receipt receipt = receiptRepository.findById(userEdgeDto.getReceiptId())
			.orElseThrow(ReceiptNotFoundException::new);

		itemService.checkItemType(receipt, ItemType.EDGE);
		itemService.checkItemOwner(loginUser, receipt);
		itemService.checkItemStatus(receipt);

		loginUser.updateEdge(edgeType);
		receipt.updateStatus(ItemStatus.USED);

		return edgeType.toString();
	}

	@Transactional
	public String updateBackground(UserDto user, UserBackgroundDto userBackgroundDto) {
		User loginUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
		BackgroundType backgroundType = BackgroundType.getRandomBackgroundType();
		Receipt receipt = receiptRepository.findById(userBackgroundDto.getReceiptId())
			.orElseThrow(ReceiptNotFoundException::new);

		itemService.checkItemType(receipt, ItemType.BACKGROUND);
		itemService.checkItemOwner(loginUser, receipt);
		itemService.checkItemStatus(receipt);

		loginUser.updateBackground(backgroundType);
		receipt.updateStatus(ItemStatus.USED);

		return backgroundType.toString();
	}

	@Transactional
	public void updateUserProfileImage(UserDto user, UserProfileImageRequestDto userProfileImageRequestDto,
		MultipartFile userImageFile) throws IOException {
		User loginUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
		Receipt receipt = receiptRepository.findById(userProfileImageRequestDto.getReceiptId())
			.orElseThrow(ReceiptNotFoundException::new);

		itemService.checkItemType(receipt, ItemType.PROFILE_IMAGE);
		itemService.checkItemOwner(loginUser, receipt);
		itemService.checkItemStatus(receipt);

		if (userImageFile == null) {
			throw new UserImageNullException();
		}
		if (userImageFile.getSize() > 50000) {
			throw new UserImageLargeException();
		} else if (userImageFile.getContentType() == null || !userImageFile.getContentType().equals("image/jpeg")) {
			throw new UserImageTypeException();
		}

		UserImage userImage = userImageRepository.findTopByUserAndIsCurrentIsTrueOrderByIdDesc(loginUser)
			.orElseThrow(UserImageNullException::new);
		userImage.updateIsCurrent();
		asyncNewUserImageUploader.update(user.getIntraId(), userImageFile);
		receipt.updateStatus(ItemStatus.USED);
	}
}
