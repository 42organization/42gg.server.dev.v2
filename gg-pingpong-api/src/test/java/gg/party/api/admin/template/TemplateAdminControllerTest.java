package gg.party.api.admin.template;

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
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.admin.templates.controller.request.TemplateAdminCreateReqDto;
import gg.party.api.admin.templates.service.TemplateAdminService;
import gg.party.api.user.room.service.RoomFindService;
import gg.repo.party.CategoryRepository;
import gg.repo.party.PartyPenaltyRepository;
import gg.repo.party.RoomRepository;
import gg.repo.party.TemplateRepository;
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
public class TemplateAdminControllerTest {
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
	TemplateAdminService templateAdminService;
	@Autowired
	TemplateRepository templateRepository;
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
	@DisplayName("템플릿 추가 테스트")
	class AddTemplate {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("adminTester", "adminTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
			userAccessToken = tokenProvider.createToken(userTester.getId());
		}

		/**
		 * 템플릿을 추가
		 * 어드민만 할 수 있음.
		 */
		@Test
		@DisplayName("추가 성공 201")
		public void success() throws Exception {
			String url = "/party/admin/templates";
			//given
			Category testCategory = testDataUtils.createNewCategory("category");
			TemplateAdminCreateReqDto templateAdminCreateReqDto = new TemplateAdminCreateReqDto(
				testCategory.getId(), "gameName", 4, 2,
				180, 180, "genre", "difficulty", "summary");
			String jsonRequest = objectMapper.writeValueAsString(templateAdminCreateReqDto);
			//when
			String contentAsString = mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
			//then
			assertThat(templateRepository.findAll()).isNotNull();
		}

		@Test
		@DisplayName("카테고리 없음으로 인한 추가 실패 404")
		public void fail() throws Exception {
			String url = "/party/admin/templates";
			//given
			TemplateAdminCreateReqDto templateAdminCreateReqDto = new TemplateAdminCreateReqDto(
				10L, "gameName", 4, 2,
				180, 180, "genre", "difficulty", "summary");
			String jsonRequest = objectMapper.writeValueAsString(templateAdminCreateReqDto);
			//when
			String contentAsString = mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isNotFound()).toString();
		}
	}
}
