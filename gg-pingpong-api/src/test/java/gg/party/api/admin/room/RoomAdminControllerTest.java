package gg.party.api.admin.room;

import static java.lang.Boolean.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

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
import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.admin.room.controller.request.RoomShowChangeReqDto;
import gg.party.api.admin.room.controller.response.AdminCommentResDto;
import gg.party.api.admin.room.controller.response.AdminRoomDetailResDto;
import gg.party.api.admin.room.controller.response.AdminRoomListResDto;
import gg.party.api.user.room.controller.response.UserRoomResDto;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class RoomAdminControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AuthTokenProvider tokenProvider;
	User userTester;
	User adminTester;
	User reportedTester;
	String userAccessToken;
	String adminAccessToken;
	String reportedAccessToken;
	Category testCategory;
	Room openRoom;
	Room startRoom;
	Room finishRoom;
	Room hiddenRoom;
	Room failRoom;
	Room pageTestRoom;
	Room openReportedRoom;
	Room startReportedRoom;
	Room finishReportedRoom;
	Room hiddenReportedRoom;
	Room failReportedRoom;
	Room[] rooms = {openRoom, startRoom, finishRoom, hiddenRoom, failRoom, openReportedRoom, startReportedRoom,
		finishReportedRoom, hiddenReportedRoom, failReportedRoom, pageTestRoom};
	RoomType[] roomTypes = {RoomType.OPEN, RoomType.START, RoomType.FINISH, RoomType.HIDDEN, RoomType.FAIL,
		RoomType.OPEN, RoomType.START, RoomType.FINISH, RoomType.HIDDEN, RoomType.FAIL, RoomType.OPEN};

	@Nested
	@DisplayName("Admin 방 Status 변경 테스트")
	class ModifyRoomVisibility {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("findTester", "findTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			adminTester = testDataUtils.createNewUser("adminTester", "adminTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
			adminAccessToken = tokenProvider.createToken(adminTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			openRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1, 3, 2, 180,
				RoomType.OPEN);
		}

		@Test
		@DisplayName("방 Status 변경 성공 204")
		public void success() throws Exception {
			//given
			String roomId = openRoom.getId().toString();
			String url = "/party/admin/rooms/" + roomId;
			RoomShowChangeReqDto roomShowChangeReqDto = new RoomShowChangeReqDto(RoomType.START.toString());
			String jsonRequest = objectMapper.writeValueAsString(roomShowChangeReqDto);
			//when
			mockMvc.perform(patch(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
				.andExpect(status().isNoContent())
				.andReturn().getResponse();
			//then
			assertThat(openRoom.getStatus()).isEqualTo(RoomType.START);
		}

		@Test
		@DisplayName("방이 없음으로 인한 에러 404")
		public void noRoomFail() throws Exception {
			//given
			String roomId = "1000";
			String url = "/party/admin/rooms/" + roomId;
			RoomShowChangeReqDto roomShowChangeReqDto = new RoomShowChangeReqDto(RoomType.START.toString());
			String jsonRequest = objectMapper.writeValueAsString(roomShowChangeReqDto);
			//when && then
			mockMvc.perform(patch(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("동일한 Status 변경으로 인한 에러 409")
		public void sameStatusFail() throws Exception {
			//given
			String roomId = openRoom.getId().toString();
			String url = "/party/admin/rooms/" + roomId;
			RoomShowChangeReqDto roomShowChangeReqDto = new RoomShowChangeReqDto(RoomType.OPEN.toString());
			String jsonRequest = objectMapper.writeValueAsString(roomShowChangeReqDto);
			//when && then
			mockMvc.perform(patch(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
				.andExpect(status().isConflict());
		}
	}

	@Nested
	@DisplayName("Admin 방 전체 조회 테스트")
	class FindAllActiveRoomList {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("findTester", "findTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			reportedTester = testDataUtils.createNewUser("reportedTester", "reportedTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			reportedAccessToken = tokenProvider.createToken(reportedTester.getId());
			testDataUtils.createNewPenalty(reportedTester, "test", "test",
				LocalDateTime.now(), 60);
			adminTester = testDataUtils.createNewUser("adminTester", "adminTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
			adminAccessToken = tokenProvider.createToken(adminTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			for (int i = 0; i < rooms.length; i++) {
				if (i < 5) {
					rooms[i] = testDataUtils.createNewRoom(userTester, userTester, testCategory, i, 1, 3, 2, 180,
						roomTypes[i]);
				} else {
					rooms[i] = testDataUtils.createNewRoom(reportedTester, reportedTester, testCategory, i, 1, 3, 2,
						180, roomTypes[i]);
				}
			}
		}

		@Test
		@DisplayName("첫 페이지 조회 성공 200")
		public void startPageSuccess() throws Exception {
			//given
			String currentPage = "1";
			String pageSize = "10";
			String url = "/party/admin/rooms?page=" + currentPage + "&size=" + pageSize;
			//when
			String contentAsString = mockMvc.perform(get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			AdminRoomListResDto arlrd = objectMapper.readValue(contentAsString, AdminRoomListResDto.class);
			assertThat(arlrd.getAdminRoomList().size()).isEqualTo(10);
		}

		@Test
		@DisplayName("마지막 페이지 조회 성공 200")
		public void lastPageSuccess() throws Exception {
			//given
			String currentPage = "2";
			String pageSize = "10";
			String url = "/party/admin/rooms?page=" + currentPage + "&size=" + pageSize;
			//when
			String contentAsString = mockMvc.perform(get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			AdminRoomListResDto arlrd = objectMapper.readValue(contentAsString, AdminRoomListResDto.class);
			assertThat(arlrd.getAdminRoomList().size()).isEqualTo(1);
		}
	}

	@Nested
	@DisplayName("방 상세 조회 테스트")
	class AdminRoomDetailInfo {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewImageUser("findTester", "findTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER, "userImage");
			userAccessToken = tokenProvider.createToken(userTester.getId());
			reportedTester = testDataUtils.createNewImageUser("reportedTester", "reportedTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER, "reportedImage");
			reportedAccessToken = tokenProvider.createToken(reportedTester.getId());
			testDataUtils.createNewPenalty(reportedTester, "test", "test",
				LocalDateTime.now(), 60);
			adminTester = testDataUtils.createNewImageUser("adminTester", "adminTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN, "adminImage");
			adminAccessToken = tokenProvider.createToken(adminTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			for (int i = 0; i < rooms.length - 1; i++) {
				if (i < 5) {
					rooms[i] = testDataUtils.createNewRoom(userTester, userTester, testCategory, i, 1, 3, 2, 180,
						roomTypes[i]);
				} else {
					rooms[i] = testDataUtils.createNewRoom(reportedTester, reportedTester, testCategory, i, 1, 3, 2,
						180, roomTypes[i]);
				}
				UserRoom testUserRoom = testDataUtils.createNewUserRoom(userTester, rooms[i], "testNickname", TRUE);
				UserRoom reportUserRoom = testDataUtils.createNewUserRoom(reportedTester, rooms[i],
					"reportNickname", TRUE);
				testDataUtils.createComment(userTester, testUserRoom, rooms[i], "testComment" + i);
				testDataUtils.createReportComment(reportedTester, reportUserRoom, rooms[i], "reportComment" + i);

			}
		}

		@Test
		@DisplayName("모든 종류에 대한 상세정보 조회 성공 200")
		public void success() throws Exception {
			for (int i = 0; i < rooms.length - 1; i++) { // pageTestRoom 제외
				//given
				String roomId = (rooms[i].getId()).toString();
				String url = "/party/admin/rooms/" + roomId;
				//when
				String contentAsString = mockMvc.perform(get(url)
						.contentType(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
					.andExpect(status().isOk())
					.andReturn().getResponse().getContentAsString();
				//then
				AdminRoomDetailResDto ardrd = objectMapper.readValue(contentAsString, AdminRoomDetailResDto.class);
				for (UserRoomResDto roomUser : ardrd.getRoomUsers()) {
					assertThat(roomUser.getIntraId()).isNotNull();
					assertThat(roomUser.getUserImage()).isNotNull();
				}
				for (AdminCommentResDto comment : ardrd.getComments()) {
					assertThat(comment.getIntraId()).isNotNull();
				}
			}
		}

		@Test
		@DisplayName("방에 아무도 없어서 FAIL 된 방 상세정보 조회 성공 200")
		public void failRoomSuccess() throws Exception {
			//given
			Room failToNoUserRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 0, 3, 2,
				180,
				RoomType.FAIL);
			testDataUtils.createNewUserRoom(userTester, failToNoUserRoom, "testNickname", FALSE);
			String roomId = failToNoUserRoom.getId().toString();
			String url = "/party/admin/rooms/" + roomId;
			//when
			String contentAsString = mockMvc.perform(get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			AdminRoomDetailResDto ardrd = objectMapper.readValue(contentAsString, AdminRoomDetailResDto.class);
			for (UserRoomResDto roomUser : ardrd.getRoomUsers()) {
				assertThat(roomUser.getIntraId()).isNotNull();
				assertThat(roomUser.getUserImage()).isNotNull();
			}
			for (AdminCommentResDto comment : ardrd.getComments()) {
				assertThat(comment.getIntraId()).isNotNull();
			}
		}

		@Test
		@DisplayName("없는 방으로 인한 에러 404")
		public void lastPageSuccess() throws Exception {
			//given
			String roomId = "1000";
			String url = "/party/admin/rooms/" + roomId;
			//when
			mockMvc.perform(get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
				.andExpect(status().isNotFound());
		}
	}
}
