package gg.pingpong.api.admin.tournament.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.pingpong.api.admin.tournament.controller.request.TournamentAdminAddUserRequestDto;
import gg.pingpong.api.admin.tournament.controller.request.TournamentAdminCreateRequestDto;
import gg.pingpong.api.admin.tournament.controller.request.TournamentAdminUpdateRequestDto;
import gg.pingpong.api.admin.tournament.controller.request.TournamentGameUpdateRequestDto;
import gg.pingpong.api.admin.tournament.service.TournamentAdminService;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.user.game.dto.TeamReqDto;
import gg.pingpong.api.user.match.utils.MatchIntegrationTestUtils;
import gg.pingpong.data.game.type.Mode;
import gg.pingpong.data.season.Season;
import gg.pingpong.data.tournament.Tournament;
import gg.pingpong.data.tournament.TournamentGame;
import gg.pingpong.data.tournament.TournamentUser;
import gg.pingpong.data.tournament.type.TournamentRound;
import gg.pingpong.data.tournament.type.TournamentStatus;
import gg.pingpong.data.tournament.type.TournamentType;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.game.PChangeRepository;
import gg.pingpong.repo.tournarment.TournamentGameRepository;
import gg.pingpong.repo.tournarment.TournamentRepository;
import gg.pingpong.repo.tournarment.TournamentUserRepository;
import gg.pingpong.utils.TestDataUtils;
import gg.pingpong.utils.annotation.IntegrationTest;
import gg.pingpong.utils.dto.GameInfoDto;
import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.CustomRuntimeException;
import lombok.RequiredArgsConstructor;

@IntegrationTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
@Transactional
class TournamentAdminControllerTest {

	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	AuthTokenProvider tokenProvider;

	@Autowired
	TournamentAdminService tournamentAdminService;

	@Autowired
	TournamentRepository tournamentRepository;

	@Autowired
	TournamentUserRepository tournamentUserRepository;

	@Autowired
	TournamentGameRepository tournamentGameRepository;

	@Autowired
	PChangeRepository pChangeRepository;

	@Autowired
	private MatchIntegrationTestUtils matchTestUtils;

	@Nested
	@DisplayName("토너먼트_관리_수정_컨트롤러_테스트")
	class TournamentAdminControllerUpdateTest {
		@BeforeEach
		void beforeEach() {
			testDataUtils.createSlot(15);
		}

		@Test
		@DisplayName("토너먼트_업데이트_성공")
		void success() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			tokenProvider.getUserIdFromAccessToken(accessToken);

			Tournament tournament = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(2).withHour(14).withMinute(0),
				LocalDateTime.now().plusDays(2).withHour(16).withMinute(0),
				TournamentStatus.BEFORE);

			TournamentAdminUpdateRequestDto updateDto = testDataUtils.createUpdateRequestDto(
				LocalDateTime.now().plusDays(3).withHour(14).withMinute(0),
				LocalDateTime.now().plusDays(3).withHour(16).withMinute(0),
				TournamentType.MASTER);

			String url = "/pingpong/admin/tournaments/" + tournament.getId();

			String content = objectMapper.writeValueAsString(updateDto);

			// when
			String contentAsString = mockMvc.perform(patch(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isNoContent())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);

