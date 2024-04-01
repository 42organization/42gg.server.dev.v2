package gg.recruit.api.admin.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.data.recruit.recruitment.enums.InputType;
import gg.recruit.api.admin.controller.request.RecruitmentRequestDto;
import gg.recruit.api.admin.service.dto.Form;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class RecruitmentAdminControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestDataUtils testDataUtils;

	@Nested
	@DisplayName("공고 생성")
	public class CreateRecruitmentTest {
		@Test
		@DisplayName("성공")
		public void testCreateRecruitment() throws Exception {
			// given
			String accessToken = testDataUtils.getAdminLoginAccessToken();
			List<Form> forms = List.of(Form.builder().question("질문").inputType(InputType.SINGLE_CHECK)
					.checkList(List.of("선택지1", "선택지2")).build(),
				Form.builder().question("질문2").inputType(InputType.TEXT).build()
			);
			RecruitmentRequestDto requestDto = new RecruitmentRequestDto(LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(2),
				"제목", "내용", "모집 기수", forms);

			// when
			ResultActions response = mockMvc.perform(post("/admin/recruitments")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isCreated());

			// then
		}
	}

	@Test
	@DisplayName("공고 생성 실패 - 선택지 미입력")
	public void testCreateRecruitmentWithEmptyCheckList() throws Exception {
		// given
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		List<Form> forms = List.of(Form.builder().question("질문").inputType(InputType.SINGLE_CHECK).build());
		// .checkList(List.of()).build());
		RecruitmentRequestDto requestDto = new RecruitmentRequestDto(LocalDateTime.now().plusDays(1),
			LocalDateTime.now().plusDays(2),
			"제목", "내용", "모집 기수", forms);

		// when
		ResultActions response = mockMvc.perform(post("/admin/recruitments")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());

		// then
	}
}
