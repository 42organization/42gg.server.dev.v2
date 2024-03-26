package gg.party.api.user.template;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

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
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.user.template.controller.response.TemplateListResDto;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TemplateControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private TestDataUtils testDataUtils;
	@Autowired
	private AuthTokenProvider tokenProvider;
	User userTester;
	User reportedTester;
	String userAccessToken;
	String reportedAccessToken;

	@BeforeEach
	void beforeEach() {
		userTester = testDataUtils.createNewUser("commentUserTester", "emailTester",
			RacketType.DUAL, SnsType.SLACK, RoleType.USER);
		reportedTester = testDataUtils.createNewUser("reportedTester", "reportedTester",
			RacketType.DUAL, SnsType.SLACK, RoleType.USER);
		testDataUtils.createNewPenalty(reportedTester, "test", "test",
			LocalDateTime.now(), 60);
		userAccessToken = tokenProvider.createToken(userTester.getId());
		reportedAccessToken = tokenProvider.createToken(reportedTester.getId());
		Category testCategory = testDataUtils.createNewCategory("test");
		for (int i = 0; i < 15; i++) {
			testDataUtils.createNewTemplate(testCategory, "test" + i, 4, 2, 60, 30, "test" + i, "test" + i, "test" + i);
		}
	}

	@Nested
	@DisplayName("템플릿 조회 테스트")
	class TemplateList {
		@Test
		@DisplayName("템플릿 목록 조회 성공 200")
		void success() throws Exception {
			//given
			String uri = "/party/templates";
			//when
			String contentAsString = mockMvc.perform(get(uri)
					.header("Authorization", "Bearer " + userAccessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			TemplateListResDto tlrd = objectMapper.readValue(contentAsString, TemplateListResDto.class);
			assertThat(tlrd.getTemplateList().size()).isEqualTo(15);
		}
	}
}
