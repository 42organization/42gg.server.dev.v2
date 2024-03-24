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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.auth.utils.AuthTokenProvider;
import gg.data.party.Category;
import gg.data.party.PartyPenalty;
import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.user.room.controller.request.RoomCreateReqDto;
import gg.party.api.user.room.controller.response.LeaveRoomResDto;
import gg.party.api.user.room.controller.response.RoomCreateResDto;
import gg.party.api.user.room.controller.response.RoomDetailResDto;
import gg.party.api.user.room.controller.response.RoomJoinResDto;
import gg.party.api.user.room.controller.response.RoomListResDto;
import gg.party.api.user.room.controller.response.RoomResDto;
import gg.party.api.user.room.controller.response.UserRoomResDto;
import gg.party.api.user.room.service.RoomFindService;
import gg.party.api.user.room.service.RoomManagementService;
import gg.repo.party.CategoryRepository;
import gg.repo.party.PartyPenaltyRepository;
import gg.repo.party.RoomRepository;
import gg.repo.party.UserRoomRepository;
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
	UserRoomRepository userRoomRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	PartyPenaltyRepository partyPenaltyRepository;
	@Autowired
	RoomFindService roomFindService;
	@Autowired
	RoomManagementService roomManagementService;
	User userTester;
	User anotherTester;
	User otherTester;
	User reportedTester;
	String userAccessToken;
	String anotherAccessToken;
	String otherAccessToken;
	String reportedAccessToken;
	Category testCategory;
	Room openRoom;
	Room startRoom;
	Room finishRoom;
	Room hiddenRoom;
	Room failRoom;

	@Nested
	@DisplayName("방 전체 조회 테스트")
	class FindAllActiveRoomList {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("findTester", "findTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			Category testCategory = testDataUtils.createNewCategory("category");
			openRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.OPEN);
			startRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				3, 2, 180, RoomType.START);
			finishRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				3, 2, 180, RoomType.FINISH);
			hiddenRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.HIDDEN);
			failRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.FAIL);
		}

		/**
		 * 조건에 해당하는 방이 없을때 빈 리스트를 반환
		 * Penalty 상태의 사용자도 게시글 목록은 조회할 수 있어야하기에 유저를 나누지 않았음.
		 */
		@Test
		@DisplayName("조회 성공 200")
		public void success() throws Exception {
			String url = "/party/rooms";
			//given
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//when
			RoomListResDto resp = roomFindService.findRoomList();
			//then
			List<RoomResDto> roomList = resp.getRoomList();
			for (RoomResDto responseDto : roomList) {
				assertThat(responseDto.getStatus()).isIn(RoomType.OPEN.toString(), RoomType.START.toString());
			}
		}
	}

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
		@DisplayName("패널티 상태로 인한 생성 실패 403")
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

	@Nested
	@DisplayName("참여한 방 조회 테스트")
	class FindAllJoinedRoomListRoom {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("findTester", "findTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			Category testCategory = testDataUtils.createNewCategory("category");
			openRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.OPEN);
			startRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				3, 2, 180, RoomType.START);
			finishRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				3, 2, 180, RoomType.FINISH);
			hiddenRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.HIDDEN);
			failRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.FAIL);
		}

		/**
		 * 조건에 해당하는 방이 없을때 빈 리스트를 반환
		 * Penalty 상태의 사용자도 참여중인 방 목록은 조회할 수 있어야하기에 유저를 나누지 않았음.
		 * Penalty 상태의 유저는 시작하기 전인 방에서 나가지게 작성했으므로 이미 시작한 방에 한해서만 조회되어야함
		 */
		@Test
		@DisplayName("조회 성공 200")
		public void success() throws Exception {
			String url = "/party/rooms/joined";
			//given
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//when
			RoomListResDto resp = roomFindService.findJoinedRoomList(userTester.getId());
			//then
			List<RoomResDto> roomList = resp.getRoomList();
			for (RoomResDto responseDto : roomList) {
				assertThat(responseDto.getStatus()).isIn(RoomType.OPEN.toString(), RoomType.START.toString());
			}
		}
	}

	@Nested
	@DisplayName("참여했던 방 조회 테스트")
	class FindMyHistoryRoomList {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("findTester", "findTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			Category testCategory = testDataUtils.createNewCategory("category");
			openRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.OPEN);
			startRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				3, 2, 180, RoomType.START);
			finishRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				3, 2, 180, RoomType.FINISH);
			hiddenRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.HIDDEN);
			failRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.FAIL);
		}

		/**
		 * 조건에 해당하는 방이 없을때 빈 리스트를 반환
		 * Penalty 상태의 사용자도 참여했던 방 목록은 조회할 수 있어야하기에 유저를 나누지 않았음.
		 */
		@Test
		@DisplayName("조회 성공 200")
		public void success() throws Exception {
			String url = "/party/rooms/joined";
			//given
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//when
			RoomListResDto resp = roomFindService.findMyHistoryRoomList(userTester.getId());
			//then
			List<RoomResDto> roomList = resp.getRoomList();
			for (RoomResDto responseDto : roomList) {
				assertThat(responseDto.getStatus()).isIn(RoomType.FINISH.toString());
			}
		}
	}

	@Nested
	@DisplayName("방 상세 조회 테스트") // penalty 상태의 유저도 방을 상세 조회할 수는 있다.
	class FindRoomDetailInfo {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewImageUser("findTester", "findTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER, "image");
			anotherTester = testDataUtils.createNewImageUser("anotherTester", "anotherTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER, "image");
			userAccessToken = tokenProvider.createToken(userTester.getId());
			anotherAccessToken = tokenProvider.createToken(anotherTester.getId());
			Category testCategory = testDataUtils.createNewCategory("category");
			openRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.OPEN);
			UserRoom openUserRoom = testDataUtils.createNewUserRoom(userTester, openRoom, "nickname", true);
			startRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				3, 2, 180, RoomType.START);
			UserRoom startUserRoom = testDataUtils.createNewUserRoom(userTester, startRoom, "nickname", true);
			finishRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				3, 2, 180, RoomType.FINISH);
			UserRoom finishUserRoom = testDataUtils.createNewUserRoom(userTester, finishRoom, "nickname", true);
			hiddenRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.HIDDEN);
			UserRoom hiddenUserRoom = testDataUtils.createNewUserRoom(userTester, hiddenRoom, "nickname", true);
			failRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.FAIL);
			UserRoom failUserRoom = testDataUtils.createNewUserRoom(userTester, failRoom, "nickname", true);
		}

		@Test
		@DisplayName("OPEN 및 참여한 방 조회 성공 200")
		public void inOpenRoomSuccess() throws Exception {
			String openRoomId = openRoom.getId().toString();
			String url = "/party/rooms/" + openRoomId;
			//given
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//when
			RoomDetailResDto rdrd = roomFindService.findRoomDetail(userTester.getId(), openRoom.getId());
			//then
			assertThat(rdrd.getStatus().toString()).isEqualTo(RoomType.OPEN.toString());
			for (UserRoomResDto roomUser : rdrd.getRoomUsers()) {
				assertThat(roomUser.getIntraId()).isNull();
				assertThat(roomUser.getUserImage()).isNull();
			}
		}

		@Test
		@DisplayName("OPEN 및 참여하지 않은 방 조회 성공 200")
		public void outOpenRoomSuccess() throws Exception {
			String openRoomId = openRoom.getId().toString();
			String url = "/party/rooms/" + openRoomId;
			//given
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + anotherAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//when
			RoomDetailResDto rdrd = roomFindService.findRoomDetail(anotherTester.getId(), openRoom.getId());
			//then
			assertThat(rdrd.getStatus().toString()).isEqualTo(RoomType.OPEN.toString());
			for (UserRoomResDto roomUser : rdrd.getRoomUsers()) {
				assertThat(roomUser.getIntraId()).isNull();
				assertThat(roomUser.getUserImage()).isNull();
			}
		}

		@Test
		@DisplayName("START 및 참여한 방 조회 성공 200")
		public void inStartRoomSuccess() throws Exception {
			String startRoomId = startRoom.getId().toString();
			String url = "/party/rooms/" + startRoomId;
			//given
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//when
			RoomDetailResDto rdrd = roomFindService.findRoomDetail(userTester.getId(), startRoom.getId());
			//then
			assertThat(rdrd.getStatus().toString()).isEqualTo(RoomType.START.toString());
			for (UserRoomResDto roomUser : rdrd.getRoomUsers()) {
				assertThat(roomUser.getIntraId()).isNotNull();
				assertThat(roomUser.getUserImage()).isNotNull();
			}
		}

		@Test
		@DisplayName("START 및 참여하지 않은 방 조회 성공 200")
		public void outStartRoomSuccess() throws Exception {
			String startRoomId = startRoom.getId().toString();
			String url = "/party/rooms/" + startRoomId;
			//given
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + anotherAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//when
			RoomDetailResDto rdrd = roomFindService.findRoomDetail(anotherTester.getId(), startRoom.getId());
			//then
			assertThat(rdrd.getStatus().toString()).isEqualTo(RoomType.START.toString());
			for (UserRoomResDto roomUser : rdrd.getRoomUsers()) {
				assertThat(roomUser.getIntraId()).isNull();
				assertThat(roomUser.getUserImage()).isNull();
			}
		}

		@Test
		@DisplayName("HIDDEN된 방 조회 실패 404")
		public void hiddenRoomFail() throws Exception {
			// given
			String hiddenRoomId = hiddenRoom.getId().toString();
			String url = "/party/rooms/" + hiddenRoomId;

			// when && then
			ResultActions resultActions = mockMvc.perform(
				get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken));
			resultActions.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("방 참여 테스트") // slack api는 테스트 불가함으로 인해 차후 unit테스트로 추가 예정 (방 시작도 동일)
	class JoinRoom {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("userTester", "userTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			anotherTester = testDataUtils.createNewUser("anotherTester", "anotherTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			reportedTester = testDataUtils.createNewUser("reportedTester", "reportedTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			PartyPenalty testPenalty = testDataUtils.createNewPenalty(reportedTester, "test", "test",
				LocalDateTime.now(), 60);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			anotherAccessToken = tokenProvider.createToken(anotherTester.getId());
			reportedAccessToken = tokenProvider.createToken(reportedTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			openRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				7, 2, 180, RoomType.OPEN);
			UserRoom openUserRoom = testDataUtils.createNewUserRoom(userTester, openRoom, "nickname", true);
			startRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				7, 2, 180, RoomType.START);
			UserRoom startUserRoom = testDataUtils.createNewUserRoom(userTester, startRoom, "nickname", true);
		}

		@Test
		@DisplayName("참여 성공 201")
		public void success() throws Exception {
			// given
			String openRoomId = openRoom.getId().toString();
			String url = "/party/rooms/" + openRoomId;
			// when
			String contentAsString = mockMvc.perform(
					post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + anotherAccessToken))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
			// then
			assertThat(openRoom.getId().toString()).isEqualTo(openRoomId);
			Room updatedRoom = roomRepository.findById(openRoom.getId()).orElse(null);
			assertThat(updatedRoom).isNotNull();
			assertThat(updatedRoom.getCurrentPeople()).isEqualTo(2);
			RoomJoinResDto rjrd = objectMapper.readValue(contentAsString, RoomJoinResDto.class);
			assertThat(rjrd.getRoomId()).isEqualTo(openRoom.getId());
		}

		@Test
		@DisplayName("패널티 상태로 인한 참여 실패 403")
		public void penaltyUserFail() throws Exception {
			// given
			String openRoomId = openRoom.getId().toString();
			String url = "/party/rooms/" + openRoomId;
			// when && then
			String contentAsString = mockMvc.perform(
					post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + reportedAccessToken))
				.andExpect(status().isForbidden()).toString();
		}

		@Test
		@DisplayName("OPEN이 아닌 상태로 인한 실패 ")
		public void notOpenRoomFail() throws Exception {
			// given
			String startRoomId = startRoom.getId().toString();
			String url = "/party/rooms/" + startRoomId;
			// when && then
			String contentAsString = mockMvc.perform(
					post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + anotherAccessToken))
				.andExpect(status().isBadRequest()).toString();
		}

		@Test
		@DisplayName("이미 참여 중인 상태로 인한 실패 ")
		public void alreadyInRoomFail() throws Exception {
			// given
			String openRoomId = openRoom.getId().toString();
			String url = "/party/rooms/" + openRoomId;
			// when && then
			String contentAsString = mockMvc.perform(
					post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isConflict()).toString();
		}
	}

	@Nested
	@DisplayName("방 나가기 테스트")
	class ExitRoom {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("userTester", "userTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			anotherTester = testDataUtils.createNewUser("anotherTester", "anotherTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			otherTester = testDataUtils.createNewUser("otherTester", "otherTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			anotherAccessToken = tokenProvider.createToken(anotherTester.getId());
			otherAccessToken = tokenProvider.createToken(otherTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			openRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				7, 2, 180, RoomType.OPEN);
			UserRoom openUserRoom = testDataUtils.createNewUserRoom(userTester, openRoom, "nickname", true);
			UserRoom anotherUserRoom = testDataUtils.createNewUserRoom(anotherTester, openRoom, "nickname2", true);
			failRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				7, 2, 180, RoomType.OPEN);
			UserRoom willFailUserRoom = testDataUtils.createNewUserRoom(userTester, failRoom, "nickname", true);
			startRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				7, 2, 180, RoomType.START);
			UserRoom startUserRoom = testDataUtils.createNewUserRoom(userTester, startRoom, "nickname", true);
		}

		@Test
		@DisplayName("호스트 이전 성공 200")
		public void hostSuccess() throws Exception {
			// given
			String openRoomId = openRoom.getId().toString();
			String url = "/party/rooms/" + openRoomId;
			// when
			String contentAsString = mockMvc.perform(
					patch(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			// then
			Room updatedRoom = roomRepository.findById(openRoom.getId()).orElse(null);
			UserRoom newHostUserRoom = userRoomRepository.findByUserAndRoom(anotherTester, openRoom).orElse(null);
			UserRoom exitUserRoom = userRoomRepository.findByUserAndRoom(userTester, openRoom).orElse(null);
			assertThat(updatedRoom).isNotNull();
			assertThat(updatedRoom.getCurrentPeople()).isEqualTo(1);
			assertThat(newHostUserRoom).isNotNull();
			assertThat(newHostUserRoom.getUser()).isEqualTo(updatedRoom.getHost());
			assertThat(exitUserRoom).isNotNull();
			assertThat(exitUserRoom.getIsExist()).isFalse();
			LeaveRoomResDto lrrd = objectMapper.readValue(contentAsString, LeaveRoomResDto.class);
			assertThat(lrrd.getNickname()).isEqualTo(exitUserRoom.getNickname());
		}

		@Test
		@DisplayName("방 FAIL 성공 200")
		public void failSuccess() throws Exception {
			// given
			String willFailRoomId = failRoom.getId().toString();
			String url = "/party/rooms/" + willFailRoomId;
			// when
			String contentAsString = mockMvc.perform(
					patch(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			// then
			Room updatedRoom = roomRepository.findById(failRoom.getId()).orElse(null);
			UserRoom exitUserRoom = userRoomRepository.findByUserAndRoom(userTester, failRoom).orElse(null);
			assertThat(updatedRoom).isNotNull();
			assertThat(updatedRoom.getCurrentPeople()).isEqualTo(0);
			assertThat(updatedRoom.getStatus()).isEqualTo(RoomType.FAIL);
			assertThat(exitUserRoom).isNotNull();
			assertThat(exitUserRoom.getIsExist()).isFalse();
			LeaveRoomResDto lrrd = objectMapper.readValue(contentAsString, LeaveRoomResDto.class);
			assertThat(lrrd.getNickname()).isEqualTo(exitUserRoom.getNickname());
		}

		@Test
		@DisplayName("방에 없는 유저로 인한 나가기 실패 404")
		public void notInFail() throws Exception {
			// given
			String openRoomId = openRoom.getId().toString();
			String url = "/party/rooms/" + openRoomId;
			// when && then
			String contentAsString = mockMvc.perform(
					patch(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + otherAccessToken))
				.andExpect(status().isNotFound()).toString();
		}

		@Test
		@DisplayName("OPEN이 아닌 방으로 인한 나가기 실패 400")
		public void startFail() throws Exception {
			// given
			String startRoomId = startRoom.getId().toString();
			String url = "/party/rooms/" + startRoomId;
			// when && then
			String contentAsString = mockMvc.perform(
					patch(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isBadRequest()).toString();
		}
	}
}