package gg.pingpong.api.user.user.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.apache.http.HttpHeaders;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.data.game.Game;
import gg.data.game.type.Mode;
import gg.data.game.type.StatusType;
import gg.data.rank.Rank;
import gg.data.rank.Tier;
import gg.data.rank.redis.RankRedis;
import gg.data.season.Season;
import gg.data.store.Item;
import gg.data.store.Receipt;
import gg.data.store.type.ItemStatus;
import gg.data.store.type.ItemType;
import gg.data.user.User;
import gg.data.user.type.BackgroundType;
import gg.data.user.type.EdgeType;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.pingpong.api.admin.store.controller.request.ItemUpdateRequestDto;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.global.utils.UserImageHandler;
import gg.pingpong.api.user.game.controller.request.RankResultReqDto;
import gg.pingpong.api.user.game.service.GameService;
import gg.pingpong.api.user.store.service.CoinHistoryService;
import gg.pingpong.api.user.user.controller.request.UserModifyRequestDto;
import gg.pingpong.api.user.user.controller.response.CoinHistoryResponseDto;
import gg.pingpong.api.user.user.controller.response.UserAttendanceResponseDto;
import gg.pingpong.api.user.user.controller.response.UserCoinHistoryListResponseDto;
import gg.pingpong.api.user.user.controller.response.UserCoinResponseDto;
import gg.pingpong.api.user.user.controller.response.UserDetailResponseDto;
import gg.pingpong.api.user.user.controller.response.UserHistoryResponseDto;
import gg.pingpong.api.user.user.controller.response.UserLiveResponseDto;
import gg.pingpong.api.user.user.controller.response.UserNormalDetailResponseDto;
import gg.pingpong.api.user.user.controller.response.UserRankResponseDto;
import gg.pingpong.api.user.user.controller.response.UserSearchResponseDto;
import gg.pingpong.api.user.user.dto.UserTextColorDto;
import gg.repo.game.GameRepository;
import gg.repo.rank.RankRepository;
import gg.repo.rank.TierRepository;
import gg.repo.rank.redis.RankRedisRepository;
import gg.repo.season.SeasonRepository;
import gg.repo.store.CoinHistoryRepository;
import gg.repo.store.CoinPolicyRepository;
import gg.repo.store.ReceiptRepository;
import gg.repo.user.UserRepository;
import gg.utils.ItemTestUtils;
import gg.utils.RedisKeyManager;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.dto.GameInfoDto;
import gg.utils.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class UserControllerTest {

	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	AuthTokenProvider tokenProvider;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RankRedisRepository redisRepository;

	@Autowired
	TierRepository tierRepository;

	@Autowired
	RankRepository rankRepository;

	@Autowired
	SeasonRepository seasonRepository;

	@Autowired
	GameRepository gameRepository;

	@Autowired
	GameService gameService;

	@Autowired
	CoinPolicyRepository coinPolicyRepository;

	@Autowired
	CoinHistoryRepository coinHistoryRepository;

	@Autowired
	ReceiptRepository receiptRepository;

	@Autowired
	CoinHistoryService coinHistoryService;
	@Autowired
	ItemTestUtils itemTestUtils;
	@MockBean
	UserImageHandler userImageHandler;
	User admin;

	@BeforeEach
	public void setUp() {
		testDataUtils.createTierSystem("pingpong");
		testDataUtils.createSeason();
		admin = testDataUtils.createAdminUserForItem();
	}

	@AfterEach
	public void flushRedis() {
		redisRepository.deleteAll();
	}

	@Test
	@DisplayName("live")
	public void userLiveTest() throws Exception {
		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		String liveUrl = "/pingpong/users/live";
		String event = "game";
		int notiCnt = 2;
		Mode currentMatchMode = Mode.RANK;
		GameInfoDto gameInfo = testDataUtils.addMockDataUserLiveApi(event, notiCnt, currentMatchMode.getCode(), userId);
		Game testGame = gameRepository.getById(gameInfo.getGameId());

		// Rank Live 게임 테스트
		String contentAsString1 = mockMvc.perform(
				get(liveUrl).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
		UserLiveResponseDto userLiveResponseDto1 = objectMapper.readValue(contentAsString1, UserLiveResponseDto.class);
		assertThat(userLiveResponseDto1.getEvent()).isEqualTo(event);
		assertThat(userLiveResponseDto1.getNotiCount()).isEqualTo(notiCnt);
		assertThat(userLiveResponseDto1.getCurrentMatchMode()).isEqualTo(currentMatchMode);
		assertThat(userLiveResponseDto1.getGameId()).isEqualTo(gameInfo.getGameId());

		// Rank 점수 입력 테스트
		RankResultReqDto rankResultReqDto = new RankResultReqDto(gameInfo.getGameId(),
			gameInfo.getEnemyTeamId(), 1,
			gameInfo.getMyTeamId(), 2);
		assertThat(testGame.getStatus()).isEqualTo(StatusType.LIVE);
		gameService.createRankResult(rankResultReqDto, gameInfo.getEnemyUserId());
		assertThat(testGame.getStatus()).isEqualTo(StatusType.END);

		String contentAsString2 = mockMvc.perform(
				get(liveUrl).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
		UserLiveResponseDto userLiveResponseDto2 = objectMapper.readValue(contentAsString2, UserLiveResponseDto.class);
		assertThat(userLiveResponseDto2.getEvent()).isEqualTo(event);
		assertThat(userLiveResponseDto2.getNotiCount()).isEqualTo(notiCnt);
		assertThat(userLiveResponseDto2.getCurrentMatchMode()).isEqualTo(currentMatchMode);
		assertThat(userLiveResponseDto2.getGameId()).isEqualTo(gameInfo.getGameId());

		// Rank PChange is_checked 테스트
		String contentAsString3 = mockMvc.perform(
				get(liveUrl).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
		UserLiveResponseDto userLiveResponseDto3 = objectMapper.readValue(contentAsString3, UserLiveResponseDto.class);
		assertThat(userLiveResponseDto3.getEvent()).isEqualTo(null);
		assertThat(userLiveResponseDto3.getNotiCount()).isEqualTo(notiCnt);
		assertThat(userLiveResponseDto3.getCurrentMatchMode()).isEqualTo(null);
		assertThat(userLiveResponseDto3.getGameId()).isEqualTo(null);
	}

	@Test
	@DisplayName("GET /pingpong/users")
	public void userNormalDetail() throws Exception {
		//given
		String url = "/pingpong/users";
		String intraId = "intra";
		String email = "email";
		//        String imageUrl = "imageUrl";

		User newUser = testDataUtils.createNewUser(intraId, email, RacketType.PENHOLDER, SnsType.BOTH, RoleType.ADMIN);
		Season season = testDataUtils.createSeason();

		String accessToken = tokenProvider.createToken(newUser.getId());
		Tier tier = tierRepository.findStartTier().get();
		testDataUtils.createUserRank(newUser, "statusMessage", season, tier);

		//when
		String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
		UserNormalDetailResponseDto responseDto = objectMapper.readValue(contentAsString,
			UserNormalDetailResponseDto.class);

		//then
		assertThat(responseDto.getIntraId()).isEqualTo(intraId);
		//        assertThat(responseDto.getUserImageUri()).isEqualTo(imageUrl);
		assertThat(responseDto.getIsAdmin()).isTrue();
		assertThat(responseDto.getIsAttended());
	}

	@Test
	@DisplayName("searches?intraId=${IntraId}")
	public void searchUser() throws Exception {
		//given
		String[] intraId = {"intraId", "2intra2", "2intra", "aaaa", "bbbb"};
		String email = "email";
		User user = null;
		for (String intra : intraId) {
			user = testDataUtils.createNewUser(intra, email, RacketType.PENHOLDER,
				SnsType.BOTH, RoleType.ADMIN);
		}
		String accessToken = tokenProvider.createToken(user.getId());
		String keyWord = "intra";
		String url = "/pingpong/users/searches?intraId=" + keyWord;

		//when
		String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
		UserSearchResponseDto userSearchResponseDto = objectMapper.readValue(contentAsString,
			UserSearchResponseDto.class);

		//then
		assertThat(userSearchResponseDto.getUsers().size()).isEqualTo(3);
	}

	@Test
	@DisplayName("[GET] {targetId}")
	public void getUserDetail() throws Exception {
		//given
		Season season = testDataUtils.createSeason();
		String intraId = "intraId";
		String email = "email";
		String statusMessage = "statusMessage";
		User newUser = testDataUtils.createNewUser(intraId, email, RacketType.PENHOLDER, SnsType.BOTH, RoleType.USER);
		String accessToken = tokenProvider.createToken(newUser.getId());
		Tier tier = tierRepository.findStartTier().get();
		testDataUtils.createUserRank(newUser, statusMessage, season, tier);
		String url = "/pingpong/users/" + newUser.getIntraId();

		//when
		String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		UserDetailResponseDto responseDto = objectMapper.readValue(contentAsString, UserDetailResponseDto.class);

		//then
		Assertions.assertThat(responseDto.getIntraId()).isEqualTo(intraId);
		Assertions.assertThat(responseDto.getStatusMessage()).isEqualTo(statusMessage);
		Assertions.assertThat(responseDto.getLevel()).isEqualTo(1);
		Assertions.assertThat(responseDto.getCurrentExp()).isEqualTo(0);
		System.out.println(responseDto);
	}

	@Test
	@DisplayName("/{intraId}/rank?season={seasonId}")
	public void userRankDetail() throws Exception {
		//given
		Season season = testDataUtils.createSeason();
		User newUser = testDataUtils.createNewUser();
		String accessToken = tokenProvider.createToken(newUser.getId());
		testDataUtils.createUserRank(newUser, "statusMessage", season);

		//when
		String url = "/pingpong/users/" + newUser.getIntraId() + "/rank?season=" + season.getId();
		String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		UserRankResponseDto responseDto = objectMapper.readValue(contentAsString, UserRankResponseDto.class);

		//then
		Assertions.assertThat(responseDto.getRank()).isEqualTo(-1);
		Assertions.assertThat(responseDto.getWins()).isEqualTo(0);
		Assertions.assertThat(responseDto.getLosses()).isEqualTo(0);
		Assertions.assertThat(responseDto.getPpp()).isEqualTo(season.getStartPpp());
		System.out.println(responseDto);
	}

	@Test
	@DisplayName("/{intraId}/historics?season={seasonId}")
	public void getUserHistory() throws Exception {
		//given
		Season season = testDataUtils.createSeason();
		User newUser = testDataUtils.createNewUser();
		String accessToken = tokenProvider.createToken(newUser.getId());

		LocalDateTime startTime = LocalDateTime.now().minusDays(1);
		LocalDateTime endTime = startTime.plusMinutes(15);
		testDataUtils.createMockMatch(newUser, season, startTime, endTime);

		LocalDateTime startTime1 = LocalDateTime.now().minusDays(2);
		LocalDateTime endTime1 = startTime1.plusMinutes(15);
		testDataUtils.createMockMatch(newUser, season, startTime1, endTime1);

		LocalDateTime startTime2 = LocalDateTime.now().minusDays(3);
		LocalDateTime endTime2 = startTime2.plusMinutes(15);
		testDataUtils.createMockMatch(newUser, season, startTime2, endTime2);

		String url = "/pingpong/users/" + newUser.getIntraId() + "/historics?season=" + season.getId();

		//when
		String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		UserHistoryResponseDto responseDto = objectMapper.readValue(contentAsString, UserHistoryResponseDto.class);

		//then
		Assertions.assertThat(responseDto.getHistorics().size()).isEqualTo(3);
		//        List<UserHistoryData> historics = responseDto.getHistorics();
		//        findPChangesHistory 에는 정렬한다는 내용이 없는 것 같아 보류
		//        Assertions.assertThat(historics)
		//                .isSortedAccordingTo(Comparator.comparing(UserHistoryData::getDate));
	}

	@Test
	@DisplayName("[put] {intraId}")
	public void updateUser() throws Exception {
		//given
		Season season = testDataUtils.createSeason();
		String intraId = "intraId";
		String email = "email";
		String imageUrl = "imageUrl";
		User newUser = testDataUtils.createNewUser(intraId, email, RacketType.PENHOLDER, SnsType.BOTH, RoleType.USER);
		String statusMessage = "statusMessage";
		testDataUtils.createUserRank(newUser, statusMessage, season);
		String accessToken = tokenProvider.createToken(newUser.getId());
		String url = "/pingpong/users/" + newUser.getIntraId();

		String newStatusMessage = "newStatusMessage";
		RacketType newRacketType = RacketType.SHAKEHAND;
		SnsType newSnsType = SnsType.SLACK;

		//when
		mockMvc.perform(put(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(
					objectMapper.writeValueAsString(new UserModifyRequestDto(newRacketType, newStatusMessage,
						newSnsType))))
			.andExpect(status().isNoContent());
		//then
		String hashKey = RedisKeyManager.getHashKey(season.getId());
		RankRedis rank = redisRepository.findRankByUserId(hashKey, newUser.getId());
		Optional<Rank> rankOptional = rankRepository.findByUserIdAndSeasonId(newUser.getId(), season.getId());

		if (rankOptional.isPresent()) {
			Rank rank1 = rankOptional.get();
			Assertions.assertThat(rank1.getStatusMessage()).isEqualTo(newStatusMessage);
		} else {
			Assertions.fail("랭크 업데이트 실패");
		}

		Optional<User> userOptional = userRepository.findById(newUser.getId());

		if (userOptional.isPresent()) {
			User user = userOptional.get();
			Assertions.assertThat(user.getRacketType()).isEqualTo(newRacketType);
			Assertions.assertThat(user.getSnsNotiOpt()).isEqualTo(newSnsType);
			Assertions.assertThat(rank.getStatusMessage()).isEqualTo(newStatusMessage);
		} else {
			Assertions.fail("유저 업데이트 실패");
		}

	}

	@Test
	@DisplayName("[post] /attendance")
	public void attendUserTest() throws Exception {
		//given
		testDataUtils.createCoinPolicy(admin, 1, 0, 0, 0);
		String accessToken = testDataUtils.getLoginAccessToken();
		String url = "/pingpong/users/attendance";

		//when
		String contentAsString = mockMvc.perform(
				post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
		UserAttendanceResponseDto result = objectMapper.readValue(contentAsString, UserAttendanceResponseDto.class);

		//then
		System.out.println(result.getAfterCoin());
		Assertions.assertThat(result.getAfterCoin() - result.getBeforeCoin()).isEqualTo(result.getCoinIncrement());
	}

	@Test
	@DisplayName("[patch] text-color")
	public void updateTextColorTest() throws Exception {
		//given
		Season season = testDataUtils.createSeason();
		String intraId = "intraId";
		String email = "email";
		User newUser = testDataUtils.createNewUser(intraId, email, RacketType.PENHOLDER, SnsType.BOTH,
			RoleType.USER);
		String statusMessage = "statusMessage";
		testDataUtils.createUserRank(newUser, statusMessage, season);
		String accessToken = tokenProvider.createToken(newUser.getId());
		String url = "/pingpong/users/text-color";
		ItemUpdateRequestDto dto = new ItemUpdateRequestDto("name", "mainContent",
			"subContent", 100, 50, ItemType.TEXT_COLOR);
		Item item = itemTestUtils.createItem(admin, dto);
		Receipt receipt = itemTestUtils.purchaseItem(newUser, newUser, item);
		//        Receipt receipt = receiptRepository.findById(4L).get();
		String newTextColor = "#FFFFFF";

		//when
		mockMvc.perform(patch(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new UserTextColorDto(receipt.getId(), newTextColor))))
			.andExpect(status().is2xxSuccessful());
		//then
		Optional<User> userOptional = userRepository.findById(newUser.getId());

		if (userOptional.isPresent()) {
			User user = userOptional.get();
			Assertions.assertThat(user.getTextColor()).isEqualTo(newTextColor);
		} else {
			Assertions.fail("유저 업데이트 실패");
		}

		AssertionsForClassTypes.assertThat(receipt.getStatus()).isEqualTo(ItemStatus.USED);
	}

	@Test
	@DisplayName("[patch] edge")
	public void updateEdgeTest() throws Exception {
		//given
		Season season = testDataUtils.createSeason();
		String intraId = "intraId";
		String email = "email";
		User newUser = testDataUtils.createNewUser(intraId, email, RacketType.PENHOLDER, SnsType.BOTH,
			RoleType.USER);
		String statusMessage = "statusMessage";
		testDataUtils.createUserRank(newUser, statusMessage, season);
		String accessToken = tokenProvider.createToken(newUser.getId());
		ItemUpdateRequestDto dto = new ItemUpdateRequestDto("name", "mainContent",
			"subContent", 100, 50, ItemType.EDGE);
		Item item = itemTestUtils.createItem(admin, dto);
		Receipt receipt = itemTestUtils.purchaseItem(newUser, newUser, item);
		//        Receipt receipt = receiptRepository.findById(3L).get();
		String url = "/pingpong/users/edge";

		//when
		mockMvc.perform(patch(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(objectMapper.writeValueAsString(receipt.getId()))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is2xxSuccessful())
			.andReturn().getResponse().getContentAsString();

		//then
		log.info("user.getEdge() : {}", newUser.getEdge());
		Optional<User> userOptional = userRepository.findById(newUser.getId());

		if (userOptional.isPresent()) {
			User user = userOptional.get();
			boolean isEdgeTypeValid = Arrays.stream(EdgeType.values()).anyMatch(v -> v.equals(user.getEdge()));
			Assertions.assertThat(isEdgeTypeValid).isEqualTo(true);
		} else {
			Assertions.fail("유저 업데이트 실패");
		}

		AssertionsForClassTypes.assertThat(receipt.getStatus()).isEqualTo(ItemStatus.USED);
	}

	@Test
	@DisplayName("[get]/pingpong/users/coin")
	public void getUserCoin() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

		String url = "/pingpong/users/coin";

		String contentAsString = mockMvc.perform(get(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		UserCoinResponseDto result = objectMapper.readValue(contentAsString, UserCoinResponseDto.class);
		int userCoin = userRepository.findById(userId).orElseThrow(UserNotFoundException::new).getGgCoin();
		assertThat(result.getCoin()).isEqualTo(userCoin);
		System.out.println(userCoin);
	}

	@Test
	@DisplayName("[patch] background")
	public void updateBackgroundTest() throws Exception {
		//given
		Season season = testDataUtils.createSeason();
		String intraId = "intraId";
		String email = "email";
		User newUser = testDataUtils.createNewUser(intraId, email, RacketType.PENHOLDER, SnsType.BOTH,
			RoleType.USER);
		ItemUpdateRequestDto dto = new ItemUpdateRequestDto("name", "mainContent",
			"subContent", 100, 50, ItemType.BACKGROUND);
		Item item = itemTestUtils.createItem(admin, dto);
		Receipt receipt = itemTestUtils.purchaseItem(newUser, newUser, item);
		String statusMessage = "statusMessage";
		testDataUtils.createUserRank(newUser, statusMessage, season);
		String accessToken = tokenProvider.createToken(newUser.getId());

		//        Receipt receipt = receiptRepository.findById(2L).get();
		String uri = "/pingpong/users/background";

		//when
		mockMvc.perform(patch(uri)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.content(objectMapper.writeValueAsString(receipt.getId()))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is2xxSuccessful())
			.andReturn().getResponse().getContentAsString();

		//then
		log.info("user.getBackground() : {}", newUser.getBackground());
		Optional<User> userOptional = userRepository.findById(newUser.getId());

		if (userOptional.isPresent()) {
			User user = userOptional.get();
			boolean isBackgroundTypeValid = Arrays
				.stream(BackgroundType.values()).anyMatch(v -> v.equals(user.getBackground()));
			Assertions.assertThat(isBackgroundTypeValid).isEqualTo(true);
		} else {
			Assertions.fail("유저 업데이트 실패");
		}

		AssertionsForClassTypes.assertThat(receipt.getStatus()).isEqualTo(ItemStatus.USED);
	}

	@Test
	@DisplayName("[get]/pingpong/users/coinhistory")
	public void getUserCoinHistory() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User user = userRepository.getById(userId);
		testDataUtils.createCoinPolicy(admin, 0, 1, 3, 2);

		coinHistoryService.addNormalCoin(user);
		coinHistoryService.addRankWinCoin(user);
		coinHistoryService.addNormalCoin(user);
		String url = "/pingpong/users/coinhistory?page=1&size=5";

		String contentAsString = mockMvc.perform(get(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		UserCoinHistoryListResponseDto result = objectMapper.readValue(contentAsString,
			UserCoinHistoryListResponseDto.class);

		System.out.println(result.getTotalPage());
		for (CoinHistoryResponseDto temp : result.getUseCoinList()) {
			System.out.println(temp.getHistory() + " " + temp.getAmount() + " " + temp.getCreatedAt());
		}
	}

	@Test
	@DisplayName("[post]/pingpong/users/profile-image")
	public void getUserImage() throws Exception {
		String mockS3Path = "mockS3Path";
		Mockito.when(userImageHandler
				.uploadToS3(Mockito.any(MultipartFile.class), Mockito.any(String.class)))
			.thenReturn(mockS3Path);
		//        String accessToken = testDataUtils.getLoginAccessToken();
		ItemUpdateRequestDto dto = new ItemUpdateRequestDto("name", "mainContent",
			"subContent", 100, 50, ItemType.PROFILE_IMAGE);
		Item item = itemTestUtils.createItem(admin, dto);
		User user = testDataUtils.createNewUser();
		testDataUtils.createUserImage(user);
		String accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
		Receipt receipt = itemTestUtils.purchaseItem(user, user, item);
		//        Receipt receipt = receiptRepository.findById(7L).orElseThrow(ReceiptNotFoundException::new);
		MockMultipartFile image = new MockMultipartFile("profileImage", "imagefile.jpeg", "image/jpeg",
			"<<jpeg data>>".getBytes());
		MockMultipartFile jsonFile = new MockMultipartFile("userProfileImageRequestDto", "", "application/json",
			("{\"receiptId\": " + receipt.getId() + "}").getBytes());

		String contentAsString = mockMvc.perform(multipart("/pingpong/users/profile-image")
				.file(image)
				.file(jsonFile)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isNoContent())
			.andReturn().getResponse().getContentAsString();
		AssertionsForClassTypes.assertThat(receipt.getStatus()).isEqualTo(ItemStatus.USED);
	}
}
