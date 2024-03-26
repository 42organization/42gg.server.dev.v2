package gg.party.api.user.report;

import static java.lang.Boolean.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.auth.utils.AuthTokenProvider;
import gg.data.party.Category;
import gg.data.party.Comment;
import gg.data.party.CommentReport;
import gg.data.party.Room;
import gg.data.party.RoomReport;
import gg.data.party.UserReport;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.user.report.controller.request.ReportReqDto;
import gg.party.api.user.room.service.RoomFindService;
import gg.party.api.user.room.service.RoomManagementService;
import gg.repo.party.CategoryRepository;
import gg.repo.party.CommentReportRepository;
import gg.repo.party.PartyPenaltyRepository;
import gg.repo.party.RoomReportRepository;
import gg.repo.party.RoomRepository;
import gg.repo.party.UserReportRepository;
import gg.repo.party.UserRoomRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@IntegrationTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
@Slf4j
@Rollback(value = true)
public class ReportControllerTest {
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
	RoomReportRepository roomReportRepository;
	@Autowired
	CommentReportRepository commentReportRepository;
	@Autowired
	UserRoomRepository userRoomRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	PartyPenaltyRepository partyPenaltyRepository;
	@Autowired
	UserReportRepository userReportRepository;
	@Autowired
	RoomFindService roomFindService;
	@Autowired
	RoomManagementService roomManagementService;
	User userTester;
	User reporterTester;
	User otherTester;
	String userAccessToken;
	String reporterAccessToken;
	String otherAccessToken;
	Category testCategory;
	Comment comment;
	Room openRoom;
	Room startRoom;
	Room finishRoom;
	Room hiddenRoom;
	Room failRoom;
	Room[] rooms = {openRoom, startRoom, finishRoom, hiddenRoom, failRoom};
	RoomType[] roomTypes = {RoomType.OPEN, RoomType.START, RoomType.FINISH, RoomType.HIDDEN, RoomType.FAIL};

	@Nested
	@DisplayName("방 신고 테스트")
	class CreateRoomReport {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("userTester", "userTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			reporterTester = testDataUtils.createNewUser("reporterTester", "reporterTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			reporterAccessToken = tokenProvider.createToken(reporterTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			openRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				7, 2, 180, RoomType.OPEN);
			testDataUtils.createNewUserRoom(userTester, openRoom, "nickname", true);
		}

