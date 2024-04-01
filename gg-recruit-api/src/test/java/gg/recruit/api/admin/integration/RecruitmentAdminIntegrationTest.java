package gg.recruit.api.admin.integration;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.admin.repo.recruit.RecruitmentAdminRepository;
import gg.data.recruit.recruitment.Recruitment;
import gg.recruit.api.RecruitMockData;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class RecruitmentAdminIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestDataUtils testDataUtils;

	@Autowired
	private RecruitMockData recruitMockData;

	@Autowired
	private RecruitmentAdminRepository recruitmentAdminRepository;

	@Nested
	@DisplayName("공고 전체 조회 시 - POST /admin/recruitments")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class PostRecruitment {

		@DisplayName("공고 종료 날짜 기준 최신순으로 조회된다.")
		@ParameterizedTest
		@MethodSource("getRecruitments")
		void getAllRecruitments(List<Recruitment> recruitments) throws Exception {
			// given
			int page = 1;
			int size = 10;
			String url = String.format("/admin/recruitments?page=%d&size=%d", page, size);
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			recruitmentAdminRepository.saveAll(recruitments);

			// when
			String response = mockMvc.perform(get(url)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			System.out.println(response);
		}

		/**
		 * parameterized test에 사용할 List<Recruitment>를 반환하는 메소드
		 * @return 종료 날짜가 서로 다른 3개의 공고 리스트, empty list
		 */
		private Stream<List<Recruitment>> getRecruitments() {
			LocalDateTime startDate = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
			return Stream.of(List.of(
					new Recruitment("title", "contents", "generation", startDate, startDate.plusDays(1)),
					new Recruitment("title", "contents", "generation", startDate, startDate.plusDays(2)),
					new Recruitment("title", "contents", "generation", startDate, startDate.plusDays(3))
				),
				List.of());
		}

	}

	@Nested
	@DisplayName("공고 삭제 시 - DELETE /admin/recruitments/{recruitmentId}")
	class DeleteRecruitment {
		@Test
		@DisplayName("공고가 존재할 경우 삭제 성공 (isDeleted = true) - 204 No Content")
		void deleteRecruitment() throws Exception {
			// given
			Recruitment recruitment = recruitMockData.createRecruitment();
			long recruitmentId = recruitment.getId();
			String url = String.format("/admin/recruitments/%d", recruitmentId);
			String accessToken = testDataUtils.getAdminLoginAccessToken();

			// when
			mockMvc.perform(delete(url)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isNoContent());

			// then
			assertThat(recruitmentAdminRepository.findById(recruitmentId).get().getIsDeleted()).isTrue();
			assertThat(recruitment.getIsDeleted()).isTrue();
		}

		@Test
		@DisplayName("공고가 존재하지 않을 경우 NotExistException 발생하고 삭제는 실패한다. - 404 Not Found")
		void deleteRecruitmentFail() throws Exception {
			// given
			long recruitmentId = 1L;
			String url = String.format("/admin/recruitments/%d", recruitmentId);
			String accessToken = testDataUtils.getAdminLoginAccessToken();

			// when then
			mockMvc.perform(delete(url)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isNotFound());
		}
	}
}
