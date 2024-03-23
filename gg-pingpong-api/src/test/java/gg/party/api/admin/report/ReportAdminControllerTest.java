package gg.party.api.admin.report;

import static java.lang.Boolean.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
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
import gg.data.party.Comment;
import gg.data.party.GameTemplate;
import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.admin.report.controller.response.CommentReportListResDto;
import gg.party.api.admin.report.service.ReportAdminService;
import gg.party.api.admin.templates.service.TemplateAdminService;
import gg.repo.party.CategoryRepository;
import gg.repo.party.TemplateRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReportAdminControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AuthTokenProvider tokenProvider;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ReportAdminService reportAdminService;
	@Autowired
	TemplateAdminService templateAdminService;
	@Autowired
	TemplateRepository templateRepository;
	User userTester;
	String userAccessToken;
	Category testCategory;
	GameTemplate testTemplate;

	@Nested
	@DisplayName("템플릿 추가 테스트")
	class GetCommentReports {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("adminTester", "adminTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
			User user1 = testDataUtils.createNewUser("user1", "user1",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			User user2 = testDataUtils.createNewUser("user2", "user2",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			User user3 = testDataUtils.createNewUser("user3", "user3",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			testCategory = testDataUtils.createNewCategory("test");
			Room Room1 = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, 180, RoomType.OPEN);
			Room Room2 = testDataUtils.createNewRoom(userTester, userTester, testCategory, 2, 1,
				3, 2, 180, RoomType.OPEN);
			Room Room3 = testDataUtils.createNewRoom(userTester, userTester, testCategory, 3, 1,
				3, 2, 180, RoomType.OPEN);
			UserRoom userRoom1 = testDataUtils.createNewUserRoom(user1, Room1, "user1", TRUE);
			UserRoom userRoom2 = testDataUtils.createNewUserRoom(user2, Room2, "user2", TRUE);
			UserRoom userRoom3 = testDataUtils.createNewUserRoom(user2, Room3, "user3", TRUE);
			Comment comment1 = testDataUtils.createComment(user1, userRoom1, Room1, "user1 comment");
			Comment comment2 = testDataUtils.createComment(user2, userRoom2, Room2, "user2 comment");
			Comment comment3 = testDataUtils.createComment(user3, userRoom3, Room3, "user3 comment");
			for (int i = 0; i < 5; i++) {
				testDataUtils.createCommentReport(user1, Room1, comment1);
				testDataUtils.createCommentReport(user2, Room2, comment2);
				testDataUtils.createCommentReport(user3, Room3, comment3);
			}
		}

		/**
		 * 댓글 신고 리스트 조회
		 */
		@Test
		@DisplayName("첫 페이지 조회 성공 200")
		public void startPageSuccess() throws Exception {
			//given
			String currentPage = "1";
			String pageSize = "10";
			String url = "/party/admin/reports/comments?page=" + currentPage + "&size=" + pageSize;
			//when
			String contentAsString = mockMvc.perform(get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			CommentReportListResDto crlrd = objectMapper.readValue(contentAsString, CommentReportListResDto.class);
			assertThat(crlrd.getCommentReportList().size()).isEqualTo(10);
		}

		@Test
		@DisplayName("마지막 페이지 조회 성공 200")
		public void middlePageSuccess() throws Exception {
			//given
			String currentPage = "2";
			String pageSize = "10";
			String url = "/party/admin/reports/comments?page=" + currentPage + "&size=" + pageSize;
			//when
			String contentAsString = mockMvc.perform(get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			CommentReportListResDto crlrd = objectMapper.readValue(contentAsString, CommentReportListResDto.class);
			assertThat(crlrd.getCommentReportList().size()).isEqualTo(5);
		}
	}
}