		@Test
		@Transactional
		@DisplayName("방 신고 성공 CREATED-201")
		public void reportRoomAddValidInputs() throws Exception {
			//given
			String url = "/party/reports/rooms/" + openRoom.getId();
			ReportReqDto reportReqDto = new ReportReqDto("report");
			//when
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reportReqDto))
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + reporterAccessToken))
				.andExpect(status().isCreated());
			//then
			RoomReport roomReport = roomReportRepository.findByRoomId(openRoom.getId()).get(0);
			assertThat(roomReport.getRoom().getId()).isEqualTo(openRoom.getId());
			assertThat(roomReport.getReporter().getId()).isEqualTo(reporterTester.getId());
			assertThat(roomReport.getMessage()).isEqualTo("report");
		}

		@Test
		@Transactional
		@DisplayName("중복 신고로 인한 신고 실패 409")
		public void reportRoomDuplicateFail() throws Exception {
			//given
			String url = "/party/reports/rooms/" + openRoom.getId();
			ReportReqDto reportReqDto = new ReportReqDto("test report");
			String jsonRequest = objectMapper.writeValueAsString(reportReqDto);
			//when & then
			// 첫 번째 신고 요청
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + reporterAccessToken))
				.andExpect(status().isCreated());

			// 중복 신고 요청
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + reporterAccessToken))
				.andExpect(status().isConflict());
		}

		@Test
		@Transactional
		@DisplayName("자진 신고로 인한 신고 실패 409")
		public void reportRoomSelfFail() throws Exception {
			//given
			String url = "/party/reports/rooms/" + openRoom.getId();
			ReportReqDto reportReqDto = new ReportReqDto("test report");
			String jsonRequest = objectMapper.writeValueAsString(reportReqDto);
			//when & then
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isBadRequest());
		}

		@Test
		@Transactional
		@DisplayName("방 없음 실패 404")
		public void reportRoomNotFoundFail() throws Exception {
			//given
			String openRoomId = "999";
			String url = "/party/reports/rooms/" + openRoomId;
			ReportReqDto reportReqDto = new ReportReqDto("test report");
			String jsonRequest = objectMapper.writeValueAsString(reportReqDto);
			//when & then
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + reporterAccessToken))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("댓글 신고 테스트")
	class CreateCommentReport {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("userTester", "userTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			reporterTester = testDataUtils.createNewUser("reporterTester", "reporterTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			reporterAccessToken = tokenProvider.createToken(reporterTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			openRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				7, 2, 180, RoomType.OPEN);
			UserRoom openUserRoom = testDataUtils.createNewUserRoom(userTester, openRoom, "nickname", true);
			comment = testDataUtils.createComment(userTester, openUserRoom, openRoom, "test_comment");
		}

		@Test
		@Transactional
		@DisplayName("댓글 신고 성공 CREATED-201")
		public void reportCommentAddValidInputs() throws Exception {
			//given
			String url = "/party/reports/comments/" + comment.getId();
			ReportReqDto reportReqDto = new ReportReqDto("report");
			//when
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reportReqDto))
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + reporterAccessToken))
				.andExpect(status().isCreated());
			//then
			CommentReport commentReport = commentReportRepository.findByCommentId(comment.getId()).get(0);
			assertThat(commentReport.getComment().getId()).isEqualTo(comment.getId());
			assertThat(commentReport.getReporter().getId()).isEqualTo(reporterTester.getId());
			assertThat(commentReport.getMessage()).isEqualTo("report");
		}

		@Test
		@Transactional
		@DisplayName("중복 신고로 인한 신고 실패 409")
		public void reportCommentDuplicateFail() throws Exception {
			//given
			String url = "/party/reports/comments/" + comment.getId();
			String jsonRequest = objectMapper.writeValueAsString(new ReportReqDto("report"));
			//when & then
			// 첫 번째 신고 요청
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + reporterAccessToken))
				.andExpect(status().isCreated());

			// 중복 신고 요청
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + reporterAccessToken))
				.andExpect(status().isConflict());
		}

		@Test
		@Transactional
		@DisplayName("자진 신고로 인한 신고 실패 409")
		public void reportCommentSelfFail() throws Exception {
			//given
			String url = "/party/reports/comments/" + comment.getId();
			String jsonRequest = objectMapper.writeValueAsString(new ReportReqDto("report"));
			//when & then
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isBadRequest());
		}

		@Test
		@Transactional
		@DisplayName("댓글 없음 실패 404")
		public void reportCommentNotFoundFail() throws Exception {
			//given
			String commentId = "999";
			String url = "/party/reports/comments/" + commentId;
			String jsonRequest = objectMapper.writeValueAsString(new ReportReqDto("report"));
			//when & then
			mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + reporterAccessToken))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("유저 신고 테스트")
	class CreateUserReport {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("userTester", "userTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			reporterTester = testDataUtils.createNewUser("reporterTester", "reporterTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			reporterAccessToken = tokenProvider.createToken(reporterTester.getId());
			otherTester = testDataUtils.createNewUser("otherTester", "otherTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			otherAccessToken = tokenProvider.createToken(otherTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			for (int i = 0; i < rooms.length - 1; i++) {
				rooms[i] = testDataUtils.createNewRoom(userTester, userTester, testCategory, i, 1, 3, 2, 180,
					roomTypes[i]);
				testDataUtils.createNewUserRoom(userTester, rooms[i], "testNickname", TRUE);
				testDataUtils.createNewUserRoom(reporterTester, rooms[i],
					"reportNickname", TRUE);
			}
		}

		@Test
		@Transactional
		@DisplayName("유저 신고 성공 201")
		public void reportUserAddValidInputs() throws Exception {
			for (int i = 1; i < rooms.length - 2; i++) { // open, hidden, fail 제외
				//given
				String url = "/party/reports/rooms/" + rooms[i].getId() + "/users/" + userTester.getIntraId();
				String jsonRequest = objectMapper.writeValueAsString(new ReportReqDto("test report"));
				//when
				mockMvc.perform(post(url)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonRequest)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + reporterAccessToken))
					.andExpect(status().isCreated())
					.andReturn().getResponse().getContentAsString();

				//then
				Optional<UserReport> roomReports = userReportRepository.findByReporterAndReporteeAndRoom(
					reporterTester,
					userTester, rooms[i]);
				if (roomReports.isPresent()) {
					assertThat(roomReports.get().getReporter().getId()).isEqualTo(reporterTester.getId());
					assertThat(roomReports.get().getReportee().getId()).isEqualTo(userTester.getId());
					assertThat(roomReports.get().getMessage()).isEqualTo("test report");
				}
			}
		}

		@Test
		@Transactional
		@DisplayName("중복 유저 신고로 인한 신고 실패 409")
		public void reportUserDuplicateFail() throws Exception {
			for (int i = 1; i < rooms.length - 2; i++) { // open, hidden, fail 제외
				//given
				String url = "/party/reports/rooms/" + rooms[i].getId() + "/users/" + userTester.getIntraId();
				String jsonRequest = objectMapper.writeValueAsString(new ReportReqDto("test report"));
				//when & then
				// 첫 번째 신고 요청
				mockMvc.perform(post(url)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonRequest)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + reporterAccessToken))
					.andExpect(status().isCreated());

				// 중복 신고 요청
				mockMvc.perform(post(url)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonRequest)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + reporterAccessToken))
					.andExpect(status().isConflict());
			}
		}

		@Test
		@Transactional
		@DisplayName("자진 신고로 인한 신고 실패 409")
		public void reportCommentSelfFail() throws Exception {
			for (int i = 1; i < rooms.length - 2; i++) { // open, hidden, fail 제외
				//given
				String url = "/party/reports/rooms/" + rooms[i].getId() + "/users/" + userTester.getIntraId();
				String jsonRequest = objectMapper.writeValueAsString(new ReportReqDto("report"));
				//when & then
				mockMvc.perform(post(url)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonRequest)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
					.andExpect(status().isBadRequest());
			}
		}

		@Test
		@Transactional
		@DisplayName("유저 없음 실패 404")
		public void reportCommentNotFoundFail() throws Exception {
			for (int i = 1; i < rooms.length - 2; i++) { // open, hidden, fail 제외
				//given
				String intraId = "9999";
				String url = "/party/reports/rooms/" + rooms[i].getId() + "/users/" + intraId;
				String jsonRequest = objectMapper.writeValueAsString(new ReportReqDto("report"));
				//when & then
				mockMvc.perform(post(url)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonRequest)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + reporterAccessToken))
					.andExpect(status().isNotFound());
			}
		}

		@Test
		@Transactional
		@DisplayName("신고자, 피신고자가 같은 방에 없음 404")
		public void reportUserNotParticipantFail() throws Exception {
			for (int i = 1; i < rooms.length - 2; i++) { // open, hidden, fail 제외
				//given
				String url = "/party/reports/rooms/" + rooms[i].getId() + "/users/" + userTester.getIntraId();
				;
				String jsonRequest = objectMapper.writeValueAsString(new ReportReqDto("report"));
				//when & then
				mockMvc.perform(post(url)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonRequest)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + otherAccessToken))
					.andExpect(status().isBadRequest());
			}
		}
	}
}
