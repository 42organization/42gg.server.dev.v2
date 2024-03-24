package gg.party.api.admin.room;

import static java.lang.Boolean.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import gg.party.api.admin.room.controller.response.AdminCommentResDto;
import gg.party.api.admin.room.controller.response.AdminRoomDetailResDto;
import gg.party.api.admin.room.controller.response.AdminRoomListResDto;
import gg.party.api.user.room.controller.response.UserRoomResDto;
import gg.party.api.user.room.service.RoomManagementService;
import gg.repo.party.CategoryRepository;
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
public class RoomAdminControllerTest {
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
	RoomManagementService roomManagementService;
	User userTester, adminTester, reportedTester;
	String userAccessToken, adminAccessToken, reportedAccessToken;
	Category testCategory;
	Room openRoom, startRoom, finishRoom, hiddenRoom, failRoom;
	Room openReportedRoom, startReportedRoom, finishReportedRoom, hiddenReportedRoom, failReportedRoom;

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
			adminTester = testDataUtils.createNewUser("adminTester", "adminTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
			adminAccessToken = tokenProvider.createToken(adminTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
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
			openReportedRoom = testDataUtils.createNewRoom(reportedTester, reportedTester, testCategory, 1, 1,
				3, 2, 180, RoomType.OPEN);
			startReportedRoom = testDataUtils.createNewRoom(reportedTester, reportedTester, testCategory, 1, 2,
				3, 2, 180, RoomType.START);
			finishReportedRoom = testDataUtils.createNewRoom(reportedTester, reportedTester, testCategory, 1, 2,
				3, 2, 180, RoomType.FINISH);
			hiddenReportedRoom = testDataUtils.createNewRoom(reportedTester, reportedTester, testCategory, 1, 1,
				3, 2, 180, RoomType.HIDDEN);
			failReportedRoom = testDataUtils.createNewRoom(reportedTester, reportedTester, testCategory, 1, 1,
				3, 2, 180, RoomType.FAIL);
		}

		@Test
		@DisplayName("첫 페이지 조회 성공 200")
		public void startPageSuccess() throws Exception {
			//given
			String currentPage = "1";
			String pageSize = "10";
			String url = "/party/admin/rooms" + currentPage + "&size=" + pageSize;
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
			String url = "/party/admin/rooms" + currentPage + "&size=" + pageSize;
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
	}

	@Nested
	@DisplayName("방 상세 조회 테스트")
	class adminRoomDetailInfo {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("findTester", "findTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			reportedTester = testDataUtils.createNewUser("reportedTester", "reportedTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			reportedAccessToken = tokenProvider.createToken(reportedTester.getId());
			adminTester = testDataUtils.createNewUser("adminTester", "adminTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
			adminAccessToken = tokenProvider.createToken(adminTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			openRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.OPEN);
			UserRoom testUserRoom = testDataUtils.createNewUserRoom(userTester, openRoom, "testNickname", TRUE);
			UserRoom reportUserRoom = testDataUtils.createNewUserRoom(reportedTester, openRoom, "reportNickname",
				FALSE);
			testDataUtils.createComment(userTester, testUserRoom, openRoom, "testComment");
			testDataUtils.createComment(reportedTester, reportUserRoom, openRoom, "reportComment");
			startRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				3, 2, 180, RoomType.START);
			UserRoom testUserRoom2 = testDataUtils.createNewUserRoom(userTester, startRoom, "testNickname", TRUE);
			UserRoom reportUserRoom2 = testDataUtils.createNewUserRoom(reportedTester, startRoom, "reportNickname",
				FALSE);
			testDataUtils.createComment(userTester, testUserRoom2, startRoom, "testComment2");
			testDataUtils.createComment(reportedTester, reportUserRoom2, startRoom, "reportComment2");
			finishRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				3, 2, 180, RoomType.FINISH);
			UserRoom testUserRoom3 = testDataUtils.createNewUserRoom(userTester, finishRoom, "testNickname", TRUE);
			UserRoom reportUserRoom3 = testDataUtils.createNewUserRoom(reportedTester, finishRoom, "reportNickname",
				FALSE);
			testDataUtils.createComment(userTester, testUserRoom3, finishRoom, "testComment3");
			testDataUtils.createComment(reportedTester, reportUserRoom3, finishRoom, "reportComment3");
			hiddenRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.HIDDEN);
			UserRoom testUserRoom4 = testDataUtils.createNewUserRoom(userTester, hiddenRoom, "testNickname", TRUE);
			UserRoom reportUserRoom4 = testDataUtils.createNewUserRoom(reportedTester, hiddenRoom, "reportNickname",
				FALSE);
			testDataUtils.createComment(userTester, testUserRoom4, hiddenRoom, "testComment4");
			testDataUtils.createComment(reportedTester, reportUserRoom4, hiddenRoom, "reportComment4");
			failRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.FAIL);
			UserRoom testUserRoom5 = testDataUtils.createNewUserRoom(userTester, failRoom, "testNickname", TRUE);
			UserRoom reportUserRoom5 = testDataUtils.createNewUserRoom(reportedTester, failRoom, "reportNickname",
				FALSE);
			testDataUtils.createComment(userTester, testUserRoom5, failRoom, "testComment5");
			testDataUtils.createComment(reportedTester, reportUserRoom5, failRoom, "reportComment5");
			openReportedRoom = testDataUtils.createNewRoom(reportedTester, reportedTester, testCategory, 1, 1,
				3, 2, 180, RoomType.OPEN);
			UserRoom testUserRoom6 = testDataUtils.createNewUserRoom(reportedTester, openReportedRoom, "testNickname",
				TRUE);
			UserRoom reportUserRoom6 = testDataUtils.createNewUserRoom(reportedTester, openReportedRoom,
				"reportNickname", FALSE);
			testDataUtils.createComment(userTester, testUserRoom6, openReportedRoom, "testComment6");
			testDataUtils.createComment(reportedTester, reportUserRoom6, openReportedRoom, "reportComment6");
			startReportedRoom = testDataUtils.createNewRoom(reportedTester, reportedTester, testCategory, 1, 2,
				3, 2, 180, RoomType.START);
			UserRoom testUserRoom7 = testDataUtils.createNewUserRoom(reportedTester, startReportedRoom, "testNickname",
				TRUE);
			UserRoom reportUserRoom7 = testDataUtils.createNewUserRoom(reportedTester, startReportedRoom,
				"reportNickname", FALSE);
			testDataUtils.createComment(userTester, testUserRoom7, startReportedRoom, "testComment7");
			testDataUtils.createComment(reportedTester, reportUserRoom7, startReportedRoom, "reportComment7");
			finishReportedRoom = testDataUtils.createNewRoom(reportedTester, reportedTester, testCategory, 1, 2,
				3, 2, 180, RoomType.FINISH);
			UserRoom testUserRoom8 = testDataUtils.createNewUserRoom(reportedTester, finishReportedRoom, "testNickname",
				TRUE);
			UserRoom reportUserRoom8 = testDataUtils.createNewUserRoom(reportedTester, finishReportedRoom,
				"reportNickname", FALSE);
			testDataUtils.createComment(userTester, testUserRoom8, finishReportedRoom, "testComment8");
			testDataUtils.createComment(reportedTester, reportUserRoom8, finishReportedRoom, "reportComment8");
			hiddenReportedRoom = testDataUtils.createNewRoom(reportedTester, reportedTester, testCategory, 1, 1,
				3, 2, 180, RoomType.HIDDEN);
			UserRoom testUserRoom9 = testDataUtils.createNewUserRoom(reportedTester, hiddenReportedRoom, "testNickname",
				TRUE);
			UserRoom reportUserRoom9 = testDataUtils.createNewUserRoom(reportedTester, hiddenReportedRoom,
				"reportNickname", FALSE);
			testDataUtils.createComment(userTester, testUserRoom9, hiddenReportedRoom, "testComment9");
			testDataUtils.createComment(reportedTester, reportUserRoom9, hiddenReportedRoom, "reportComment9");
			failReportedRoom = testDataUtils.createNewRoom(reportedTester, reportedTester, testCategory, 1, 1,
				3, 2, 180, RoomType.FAIL);
			UserRoom testUserRoom10 = testDataUtils.createNewUserRoom(reportedTester, failReportedRoom, "testNickname",
				TRUE);
			UserRoom reportUserRoom10 = testDataUtils.createNewUserRoom(reportedTester, failReportedRoom,
				"reportNickname", FALSE);
			testDataUtils.createComment(userTester, testUserRoom10, failReportedRoom, "testComment10");
			testDataUtils.createComment(reportedTester, reportUserRoom10, failReportedRoom, "reportComment10");
		}

		@Test
		@DisplayName("모든 종류에 대한 상세정보 조회 성공 200")
		public void success() throws Exception {
			Room[] rooms = {openRoom, startRoom, finishRoom, hiddenRoom, failRoom, openReportedRoom, startReportedRoom,
				finishReportedRoom, hiddenReportedRoom, failReportedRoom};
			for (int i = 0; i < rooms.length; i++) {
				//given
				String roomId = (openRoom.getId()).toString();
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
		@DisplayName("없는 방으로 인한 에러 404")
		public void lastPageSuccess() throws Exception {
			//given
			String roomId = "1000";
			String url = "/party/admin/rooms/" + roomId;
			//when
			String contentAsString = mockMvc.perform(get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken))
				.andExpect(status().isNotFound()).toString();
		}
	}
}
