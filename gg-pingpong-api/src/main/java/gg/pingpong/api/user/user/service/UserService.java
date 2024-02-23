package gg.pingpong.api.user.user.service;

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

import gg.pingpong.api.global.utils.ExpLevelCalculator;
import gg.pingpong.api.global.utils.aws.AsyncNewUserImageUploader;
import gg.pingpong.api.user.rank.service.RankFindService;
import gg.pingpong.api.user.season.service.SeasonFindService;
import gg.pingpong.api.user.store.service.CoinHistoryService;
import gg.pingpong.api.user.store.service.ItemService;
import gg.pingpong.api.user.store.service.UserCoinChangeService;
import gg.pingpong.api.user.user.controller.request.UserProfileImageRequestDto;
import gg.pingpong.api.user.user.controller.response.UserAttendanceResponseDto;
import gg.pingpong.api.user.user.controller.response.UserDetailResponseDto;
import gg.pingpong.api.user.user.controller.response.UserHistoryResponseDto;
import gg.pingpong.api.user.user.controller.response.UserImageResponseDto;
import gg.pingpong.api.user.user.controller.response.UserLiveResponseDto;
import gg.pingpong.api.user.user.controller.response.UserNormalDetailResponseDto;
import gg.pingpong.api.user.user.controller.response.UserRankResponseDto;
import gg.pingpong.api.user.user.dto.UserBackgroundDto;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.pingpong.api.user.user.dto.UserEdgeDto;
import gg.pingpong.api.user.user.dto.UserHistoryData;
import gg.pingpong.api.user.user.dto.UserImageDto;
import gg.pingpong.api.user.user.dto.UserTextColorDto;
import gg.pingpong.data.game.Game;
import gg.pingpong.data.game.PChange;
import gg.pingpong.data.game.Rank;
import gg.pingpong.data.game.Season;
import gg.pingpong.data.game.Tier;
import gg.pingpong.data.game.redis.RankRedis;
import gg.pingpong.data.game.type.StatusType;
import gg.pingpong.data.store.Receipt;
import gg.pingpong.data.store.type.ItemStatus;
import gg.pingpong.data.store.type.ItemType;
import gg.pingpong.data.user.User;
import gg.pingpong.data.user.UserImage;
import gg.pingpong.data.user.type.BackgroundType;
import gg.pingpong.data.user.type.EdgeType;
import gg.pingpong.data.user.type.RacketType;
import gg.pingpong.data.user.type.RoleType;
import gg.pingpong.data.user.type.SnsType;
import gg.pingpong.repo.game.GameRepository;
import gg.pingpong.repo.game.PChangeRepository;
import gg.pingpong.repo.match.RedisMatchUserRepository;
import gg.pingpong.repo.noti.NotiRepository;
import gg.pingpong.repo.rank.RankRepository;
import gg.pingpong.repo.rank.RankV2Dto;
import gg.pingpong.repo.rank.TierRepository;
import gg.pingpong.repo.rank.redis.RankRedisRepository;
import gg.pingpong.repo.store.ReceiptRepository;
import gg.pingpong.repo.user.UserImageRepository;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.RedisKeyManager;
import gg.pingpong.utils.exception.pchange.PChangeNotExistException;
import gg.pingpong.utils.exception.rank.RankNotFoundException;
import gg.pingpong.utils.exception.rank.RedisDataNotFoundException;
import gg.pingpong.utils.exception.receipt.ReceiptNotFoundException;
import gg.pingpong.utils.exception.tier.TierNotFoundException;
import gg.pingpong.utils.exception.user.UserImageLargeException;
import gg.pingpong.utils.exception.user.UserImageNullException;
import gg.pingpong.utils.exception.user.UserImageTypeException;
import gg.pingpong.utils.exception.user.UserNotFoundException;
import gg.pingpong.utils.exception.user.UserTextColorException;
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
		return pageUsers.getContent().stream().map(User::getIntraId)
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
