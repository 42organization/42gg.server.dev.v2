package gg.party.api.admin.comment;

import static java.lang.Boolean.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
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
import gg.data.party.Comment;
import gg.data.party.PartyPenalty;
import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.admin.comment.controller.request.CommentUpdateAdminReqDto;
import gg.repo.party.CategoryRepository;
import gg.repo.party.CommentRepository;
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
public class CommentAdminControllerTest {
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
	CommentRepository commentRepository;
	User userTester;
	User reportedTester;
	User adminTester;
	String adminAccessToken;
	Room testRoom;
	Room reportTestRoom;

	@Nested
	@DisplayName("Comment Show 여부 수정 테스트")
	class HideComment {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewImageUser("findTester", "findTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER, "userImage");
			reportedTester = testDataUtils.createNewImageUser("reportedTester", "reportedTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER, "reportedImage");
			PartyPenalty testPenalty = testDataUtils.createNewPenalty(reportedTester, "test", "test",
				LocalDateTime.now(), 60);
			adminTester = testDataUtils.createNewImageUser("adminTester", "adminTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN, "adminImage");
			adminAccessToken = tokenProvider.createToken(adminTester.getId());
			Category testCategory = testDataUtils.createNewCategory("test");
			testRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1, 3, 2, 180,
				RoomType.OPEN);
			reportTestRoom = testDataUtils.createNewRoom(reportedTester, reportedTester, testCategory, 1, 1, 3, 2, 180,
				RoomType.OPEN);
			UserRoom testUserRoom = testDataUtils.createNewUserRoom(userTester, testRoom, "testNickname", TRUE);
			UserRoom reportUserRoom = testDataUtils.createNewUserRoom(reportedTester, reportTestRoom, "reportNickname",
				TRUE);
			userTester = testDataUtils.createNewUser("findTester", "findTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			testDataUtils.createComment(userTester, testUserRoom, testRoom, "testComment");
			testDataUtils.createReportComment(reportedTester, reportUserRoom, reportTestRoom, "reportComment");
		}

		@Test
		@DisplayName("isHidden False -> True 변경 성공 204")
		public void success() throws Exception {
			Comment comment = commentRepository.findByRoomId(testRoom.getId()).get(0);
			String commentId = comment.getId().toString();
			String url = "/party/admin/comments" + commentId;
			CommentUpdateAdminReqDto commentUpdateAdminReqDto = new CommentUpdateAdminReqDto(TRUE);
			String requestBody = objectMapper.writeValueAsString(commentUpdateAdminReqDto);
			//given
			String contentAsString = mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminTester))
				.andExpect(status().isBadRequest()).toString();
			//then
			assertThat(comment.isHidden()).isEqualTo(TRUE);
		}

		@Test
		@DisplayName("isHidden True -> False 변경 성공 204")
		public void reportCommentSuccess() throws Exception {
			Comment comment = commentRepository.findByRoomId(reportTestRoom.getId()).get(0);
			String commentId = comment.getId().toString();
			String url = "/party/admin/comments" + commentId;
			CommentUpdateAdminReqDto commentUpdateAdminReqDto = new CommentUpdateAdminReqDto(FALSE);
			String requestBody = objectMapper.writeValueAsString(commentUpdateAdminReqDto);
			//given
			String contentAsString = mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminTester))
				.andExpect(status().isBadRequest()).toString();
			//then
			assertThat(comment.isHidden()).isEqualTo(FALSE);
		}

		@Test
		@DisplayName("없는 Comment로 인한 에러 404")
		public void fail() throws Exception {
			String commentId = "1000";
			String url = "/party/admin/comments" + commentId;
			CommentUpdateAdminReqDto commentUpdateAdminReqDto = new CommentUpdateAdminReqDto(TRUE);
			String requestBody = objectMapper.writeValueAsString(commentUpdateAdminReqDto);
			//given
			String contentAsString = mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminTester))
				.andExpect(status().isNotFound()).toString();
		}
	}
}
