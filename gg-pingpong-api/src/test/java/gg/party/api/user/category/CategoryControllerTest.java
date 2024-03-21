package gg.party.api.user.category;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.user.category.controller.response.CategoryListResDto;
import gg.party.api.user.category.controller.response.CategoryResDto;
import gg.party.api.user.category.service.CategoryService;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private TestDataUtils testDataUtils;
	@Autowired
	private AuthTokenProvider tokenProvider;
	@MockBean
	private CategoryService categoryService;

	private User userTester;
	String userAccessToken;

	@BeforeEach
	void beforeEach() {
		userTester = testDataUtils.createNewUser("commentUserTester", "emailTester",
			RacketType.DUAL, SnsType.SLACK, RoleType.USER);
		userAccessToken = tokenProvider.createToken(userTester.getId());
		Category category1 = testDataUtils.createNewCategory("테스트 카테고리1");
		Category category2 = testDataUtils.createNewCategory("테스트 카테고리2");

		CategoryResDto categoryResDto1 = new CategoryResDto(category1);
		CategoryResDto categoryResDto2 = new CategoryResDto(category2);
		CategoryListResDto categoryListResDto = new CategoryListResDto(Arrays.asList(categoryResDto1, categoryResDto2));

		when(categoryService.findCategoryList()).thenReturn(categoryListResDto);

	}

	@Nested
	@DisplayName("카테고리 조회 테스트")
	class CategoryListTest {

		@Test
		@DisplayName("카테고리 목록 조회 테스트")
		void categoryListSuccess() throws Exception {
			mockMvc.perform(get("/party/categories")
					.header("Authorization", "Bearer " + userAccessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.categoryList[0].categoryName").value("테스트 카테고리1"))
				.andExpect(jsonPath("$.categoryList[1].categoryName").value("테스트 카테고리2"));

			verify(categoryService, times(1)).findCategoryList(); //조회 1번만 되었는지 체크
		}
	}
}
