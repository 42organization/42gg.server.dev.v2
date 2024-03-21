package gg.party.api.user.template;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import gg.auth.utils.AuthTokenProvider;
import gg.data.party.Category;
import gg.data.party.GameTemplate;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.user.template.controller.response.TemplateListResDto;
import gg.party.api.user.template.controller.response.TemplateResDto;
import gg.party.api.user.template.service.TemplateService;
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
	private TestDataUtils testDataUtils;
	@Autowired
	private AuthTokenProvider tokenProvider;
	@MockBean
	private TemplateService templateService;

	private User userTester;
	String userAccessToken;

	@BeforeEach
	void beforeEach() {
		userTester = testDataUtils.createNewUser("templateUserTester", "templateEmailTester",
			RacketType.DUAL, SnsType.SLACK, RoleType.USER);
		userAccessToken = tokenProvider.createToken(userTester.getId());
		Category category = testDataUtils.createNewCategory("테스크 카테고리1");
		GameTemplate template1 = testDataUtils.createNewTemplate(category, "마루티", 4,
			2, 60, 30, "카드", "쉬움", "카드게임");
		GameTemplate template2 = testDataUtils.createNewTemplate(category, "마피아", 5,
			3, 90, 45, "스릴", "보통", "마피아 찾기");

		TemplateResDto templateResDto1 = new TemplateResDto(template1);
		TemplateResDto templateResDto2 = new TemplateResDto(template2);
		TemplateListResDto templateListResDto = new TemplateListResDto(Arrays.asList(templateResDto1, templateResDto2));

		when(templateService.findTemplateList()).thenReturn(templateListResDto);
	}

	@Nested
	@DisplayName("템플릿 조회 테스트")
	class TemplateListTest {

		@Test
		@DisplayName("템플릿 목록 조회 테스트")
		void retrieveTemplateListSuccess() throws Exception {
			mockMvc.perform(get("/party/templates")
					.header("Authorization", "Bearer " + userAccessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.templateList[0].gameName").value("마루티"))
				.andExpect(jsonPath("$.templateList[1].gameName").value("마피아"));

			verify(templateService, times(1)).findTemplateList();
		}
	}
}