			// then
			Tournament result = tournamentRepository.findById(tournament.getId()).get();
			assertThat(result.getTitle()).isEqualTo(tournament.getTitle());
			assertThat(result.getContents()).isEqualTo(tournament.getContents());
			assertThat(result.getStartTime()).isEqualTo(updateDto.getStartTime());
			assertThat(result.getEndTime()).isEqualTo(updateDto.getEndTime());
			assertThat(result.getType()).isEqualTo(updateDto.getType());
			assertThat(result.getStatus()).isEqualTo(tournament.getStatus());
		}

		@Test
		@DisplayName("토너먼트_없는_경우")
		void tournamentNotFound() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			tokenProvider.getUserIdFromAccessToken(accessToken);

			TournamentAdminUpdateRequestDto updateDto = testDataUtils.createUpdateRequestDto(
				LocalDateTime.now().plusDays(2).withHour(14).withMinute(0),
				LocalDateTime.now().plusDays(2).withHour(16).withMinute(0),
				TournamentType.MASTER);

			String url = "/pingpong/admin/tournaments/" + 1111;

			String content = objectMapper.writeValueAsString(updateDto);
			// when, then
			String contentAsString = mockMvc.perform(patch(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);
		}

		@Test
		@DisplayName("토너먼트_업데이트_기간_겹치는_경우")
		void tournamentConflicted() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			tokenProvider.getUserIdFromAccessToken(accessToken);

			Tournament tournamentAlreadyExist = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(3).withHour(14).withMinute(0),
				LocalDateTime.now().plusDays(3).withHour(16).withMinute(0),
				TournamentStatus.BEFORE);

			Tournament tournamentToChange = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(3).withHour(18).withMinute(0),
				LocalDateTime.now().plusDays(3).withHour(20).withMinute(0),
				TournamentStatus.BEFORE);

			// 겹치는 시간 조절
			TournamentAdminUpdateRequestDto updateDto = testDataUtils.createUpdateRequestDto(
				LocalDateTime.now().plusDays(3).withHour(13).withMinute(0),
				LocalDateTime.now().plusDays(3).withHour(15).withMinute(0),
				TournamentType.MASTER);

			String url = "/pingpong/admin/tournaments/" + tournamentToChange.getId();

			String content = objectMapper.writeValueAsString(updateDto);

			// when, then
			String contentAsString = mockMvc.perform(patch(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isConflict())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);
		}

		@Test
		@DisplayName("기간내_게임_존재")
		void gameAlreadyExist() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();

			GameInfoDto game = testDataUtils.createGame(testDataUtils.createNewUser("testUser"),
				LocalDateTime.now().plusDays(3).withHour(14).withMinute(0),
				LocalDateTime.now().plusDays(3).withHour(14).withMinute(15),
				testDataUtils.createSeason(), Mode.NORMAL);

			Tournament tournament = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(2).withHour(14).withMinute(0),
				LocalDateTime.now().plusDays(2).withHour(16).withMinute(0),
				TournamentStatus.BEFORE);

			// 겹치는 시간 조절
			TournamentAdminUpdateRequestDto updateDto = testDataUtils.createUpdateRequestDto(
				tournament.getStartTime().plusDays(1),
				tournament.getEndTime().plusDays(1),
				TournamentType.MASTER);

			String url = "/pingpong/admin/tournaments/" + tournament.getId();

			String content = objectMapper.writeValueAsString(updateDto);

			// when, then
			String contentAsString = mockMvc.perform(patch(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isConflict())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);
		}

		@Test
		@DisplayName("이미_시작했거나_종료된_토너먼트_수정")
		void canNotUpdate() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			tokenProvider.getUserIdFromAccessToken(accessToken);

			Tournament liveTournament = testDataUtils.createTournament(
				LocalDateTime.now().minusHours(1).withMinute(0),
				LocalDateTime.now().plusHours(2).withMinute(0),
				TournamentStatus.LIVE);

			Tournament endedTournament = testDataUtils.createTournament(
				LocalDateTime.now().minusHours(3).withMinute(0),
				LocalDateTime.now().minusHours(1).withMinute(0),
				TournamentStatus.END);

			TournamentAdminUpdateRequestDto updateTournamentDto = testDataUtils.createUpdateRequestDto(
				LocalDateTime.now().plusDays(2).plusHours(3).withMinute(0),
				LocalDateTime.now().plusDays(2).plusHours(5).withMinute(0),
				TournamentType.MASTER);

			String url = "/pingpong/admin/tournaments/" + liveTournament.getId();

			String content = objectMapper.writeValueAsString(updateTournamentDto);

			// when live tournament test, then
			String contentAsString = mockMvc.perform(patch(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isForbidden())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);

			url = "/pingpong/admin/tournaments/" + endedTournament.getId();

			// when ended tournament test, then
			contentAsString = mockMvc.perform(patch(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isForbidden())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);
		}

		@Test
		@DisplayName("토너먼트_잘못된_기간")
		void wrongTournamentTime() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			tokenProvider.getUserIdFromAccessToken(accessToken);

			Tournament tournamentToChange = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(2).withHour(14).withMinute(0),
				LocalDateTime.now().plusDays(2).withHour(16).withMinute(0),
				TournamentStatus.BEFORE);

			TournamentAdminUpdateRequestDto updateDto1 = testDataUtils.createUpdateRequestDto(
				tournamentToChange.getStartTime(),
				tournamentToChange.getStartTime(),
				TournamentType.MASTER);

			TournamentAdminUpdateRequestDto updateDto2 = testDataUtils.createUpdateRequestDto(
				tournamentToChange.getEndTime(),
				tournamentToChange.getStartTime(),
				TournamentType.MASTER);

			TournamentAdminUpdateRequestDto updateDto3 = testDataUtils.createUpdateRequestDto(
				LocalDateTime.now().plusDays(2).withHour(14).withMinute(5),
				LocalDateTime.now().plusDays(2).withHour(16).withMinute(5),
				TournamentType.MASTER);

			String url = "/pingpong/admin/tournaments/" + tournamentToChange.getId();
			// when startTime == endTime, then
			String content = objectMapper.writeValueAsString(updateDto1);

			String contentAsString = mockMvc.perform(patch(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isForbidden())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);

			// when startTime > endTime test, then
			content = objectMapper.writeValueAsString(updateDto2);

			contentAsString = mockMvc.perform(patch(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isForbidden())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);

			// when startTime minute invalid test, then
			content = objectMapper.writeValueAsString(updateDto3);

			contentAsString = mockMvc.perform(patch(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isForbidden())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);
		}

		@Test
		@DisplayName("잘못된_dto")
		void wrongDto() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			tokenProvider.getUserIdFromAccessToken(accessToken);

			Tournament tournamentToChange = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(2).withHour(14).withMinute(0),
				LocalDateTime.now().plusDays(2).withHour(16).withMinute(0),
				TournamentStatus.BEFORE);

			TournamentAdminUpdateRequestDto updateDto1 = testDataUtils.createUpdateRequestDto(
				tournamentToChange.getStartTime(),
				tournamentToChange.getStartTime(),
				null);

			String url = "/pingpong/admin/tournaments/" + tournamentToChange.getId();
			// when startTime == endTime, then
			String content = objectMapper.writeValueAsString(updateDto1);

			String contentAsString = mockMvc.perform(patch(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);
		}
	}

	@Nested
	@DisplayName("토너먼트_관리_삭제_컨트롤러_테스트")
	class TournamentAdminControllerDeleteTest {
		@Test
		@DisplayName("토너먼트_삭제_성공")
		void success() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			tokenProvider.getUserIdFromAccessToken(accessToken);

			Tournament tournament = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(2).plusHours(1),
				LocalDateTime.now().plusDays(2).plusHours(3),
				TournamentStatus.BEFORE);

			List<TournamentGame> tournamentGameList = testDataUtils.createTournamentGameList(tournament, 7);

			String url = "/pingpong/admin/tournaments/" + tournament.getId();

			// when
			String contentAsString = mockMvc.perform(delete(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isNoContent())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);

			// then
			tournamentRepository.findById(tournament.getId()).ifPresent(
				a -> {
					throw new CustomRuntimeException("삭제되지 않았습니다.", ErrorCode.BAD_REQUEST);
				});
		}

		@Test
		@DisplayName("토너먼트_없는_경우")
		void tournamentNotFound() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			tokenProvider.getUserIdFromAccessToken(accessToken);

			String url = "/pingpong/admin/tournaments/" + 1111;

			// when, then
			String contentAsString = mockMvc.perform(delete(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);
		}

		@Test
		@DisplayName("이미_시작했거나_종료된_토너먼트_수정")
		void canNotDelete() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			tokenProvider.getUserIdFromAccessToken(accessToken);

			Tournament liveTournament = testDataUtils.createTournament(
				LocalDateTime.now().minusHours(1),
				LocalDateTime.now().plusHours(2),
				TournamentStatus.LIVE);

			Tournament endedTournament = testDataUtils.createTournament(
				LocalDateTime.now().minusHours(3),
				LocalDateTime.now().minusHours(1),
				TournamentStatus.END);

			String url = "/pingpong/admin/tournaments/" + liveTournament.getId();

			// when live tournament test, then
			String contentAsString = mockMvc.perform(delete(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isForbidden())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);

			url = "/pingpong/admin/tournaments/" + endedTournament.getId();

			// when ended tournament test, then
			contentAsString = mockMvc.perform(delete(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isForbidden())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);
		}
	}

	@Nested
	@DisplayName("토너먼트_관리_생성_컨트롤러_테스트")
	class TournamentAdminControllerCreateTest {
		@BeforeEach
		void beforeEach() {
			testDataUtils.createSlot(15);
		}

		@Test
		@DisplayName("토너먼트 생성 성공")
		void success() throws Exception {
			//given
			String accessToken = testDataUtils.getAdminLoginAccessToken();

			TournamentAdminCreateRequestDto createDto = testDataUtils.createRequestDto(
				LocalDateTime.now().plusDays(3).withHour(14).withMinute(0),
				LocalDateTime.now().plusDays(3).withHour(16).withMinute(0),
				TournamentType.ROOKIE);

			String url = "/pingpong/admin/tournaments";
			String content = objectMapper.writeValueAsString(createDto);

			//when, then
			String contentAsString = mockMvc.perform(post(url)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);
		}

		@Test
		@DisplayName("기간내_게임_존재")
		void gameAlreadyExist() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			GameInfoDto game = testDataUtils.createGame(testDataUtils.createNewUser("testUser"),
				LocalDateTime.now().plusDays(3).withHour(14).withMinute(0),
				LocalDateTime.now().plusDays(3).withHour(14).withMinute(15),
				testDataUtils.createSeason(), Mode.NORMAL);

			TournamentAdminCreateRequestDto createDto = testDataUtils.createRequestDto(
				LocalDateTime.now().plusDays(3).withHour(14).withMinute(0),
				LocalDateTime.now().plusDays(3).withHour(16).withMinute(0),
				TournamentType.ROOKIE);

			String url = "/pingpong/admin/tournaments";
			String content = objectMapper.writeValueAsString(createDto);

			// when, then
			String contentAsString = mockMvc.perform(post(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isConflict())
				.andReturn().getResponse().getContentAsString();

			System.out.println(contentAsString);
		}

		@Test
		@DisplayName("잘못된 DTO - 길이 초과")
		void invalidLength() throws Exception {
			//given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			int leftLimit = 97; // letter 'a'
			int rightLimit = 122; // letter 'z'
			int targetStringLength = 3100;
			Random random = new Random();
			String contents = random.ints(leftLimit, rightLimit + 1)
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
			TournamentAdminCreateRequestDto createDto = new TournamentAdminCreateRequestDto(
				"1st rookie tournament",
				contents,
				LocalDateTime.now().plusDays(3).withHour(14).withMinute(0),
				LocalDateTime.now().plusDays(3).withHour(16).withMinute(0),
				TournamentType.ROOKIE);

			String url = "/pingpong/admin/tournaments";
			String content = objectMapper.writeValueAsString(createDto);

			//when, then
			String contentAsString = mockMvc.perform(post(url)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();
		}
	}

	@Nested
	@DisplayName("관리자_토너먼트_유저_추가_컨트롤러_테스트")
	class TournamentAdminControllerAddUserTest {
		@Test
		@DisplayName("유저_추가_성공")
		void success() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			tokenProvider.getUserIdFromAccessToken(accessToken);

			Tournament tournament1 = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(2).plusHours(1),
				LocalDateTime.now().plusDays(2).plusHours(3),
				TournamentStatus.BEFORE);
			Tournament tournament2 = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(3).plusHours(1),
				LocalDateTime.now().plusDays(3).plusHours(3),
				TournamentStatus.BEFORE);
			User user = testDataUtils.createNewUser("testUser");
			testDataUtils.createTournamentUser(user, tournament2, true);

			TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto(user.getIntraId());

			String url = "/pingpong/admin/tournaments/" + tournament1.getId() + "/users";
			String content = objectMapper.writeValueAsString(requestDto);

			// when
			String contentAsString = mockMvc.perform(post(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

			// then
			System.out.println(contentAsString);
			tournament1.getTournamentUsers().stream().filter(tu -> tu.getUser().equals(user)).findAny()
				.orElseThrow(() -> new CustomRuntimeException("토너먼트 유저 리스트에 추가 안됨", ErrorCode.BAD_REQUEST));
			tournamentUserRepository.findAllByTournamentId(tournament1.getId())
				.stream().filter(tu -> tu.getUser().getIntraId().equals(requestDto.getIntraId()))
				.findAny().orElseThrow(() -> new CustomRuntimeException("토너먼트 유저 테이블에 추가 안됨", ErrorCode.BAD_REQUEST));
		}

		@Test
		@DisplayName("토너먼트_없는_경우")
		void tournamentNotFound() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			tokenProvider.getUserIdFromAccessToken(accessToken);

			User user = testDataUtils.createNewUser("test");

			TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto(user.getIntraId());

			String url = "/pingpong/admin/tournaments/" + 9999 + "/users";

			String content = objectMapper.writeValueAsString(requestDto);

			// when, then
			String contentAsString = mockMvc.perform(post(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();

		}

		@Test
		@DisplayName("이미_시작했거나_종료된_토너먼트_수정")
		void canNotUpdate() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			tokenProvider.getUserIdFromAccessToken(accessToken);

			Tournament tournament = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(0).plusHours(-1),
				LocalDateTime.now().plusDays(0).plusHours(1),
				TournamentStatus.LIVE);
			User user = testDataUtils.createNewUser("test");

			TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto(user.getIntraId());

			String url = "/pingpong/admin/tournaments/" + tournament.getId() + "/users";

			String content = objectMapper.writeValueAsString(requestDto);

			// when, then
			String contentAsString = mockMvc.perform(post(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isForbidden())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("찾을_수_없는_유저")
		void userNotFound() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			tokenProvider.getUserIdFromAccessToken(accessToken);

			Tournament tournament = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(2).plusHours(1),
				LocalDateTime.now().plusDays(2).plusHours(3),
				TournamentStatus.BEFORE);

			TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto("nobody");

			String url = "/pingpong/admin/tournaments/" + tournament.getId() + "/users";

			String content = objectMapper.writeValueAsString(requestDto);

			// when, then
			String contentAsString = mockMvc.perform(post(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("해당_토너먼트_참가자인_경우")
		void alreadyTournamentParticipant() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			tokenProvider.getUserIdFromAccessToken(accessToken);

			Tournament tournament = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(2).plusHours(1),
				LocalDateTime.now().plusDays(2).plusHours(3),
				TournamentStatus.BEFORE);

			User user = testDataUtils.createNewUser("test");
			TournamentUser participant = testDataUtils.createTournamentUser(user, tournament, false);
			TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto(user.getIntraId());

			String url = "/pingpong/admin/tournaments/" + tournament.getId() + "/users";
			String content = objectMapper.writeValueAsString(requestDto);

			// when, then
			String contentAsString = mockMvc.perform(post(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isConflict())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("토너먼트_대기자_신청")
		void waitUserTest() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			tokenProvider.getUserIdFromAccessToken(accessToken);

			Tournament tournament = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(2).plusHours(1),
				LocalDateTime.now().plusDays(2).plusHours(3),
				TournamentStatus.BEFORE);

			User user = testDataUtils.createNewUser("testUser0");
			for (int i = 1; i <= 8; i++) {
				testDataUtils.createTournamentUser(testDataUtils.createNewUser("testUser" + i), tournament, true);
			}
			TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto(user.getIntraId());

			String url = "/pingpong/admin/tournaments/" + tournament.getId() + "/users";
			String content = objectMapper.writeValueAsString(requestDto);

			// when
			String contentAsString = mockMvc.perform(post(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

			// then
			tournamentUserRepository.findAllByTournamentId(tournament.getId())
				.stream()
				.filter(tu -> tu.getUser().getIntraId().equals(user.getIntraId()))
				.findAny()
				.filter(tu -> !tu.getIsJoined())
				.orElseThrow(() -> new CustomRuntimeException("waitlist 제대로 등록 안됨", ErrorCode.BAD_REQUEST));
		}
	}

	@Nested
	@DisplayName("관리자_토너먼트_유저_삭제_컨트롤러_테스트")
	class TournamentAdminControllerDeleteUserTest {
		@Test
		@DisplayName("유저_삭제_성공")
		void success() throws Exception {
			// given
			int maxTournamentUser = 8;
			String accessToken = testDataUtils.getAdminLoginAccessToken();

			Tournament tournament = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(2).plusHours(1),
				LocalDateTime.now().plusDays(2).plusHours(3),
				TournamentStatus.BEFORE);

			for (int i = 0; i < maxTournamentUser; i++) {
				TournamentUser tournamentUser = testDataUtils.createTournamentUser(
					testDataUtils.createNewUser("testUser" + i), tournament, true);
			}
			for (int i = maxTournamentUser; i < maxTournamentUser + 4; i++) {
				TournamentUser tournamentUser = testDataUtils.createTournamentUser(
					testDataUtils.createNewUser("testUser" + i), tournament, false);
			}

			User user = tournament.getTournamentUsers().get(6).getUser();

			String url = "/pingpong/admin/tournaments/" + tournament.getId() + "/users/" + user.getId();
			// when
			String contentAsString = mockMvc.perform(delete(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isNoContent())
				.andReturn().getResponse().getContentAsString();

			// then
			System.out.println(contentAsString);
			List<TournamentUser> tournamentUserList = tournament.getTournamentUsers();
			tournamentUserList.stream().filter(tu -> tu.getUser().equals(user)).findAny()
				.ifPresent(a -> {
					throw new CustomRuntimeException("토너먼트 유저 리스트에 삭제 안됨", ErrorCode.BAD_REQUEST);
				});
			tournamentUserRepository.findByTournamentIdAndUserId(tournament.getId(), user.getId())
				.ifPresent(a -> {
					throw new CustomRuntimeException("토너먼트 유저 레포에서 삭제 안됨", ErrorCode.BAD_REQUEST);
				});
			for (int i = 0; i < maxTournamentUser; i++) {
				if (!tournamentUserList.get(i).getIsJoined()) {
					throw new CustomRuntimeException("대기자 => 참여자 전환 제대로 안됨", ErrorCode.BAD_REQUEST);
				}
			}
			for (int i = maxTournamentUser; i < tournamentUserList.size(); i++) {
				if (tournamentUserList.get(i).getIsJoined()) {
					throw new CustomRuntimeException("정해진 참가자 수보다 참가자가 많음", ErrorCode.BAD_REQUEST);
				}
			}
		}

		@Test
		@DisplayName("토너먼트_없는_경우")
		void tournamentNotFound() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();

			User user = testDataUtils.createNewUser("testUser");

			TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto(user.getIntraId());

			String url = "/pingpong/admin/tournaments/" + 1234 + "/users/" + user.getId();

			String content = objectMapper.writeValueAsString(requestDto);

			// when, then
			String contentAsString = mockMvc.perform(delete(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();

		}

		@Test
		@DisplayName("이미_시작했거나_종료된_토너먼트_수정")
		void canNotUpdate() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();

			Tournament tournament = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(0).plusHours(-1),
				LocalDateTime.now().plusDays(0).plusHours(1),
				TournamentStatus.LIVE);
			User user = testDataUtils.createNewUser("testUser");

			TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto(user.getIntraId());

			String url = "/pingpong/admin/tournaments/" + tournament.getId() + "/users/" + user.getId();

			String content = objectMapper.writeValueAsString(requestDto);

			// when, then
			String contentAsString = mockMvc.perform(delete(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isForbidden())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("찾을_수_없는_유저")
		void userNotFound() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();

			Tournament tournament = testDataUtils.createTournament(
				LocalDateTime.now().plusDays(2).plusHours(1),
				LocalDateTime.now().plusDays(2).plusHours(3),
				TournamentStatus.BEFORE);

			TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto("nobody");

			String url = "/pingpong/admin/tournaments/" + tournament.getId() + "/users/" + "4321";

			String content = objectMapper.writeValueAsString(requestDto);

			// when, then
			String contentAsString = mockMvc.perform(delete(url)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();
		}
	}

	@Nested
	@DisplayName("[Patch] /pingpong/admin/tournaments/{tournamentId}/games")
	class AdminUpdateTournamentGameTest {
		private String accessToken;
		private Tournament tournament;
		private List<TournamentGame> allTournamentGames;
		Season season;

		@BeforeEach
		void setUp() {
			// 토너먼트 생성하고 8강 & 4강은 게임 점수 입력하고 종료된 상태이고 결승전 매칭된 상태로 초기화
			season = testDataUtils.createSeason();
			testDataUtils.createSlotManagement(15);
			tournament = testDataUtils.createTournamentWithUser(Tournament.ALLOWED_JOINED_NUMBER, 4, "test");
			allTournamentGames = testDataUtils.createTournamentGameList(tournament, 7);
			tournament.updateStatus(TournamentStatus.LIVE);
			List<TournamentGame> quarterGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.QUARTER_FINAL_1);
			matchTestUtils.updateTournamentGamesResult(quarterGames, List.of(2, 0));
			List<TournamentGame> semiGames = matchTestUtils.matchTournamentGames(tournament,
				TournamentRound.SEMI_FINAL_1);
			matchTestUtils.updateTournamentGamesResult(semiGames, List.of(2, 0));

			accessToken = testDataUtils.getAdminLoginAccessToken();
		}

		@Test
		@DisplayName("토너먼트_게임_수정_성공")
		void updateTournamentGameSuccess() throws Exception {
			// given
			String url = "/pingpong/admin/tournaments/" + tournament.getId() + "/games";

			int myTeamScore = 2;
			int otherTeamScore = 1;
			TournamentGame tournamentGame = allTournamentGames.stream()
				.filter(tg -> tg.getTournamentRound() == TournamentRound.SEMI_FINAL_1)
				.findAny()
				.orElseThrow();
			TournamentGame nextTournamentGame = allTournamentGames.stream()
				.filter(tg -> tg.getTournamentRound() == TournamentRound.THE_FINAL)
				.findAny()
				.orElseThrow();
			User user1 = tournamentGame.getGame().getTeams().get(0).getTeamUsers().get(0).getUser();
			User user2 = tournamentGame.getGame().getTeams().get(1).getTeamUsers().get(0).getUser();
			testDataUtils.createUserRank(user1, "", season);
			testDataUtils.createUserRank(user2, "", season);

			TournamentGameUpdateRequestDto requestDto = new TournamentGameUpdateRequestDto(tournamentGame.getId(),
				nextTournamentGame.getId(),
				new TeamReqDto(tournamentGame.getGame().getTeams().get(0).getId(), myTeamScore),
				new TeamReqDto(tournamentGame.getGame().getTeams().get(1).getId(), otherTeamScore));

			String content = objectMapper.writeValueAsString(requestDto);
			// when
			mockMvc.perform(patch(url)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			TournamentGame resTournamentGame = tournamentGameRepository.findById(tournamentGame.getId()).orElseThrow();
			assertThat(resTournamentGame.getGame().getTeams().get(0).getScore()).isEqualTo(myTeamScore);
			assertThat(resTournamentGame.getGame().getTeams().get(1).getScore()).isEqualTo(otherTeamScore);
			assertThat(
				pChangeRepository.findByUserIdAndGameId(user1.getId(), tournamentGame.getGame().getId())).isNotEmpty();
			assertThat(
				pChangeRepository.findByUserIdAndGameId(user2.getId(), tournamentGame.getGame().getId())).isNotEmpty();
		}

		@Test
		@DisplayName("토너먼트_게임_수정_불가능")
		void updateTournamentGameEnable() throws Exception {
			// given
			String url = "/pingpong/admin/tournaments/" + tournament.getId() + "/games";
			int myTeamScore = 2;
			int otherTeamScore = 1;
			TournamentGame tournamentGame = allTournamentGames.stream()
				.filter(tg -> tg.getTournamentRound() == TournamentRound.QUARTER_FINAL_1)
				.findAny()
				.orElseThrow();
			TournamentGame nextTournamentGame = allTournamentGames.stream()
				.filter(tg -> tg.getTournamentRound() == TournamentRound.SEMI_FINAL_1)
				.findAny()
				.orElseThrow();
			TournamentGameUpdateRequestDto requestDto = new TournamentGameUpdateRequestDto(tournamentGame.getId(),
				nextTournamentGame.getId(),
				new TeamReqDto(tournamentGame.getGame().getTeams().get(0).getId(), myTeamScore),
				new TeamReqDto(tournamentGame.getGame().getTeams().get(1).getId(), otherTeamScore));

			String content = objectMapper.writeValueAsString(requestDto);
			// when
			mockMvc.perform(patch(url)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.content(content)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden())
				.andReturn().getResponse().getContentAsString();
		}
	}
}
