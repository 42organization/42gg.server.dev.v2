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
import gg.data.party.GameTemplate;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.admin.templates.controller.request.TemplateAdminCreateReqDto;
import gg.party.api.admin.templates.controller.request.TemplateAdminUpdateReqDto;
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
	CategoryRepository categoryRepository;
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
	class AddTemplate {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("adminTester", "adminTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
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
			TemplateAdminCreateReqDto templateAdminCreateReqDto = new TemplateAdminCreateReqDto(
				testCategory.getId(), "gameName", 4, 2,
				180, 180, "genre", "hard", "summary");
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
				180, 180, "genre", "hard", "summary");
			String jsonRequest = objectMapper.writeValueAsString(templateAdminCreateReqDto);
			//when && then
			String contentAsString = mockMvc.perform(post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isNotFound()).toString();
		}
	}

	@Nested
	@DisplayName("템플릿 수정 테스트")
	class UpdateTemplate {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("adminTester", "adminTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			testTemplate = testDataUtils.createTemplate(testCategory, "gameName", 4,
				2, 180, 180, "genre", "hard", "summary");
		}

		/**
		 * 템플릿 수정
		 * 어드민만 할 수 있음.
		 */
		@Test
		@DisplayName("수정 성공 204")
		public void success() throws Exception {
			String templateId = testTemplate.getId().toString();
			String url = "/party/admin/templates/" + templateId;
			//given
			Category newTestCategory = testDataUtils.createNewCategory("newCate");
			TemplateAdminUpdateReqDto templateAdminUpdateReqDto = new TemplateAdminUpdateReqDto(
				newTestCategory.getId(), "newGameName", 8, 4,
				90, 90, "newGenre", "easy", "newSummary");
			String jsonRequest = objectMapper.writeValueAsString(templateAdminUpdateReqDto);
			//when
			String contentAsString = mockMvc.perform(patch(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isNoContent())
				.andReturn().getResponse().getContentAsString();
			//then
			assertThat(testTemplate.getGameName()).isEqualTo("newGameName");
			assertThat(testTemplate.getCategory()).isEqualTo(newTestCategory);
			assertThat(testTemplate.getMaxGamePeople()).isEqualTo(8);
			assertThat(testTemplate.getMinGamePeople()).isEqualTo(4);
			assertThat(testTemplate.getMaxGameTime()).isEqualTo(90);
			assertThat(testTemplate.getMinGameTime()).isEqualTo(90);
			assertThat(testTemplate.getGenre()).isEqualTo("newGenre");
			assertThat(testTemplate.getDifficulty()).isEqualTo("easy");
			assertThat(testTemplate.getSummary()).isEqualTo("newSummary");
		}

		@Test
		@DisplayName("카테고리 없음으로 인한 수정 실패 404")
		public void noCategoryFail() throws Exception {
			String templateId = testTemplate.getId().toString();
			String url = "/party/admin/templates/" + templateId;
			//given
			TemplateAdminUpdateReqDto templateAdminUpdateReqDto = new TemplateAdminUpdateReqDto(
				10L, "newGameName", 8, 4,
				90, 90, "newGenre", "easy", "newSummary");
			String jsonRequest = objectMapper.writeValueAsString(templateAdminUpdateReqDto);
			//when && then
			String contentAsString = mockMvc.perform(patch(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isNotFound()).toString();
		}

		@Test
		@DisplayName("템플릿 없음으로 인한 수정 실패 404")
		public void noTemplateFail() throws Exception {
			String templateId = "10";
			String url = "/party/admin/templates/" + templateId;
			//given
			Category newTestCategory = testDataUtils.createNewCategory("newCate");
			TemplateAdminUpdateReqDto templateAdminUpdateReqDto = new TemplateAdminUpdateReqDto(
				newTestCategory.getId(), "newGameName", 8, 4,
				90, 90, "newGenre", "easy", "newSummary");
			String jsonRequest = objectMapper.writeValueAsString(templateAdminUpdateReqDto);
			//when && then
			String contentAsString = mockMvc.perform(patch(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonRequest)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isNotFound()).toString();
		}
	}

	@Nested
	@DisplayName("템플릿 삭제 테스트")
	class RemoveTemplate {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("adminTester", "adminTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			testCategory = testDataUtils.createNewCategory("category");
			testTemplate = testDataUtils.createTemplate(testCategory, "gameName", 4,
				2, 180, 180, "genre", "hard", "summary");
		}

		/**
		 * 템플릿 삭제
		 * 어드민만 할 수 있음.
		 */
		@Test
		@DisplayName("삭제 성공 204")
		public void success() throws Exception {
			String templateId = testTemplate.getId().toString();
			String url = "/party/admin/templates/" + templateId;
			//given && when
			mockMvc.perform(delete(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isNoContent());
			//then
			assertThat(templateRepository.findAll()).isEmpty();
		}

		@Test
		@DisplayName("템플릿 없음으로 인한 삭제 실패 404")
		public void fail() throws Exception {
			String templateId = "10";
			String url = "/party/admin/templates/" + templateId;
			//given && when
			mockMvc.perform(delete(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isNotFound());
		}
	}
}
