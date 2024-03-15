package gg.party.api.user.room;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

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

import gg.auth.utils.AuthTokenProvider;
import gg.data.party.Category;
import gg.data.party.PartyPenalty;
import gg.data.party.Room;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.user.room.controller.request.RoomCreateReqDto;
import gg.party.api.user.room.controller.response.RoomCreateResDto;
import gg.party.api.user.room.controller.response.RoomListResDto;
import gg.party.api.user.room.controller.response.RoomResDto;
import gg.party.api.user.room.service.RoomService;
import gg.repo.party.CategoryRepository;
import gg.repo.party.PartyPenaltyRepository;
import gg.repo.party.RoomRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RoomControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AuthTokenProvider tokenProvider;
	@Autowired
	RoomRepository roomRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	PartyPenaltyRepository partyPenaltyRepository;
	@Autowired
	RoomService RoomService;
	User userTester;
	User reportedTester;
	String userAccessToken;
	String reportedAccessToken;
	Category testCategory;

	@Nested
	@DisplayName("방 전체 조회 테스트")
	class FindAllActiveRoomList {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("findControllerTester", "findControllerTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			Category testCategory = testDataUtils.createNewCategory("category");
			Room openRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.OPEN);
			Room startRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				3, 2, 180, RoomType.START);
			Room finishRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				3, 2, 180, RoomType.FINISH);
			Room hiddenRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.OPEN);
			Room failRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.FAIL);
		}

		/**
		 * 조회가 실패하는 경우는 조건에 해당하는 게시글이 없을때 빈 리스트를 반환
		 * Penalty 상태의 사용자도 게시글 목록은 조회할 수 있어야하기에 유저를 나누지 않았음.
		 */
		@Test
		@DisplayName("조회 성공 201")
		public void success() throws Exception {
			String url = "/party/rooms";
			//given
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//when
			RoomListResDto resp = RoomService.findRoomList();
			//then
			List<RoomResDto> roomList = resp.getRoomList();
			for (RoomResDto responseDto : roomList) {
				assertThat(responseDto.getStatus()).isIn(RoomType.OPEN.toString(), RoomType.START.toString());
			}
		}
	}

	/**
	 * 방 생성시 패널티 상태의 사용자는 실패해야함
	 */
	@Nested
	@DisplayName("방 생성 테스트")
	class CreateRoom {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("userTester", "userTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			reportedTester = testDataUtils.createNewUser("reportedTester", "reportedTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			PartyPenalty testPenalty = testDataUtils.createNewPenalty(reportedTester, "test", "test",
				LocalDateTime.now(), 60);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			reportedAccessToken = tokenProvider.createToken(reportedTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
		}

		@Test
		@DisplayName("생성 성공 201")
		public void success() throws Exception {
			String url = "/party/rooms";
			//given
			RoomCreateReqDto roomCreateReqDto = new RoomCreateReqDto("title", "content", testCategory.getId(),
				4, 2, 180);
			String jsonRequest = objectMapper.writeValueAsString(roomCreateReqDto);
			//when
			String contentAsString = mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
			RoomCreateResDto rcrd = objectMapper.readValue(contentAsString,
				RoomCreateResDto.class);
			//then
			assertThat(rcrd.getRoomId()).isNotNull();
		}

		@Test
		@DisplayName("패널티 유저 생성 실패 403")
		public void penaltyUserFail() throws Exception {
			String url = "/party/rooms";
			//given
			RoomCreateReqDto roomCreateReqDto = new RoomCreateReqDto("title", "content", testCategory.getId(), 4, 2,
				180);
			String requestBody = objectMapper.writeValueAsString(roomCreateReqDto);
			// when && then
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + reportedAccessToken))
				.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("없는 카테고리로 인한 생성 실패 404")
		public void notValidCategoryFail() throws Exception {
			String url = "/party/rooms";
			//given
			RoomCreateReqDto roomCreateReqDto = new RoomCreateReqDto("title", "content", 100L, 4, 2, 180);
			String requestBody = objectMapper.writeValueAsString(roomCreateReqDto);
			// when && then
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("최소인원 > 최대인원 생성 실패 400")
		public void minOverMaxFail() throws Exception {
			String url = "/party/rooms";
			//given
			RoomCreateReqDto roomCreateReqDto = new RoomCreateReqDto("title", "content", testCategory.getId(), 2, 4,
				180);
			String requestBody = objectMapper.writeValueAsString(roomCreateReqDto);
			// when && then
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("이상한 시간으로 인한 생성 실패 400")
		public void notValidTimeFail() throws Exception {
			String url = "/party/rooms";
			//given
			RoomCreateReqDto roomCreateReqDto = new RoomCreateReqDto("title", "content", testCategory.getId(), 4, 2,
				-180);
			String requestBody = objectMapper.writeValueAsString(roomCreateReqDto);
			// when && then
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isBadRequest());
		}
	}
}
