package gg.party.api.user.room;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.auth.utils.AuthTokenProvider;
import gg.data.party.Category;
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
import gg.party.api.user.room.controller.response.RoomStartResDto;
import gg.party.api.user.room.controller.response.UserRoomResDto;
import gg.party.api.user.room.service.RoomManagementService;
import gg.pingpong.api.user.noti.service.PartyNotiService;
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
	RoomManagementService roomManagementService;
	@MockBean
	PartyNotiService partyNotiService;
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
	Room[] rooms = {openRoom, startRoom, finishRoom, hiddenRoom, failRoom};
	RoomType[] roomTypes = {RoomType.OPEN, RoomType.START, RoomType.FINISH, RoomType.HIDDEN, RoomType.FAIL};

	@Nested
	@DisplayName("방 전체 조회 테스트")
	class FindAllActiveRoomList {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("findTester", "findTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			for (int i = 0; i < rooms.length; i++) {
				rooms[i] = testDataUtils.createNewRoom(userTester, userTester, testCategory, i, 1, 3, 2, 180,
					roomTypes[i]);
			}
		}

		/**
		 * 조건에 해당하는 방이 없을때 빈 리스트를 반환
		 * Penalty 상태의 사용자도 게시글 목록은 조회할 수 있어야하기에 유저를 나누지 않았음.
		 */
		@Test
		@DisplayName("조회 성공 200")
		public void success() throws Exception {
			//given
			String url = "/party/rooms";
			//when
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			RoomListResDto rlrd = objectMapper.readValue(contentAsString, RoomListResDto.class);
			List<RoomResDto> roomList = rlrd.getRoomList();
			for (RoomResDto responseDto : roomList) {
				assertThat(responseDto.getStatus()).isIn(RoomType.OPEN.toString(), RoomType.START.toString(),
					RoomType.FINISH.toString());
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
			testDataUtils.createNewPenalty(reportedTester, "test", "test",
				LocalDateTime.now(), 60);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			reportedAccessToken = tokenProvider.createToken(reportedTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
		}

		@Test
		@DisplayName("생성 성공 201")
		public void success() throws Exception {
			//given
			String url = "/party/rooms";
			RoomCreateReqDto roomCreateReqDto = new RoomCreateReqDto("title", "content", testCategory.getName(),
				4, 2, 180);
			String jsonRequest = objectMapper.writeValueAsString(roomCreateReqDto);
			//when
			String contentAsString = mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
			//then
			RoomCreateResDto rcrd = objectMapper.readValue(contentAsString, RoomCreateResDto.class);
			assertThat(rcrd.getRoomId()).isNotNull();
		}

		@Test
		@DisplayName("패널티 상태로 인한 생성 실패 403")
		public void penaltyUserFail() throws Exception {
			//given
			String url = "/party/rooms";
			RoomCreateReqDto roomCreateReqDto = new RoomCreateReqDto("title", "content", testCategory.getName(), 4, 2,
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
			//given
			String url = "/party/rooms";
			RoomCreateReqDto roomCreateReqDto = new RoomCreateReqDto("title", "content", "NOTFOUND", 4, 2, 180);
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
			//given
			String url = "/party/rooms";
			RoomCreateReqDto roomCreateReqDto = new RoomCreateReqDto("title", "content", testCategory.getName(), 2, 4,
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
			//given
			String url = "/party/rooms";
			RoomCreateReqDto roomCreateReqDto = new RoomCreateReqDto("title", "content", testCategory.getName(), 4, 2,
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
			testCategory = testDataUtils.createNewCategory("category");
			for (int i = 0; i < rooms.length; i++) {
				rooms[i] = testDataUtils.createNewRoom(userTester, userTester, testCategory, i, 1, 3, 2, 180,
					roomTypes[i]);
			}
		}

		/**
		 * 조건에 해당하는 방이 없을때 빈 리스트를 반환
		 * Penalty 상태의 사용자도 참여중인 방 목록은 조회할 수 있어야하기에 유저를 나누지 않았음.
		 * Penalty 상태의 유저는 시작하기 전인 방에서 나가지게 작성했으므로 이미 시작한 방에 한해서만 조회되어야함
		 */
		@Test
		@DisplayName("조회 성공 200")
		public void success() throws Exception {
			//given
			String url = "/party/rooms/joined";
			//when
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			RoomListResDto resp = objectMapper.readValue(contentAsString, RoomListResDto.class);
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
			testCategory = testDataUtils.createNewCategory("category");
			for (int i = 0; i < rooms.length; i++) {
				rooms[i] = testDataUtils.createNewRoom(userTester, userTester, testCategory, i, 1, 3, 2, 180,
					roomTypes[i]);
			}
		}

		/**
		 * 조건에 해당하는 방이 없을때 빈 리스트를 반환
		 * Penalty 상태의 사용자도 참여했던 방 목록은 조회할 수 있어야하기에 유저를 나누지 않았음.
		 */
		@Test
		@DisplayName("조회 성공 200")
		public void success() throws Exception {
			//given
			String url = "/party/rooms/joined";
			//when
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			RoomListResDto resp = objectMapper.readValue(contentAsString, RoomListResDto.class);
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
			otherTester = testDataUtils.createNewImageUser("otherTester", "otherTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER, "image");
			userAccessToken = tokenProvider.createToken(userTester.getId());
			anotherAccessToken = tokenProvider.createToken(anotherTester.getId());
			otherAccessToken = tokenProvider.createToken(otherTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			for (int i = 0; i < rooms.length; i++) {
				rooms[i] = testDataUtils.createNewRoom(userTester, userTester, testCategory, i, 1, 3, 2, 180,
					roomTypes[i]);
				testDataUtils.createNewUserRoom(userTester, rooms[i], "nickname" + i, true);
				testDataUtils.createNewUserRoom(otherTester, rooms[i], "nickname" + i, true);
			}
		}

		@Test
		@DisplayName("OPEN 및 참여한 방 조회 성공 200")
		public void inOpenRoomSuccess() throws Exception {
			//given
			String roomId = rooms[0].getId().toString(); // openRoom
			String url = "/party/rooms/" + roomId;
			//when
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			RoomDetailResDto rdrd = objectMapper.readValue(contentAsString, RoomDetailResDto.class);
			assertThat(rdrd.getStatus().toString()).isEqualTo(RoomType.OPEN.toString());
			for (UserRoomResDto roomUser : rdrd.getRoomUsers()) {
				assertThat(roomUser.getIntraId()).isNull();
				assertThat(roomUser.getUserImage()).isNull();
			}
		}

		@Test
		@DisplayName("OPEN 및 참여하지 않은 방 조회 성공 200")
		public void outOpenRoomSuccess() throws Exception {
			//given
			String roomId = rooms[0].getId().toString(); // openRoom
			String url = "/party/rooms/" + roomId;
			//when
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + anotherAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			RoomDetailResDto rdrd = objectMapper.readValue(contentAsString, RoomDetailResDto.class);
			assertThat(rdrd.getStatus().toString()).isEqualTo(RoomType.OPEN.toString());
			for (UserRoomResDto roomUser : rdrd.getRoomUsers()) {
				assertThat(roomUser.getIntraId()).isNull();
				assertThat(roomUser.getUserImage()).isNull();
			}
		}

		@Test
		@DisplayName("START 및 참여한 방 조회 성공 200")
		public void inStartRoomSuccess() throws Exception {
			//given
			String roomId = rooms[1].getId().toString(); // startRoom
			String url = "/party/rooms/" + roomId;
			//when
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			RoomDetailResDto rdrd = objectMapper.readValue(contentAsString, RoomDetailResDto.class);
			assertThat(rdrd.getStatus().toString()).isEqualTo(RoomType.START.toString());
			for (UserRoomResDto roomUser : rdrd.getRoomUsers()) {
				assertThat(roomUser.getIntraId()).isNotNull();
				assertThat(roomUser.getUserImage()).isNotNull();
			}
		}

		@Test
		@DisplayName("START 및 참여하지 않은 방 조회 성공 200")
		public void outStartRoomSuccess() throws Exception {
			//given
			String roomId = rooms[1].getId().toString(); // startRoom
			String url = "/party/rooms/" + roomId;
			//when
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + anotherAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			RoomDetailResDto rdrd = objectMapper.readValue(contentAsString, RoomDetailResDto.class);
			assertThat(rdrd.getStatus().toString()).isEqualTo(RoomType.START.toString());
			for (UserRoomResDto roomUser : rdrd.getRoomUsers()) {
				assertThat(roomUser.getIntraId()).isNull();
				assertThat(roomUser.getUserImage()).isNull();
			}
		}

		@Test
		@DisplayName("START 및 참여한 방장이 아닌 방 조회 성공 200")
		public void otherStartRoomSuccess() throws Exception {
			//given
			String roomId = rooms[1].getId().toString(); // startRoom
			String url = "/party/rooms/" + roomId;
			//when
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + otherAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			RoomDetailResDto rdrd = objectMapper.readValue(contentAsString, RoomDetailResDto.class);
			assertThat(rdrd.getStatus().toString()).isEqualTo(RoomType.START.toString());
			for (UserRoomResDto roomUser : rdrd.getRoomUsers()) {
				assertThat(roomUser.getIntraId()).isNotNull();
				assertThat(roomUser.getUserImage()).isNotNull();
			}
		}

		@Test
		@DisplayName("Finish 및 참여한 방장이 아닌 방 조회 성공 200")
		public void otherFinishRoomSuccess() throws Exception {
			//given
			String roomId = rooms[2].getId().toString(); // finishRoom
			String url = "/party/rooms/" + roomId;
			//when
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + otherAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			RoomDetailResDto rdrd = objectMapper.readValue(contentAsString, RoomDetailResDto.class);
			assertThat(rdrd.getStatus().toString()).isEqualTo(RoomType.FINISH.toString());
			for (UserRoomResDto roomUser : rdrd.getRoomUsers()) {
				assertThat(roomUser.getIntraId()).isNotNull();
				assertThat(roomUser.getUserImage()).isNotNull();
			}
		}

		@Test
		@DisplayName("HIDDEN된 방 조회 실패 404")
		public void hiddenRoomFail() throws Exception {
			// given
			String roomId = rooms[3].getId().toString();
			String url = "/party/rooms/" + roomId;
			// when && then
			ResultActions resultActions = mockMvc.perform(
				get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken));
			resultActions.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("방 시작 테스트")
	class StartRoom {
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
			openRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 0, 2, 3, 2, 180,
				RoomType.OPEN);
			startRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2, 3, 2, 180,
				RoomType.START);
			testDataUtils.createNewUserRoom(userTester, openRoom, "nickname", true);
			testDataUtils.createNewUserRoom(otherTester, openRoom, "nickname2", true);
		}

		@Test
		@DisplayName("시작 성공 201")
		public void success() throws Exception {
			// given
			String roomId = openRoom.getId().toString();
			String url = "/party/rooms/" + roomId + "/start";
			// when
			String contentAsString = mockMvc.perform(
					post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
			// then
			RoomStartResDto rsrd = objectMapper.readValue(contentAsString, RoomStartResDto.class);
			assertThat(rsrd.getRoomId()).isEqualTo(openRoom.getId());
			then(partyNotiService).should(times(1)).sendPartyNotifications(any());
		}

		@Test
		@DisplayName("방 없음으로 인한 에러 404")
		public void noRoomFail() throws Exception {
			// given
			String roomId = "1000";
			String url = "/party/rooms/" + roomId + "/start";
			// when && then
			String contentAsString = mockMvc.perform(
					post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isNotFound()).toString();
		}

		@Test
		@DisplayName("Open이 아닌 상태로 인한 에러 400")
		public void notOpenFail() throws Exception {
			// given
			String roomId = startRoom.getId().toString();
			String url = "/party/rooms/" + roomId + "/start";
			// when && then
			String contentAsString = mockMvc.perform(
					post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isBadRequest()).toString();
		}

		@Test
		@DisplayName("인원부족으로 인한 에러 400")
		public void notNotEnoughPeopleFail() throws Exception {
			// given
			Room room = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1, 3, 2, 180,
				RoomType.OPEN);
			String roomId = room.getId().toString();
			String url = "/party/rooms/" + roomId + "/start";
			// when && then
			String contentAsString = mockMvc.perform(
					post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isBadRequest()).toString();
		}

		@Test
		@DisplayName("방에 참가하지 않음으로 인한 에러 400")
		public void notCurrentFail() throws Exception {
			// given
			String roomId = openRoom.getId().toString();
			String url = "/party/rooms/" + roomId + "/start";
			// when && then
			String contentAsString = mockMvc.perform(
					post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + anotherAccessToken))
				.andExpect(status().isBadRequest()).toString();
		}

		@Test
		@DisplayName("방장이 아님으로 인한 에러 403")
		public void notHostFail() throws Exception {
			// given
			String roomId = openRoom.getId().toString();
			String url = "/party/rooms/" + roomId + "/start";
			// when && then
			String contentAsString = mockMvc.perform(
					post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + otherAccessToken))
				.andExpect(status().isForbidden()).toString();
		}
	}

	@Nested
	@DisplayName("방 참여 테스트")
	class JoinRoom {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("userTester", "userTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			anotherTester = testDataUtils.createNewUser("anotherTester", "anotherTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			reportedTester = testDataUtils.createNewUser("reportedTester", "reportedTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			testDataUtils.createNewPenalty(reportedTester, "test", "test", LocalDateTime.now(), 60);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			anotherAccessToken = tokenProvider.createToken(anotherTester.getId());
			reportedAccessToken = tokenProvider.createToken(reportedTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			for (int i = 0; i < rooms.length; i++) {
				rooms[i] = testDataUtils.createNewRoom(userTester, userTester, testCategory, i, 1, 3, 2, 180,
					roomTypes[i]);
				testDataUtils.createNewUserRoom(userTester, rooms[i], "nickname" + i, true);
			}
			openRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 0, 1, 2, 2, 180,
				RoomType.OPEN);
			testDataUtils.createNewUserRoom(userTester, openRoom, "nickname", true);
		}

		@Test
		@DisplayName("참여 성공 201")
		public void success() throws Exception {
			// given
			String roomId = rooms[0].getId().toString(); // openRoom
			String url = "/party/rooms/" + roomId;
			// when
			String contentAsString = mockMvc.perform(
					post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + anotherAccessToken))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
			// then
			RoomJoinResDto rjrd = objectMapper.readValue(contentAsString, RoomJoinResDto.class);
			Room updatedRoom = roomRepository.findById(rooms[0].getId()).orElse(null);
			assertThat(updatedRoom).isNotNull();
			assertThat(updatedRoom.getCurrentPeople()).isEqualTo(2);
			assertThat(rjrd.getRoomId()).isEqualTo(rooms[0].getId());
		}

		@Test
		@DisplayName("참여로 인한 최대인원으로 시작 성공 201")
		public void startSuccess() throws Exception {
			// given
			String roomId = openRoom.getId().toString();
			String url = "/party/rooms/" + roomId;
			// when
			String contentAsString = mockMvc.perform(
					post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + anotherAccessToken))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
			// then
			RoomJoinResDto rjrd = objectMapper.readValue(contentAsString, RoomJoinResDto.class);
			Room updatedRoom = roomRepository.findById(openRoom.getId()).orElse(null);
			assertThat(updatedRoom).isNotNull();
			assertThat(updatedRoom.getCurrentPeople()).isEqualTo(2);
			assertThat(rjrd.getRoomId()).isEqualTo(openRoom.getId());
			then(partyNotiService).should(times(1)).sendPartyNotifications(any());
		}

		@Test
		@DisplayName("패널티 상태로 인한 참여 실패 403")
		public void penaltyUserFail() throws Exception {
			// given
			String roomId = rooms[0].getId().toString(); // openRoom
			String url = "/party/rooms/" + roomId;
			// when && then
			String contentAsString = mockMvc.perform(
					post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + reportedAccessToken))
				.andExpect(status().isForbidden()).toString();
		}

		@Test
		@DisplayName("OPEN이 아닌 상태로 인한 실패 400")
		public void notOpenRoomFail() throws Exception {
			for (int i = 1; i < rooms.length; i++) {
				//given
				String roomId = (rooms[i].getId()).toString();
				String url = "/party/rooms/" + roomId;
				// when && then
				String contentAsString = mockMvc.perform(
						post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + anotherAccessToken))
					.andExpect(status().isBadRequest()).toString();
			}
		}

		@Test
		@DisplayName("이미 참여 중인 상태로 인한 실패 409")
		public void alreadyInRoomFail() throws Exception {
			// given
			String roomId = rooms[0].getId().toString(); // openRoom
			String url = "/party/rooms/" + roomId;
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
			otherAccessToken = tokenProvider.createToken(otherTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			for (int i = 1; i < rooms.length; i++) {
				rooms[i] = testDataUtils.createNewRoom(userTester, userTester, testCategory, i, 1, 3, 2, 180,
					roomTypes[i]);
				testDataUtils.createNewUserRoom(userTester, rooms[i], "nickname" + i, true);
			}
			rooms[0] = testDataUtils.createNewRoom(userTester, userTester, testCategory, 0, 2, 3, 2, 180,
				roomTypes[0]);
			testDataUtils.createNewUserRoom(userTester, rooms[0], "nickname", true);
			testDataUtils.createNewUserRoom(anotherTester, rooms[0], "nickname", true);
			openRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 0, 1, 3, 2, 180,
				RoomType.OPEN);
			testDataUtils.createNewUserRoom(userTester, openRoom, "nickname", true);
		}

		@Test
		@DisplayName("호스트 이전 성공 200")
		public void hostSuccess() throws Exception {
			// given
			String roomId = rooms[0].getId().toString(); // openRoom
			String url = "/party/rooms/" + roomId;
			// when
			String contentAsString = mockMvc.perform(
					patch(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			// then
			Room updatedRoom = roomRepository.findById(rooms[0].getId()).orElse(null);
			UserRoom newHostUserRoom = userRoomRepository.findByUserAndRoom(anotherTester, rooms[0]).orElse(null);
			UserRoom exitUserRoom = userRoomRepository.findByUserAndRoom(userTester, rooms[0]).orElse(null);
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
			String roomId = openRoom.getId().toString(); // openRoom
			String url = "/party/rooms/" + roomId;
			// when
			String contentAsString = mockMvc.perform(
					patch(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			// then
			LeaveRoomResDto lrrd = objectMapper.readValue(contentAsString, LeaveRoomResDto.class);
			Room updatedRoom = roomRepository.findById(openRoom.getId()).orElse(null);
			UserRoom exitUserRoom = userRoomRepository.findByUserAndRoom(userTester, openRoom).orElse(null);
			assertThat(updatedRoom).isNotNull();
			assertThat(updatedRoom.getCurrentPeople()).isEqualTo(0);
			assertThat(updatedRoom.getStatus()).isEqualTo(RoomType.FAIL);
			assertThat(exitUserRoom).isNotNull();
			assertThat(exitUserRoom.getIsExist()).isFalse();
			assertThat(lrrd.getNickname()).isEqualTo(exitUserRoom.getNickname());
		}

		@Test
		@DisplayName("방에 없는 유저로 인한 나가기 실패 404")
		public void notInFail() throws Exception {
			// given
			String roomId = rooms[0].getId().toString(); // openRoom
			String url = "/party/rooms/" + roomId;
			// when && then
			String contentAsString = mockMvc.perform(
					patch(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + otherAccessToken))
				.andExpect(status().isBadRequest()).toString();
		}

		@Test
		@DisplayName("OPEN이 아닌 방으로 인한 나가기 실패 400")
		public void startFail() throws Exception {
			// given
			String startRoomId = rooms[1].getId().toString(); // startRoom
			String url = "/party/rooms/" + startRoomId;
			// when && then
			String contentAsString = mockMvc.perform(
					patch(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isBadRequest()).toString();
		}
	}
}
