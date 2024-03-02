package gg.recruit.api.admin.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.data.recruit.recruitment.Recruitments;
import gg.data.user.User;
import gg.recruit.api.admin.controller.request.UpdateStatusRequestDto;
import gg.recruit.api.user.RecruitMockData;
import gg.repo.recruit.user.recruitment.RecruitmentRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc
class AdminRecruitmentControllerTest {
	@Autowired
	private RecruitMockData recruitMockData;

	@Autowired
	private TestDataUtils testDataUtils;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RecruitmentRepository recruitmentRepository;

	@Test
	@DisplayName("PATCH /admin/recruitments/{recruitId}/status -> 204 NO CONTENT TEST")
	public void updateRecruitStatusTest() throws Exception {
		//given
		Recruitments recruitments = recruitMockData.createRecruitments();
		UpdateStatusRequestDto requestDto = new UpdateStatusRequestDto(true);
		User adminUser = testDataUtils.createAdminUser();

		//when
		mockMvc.perform(patch("/admin/recruitments/{recruitId}/status", recruitments.getId())
			.header("Authorization", "Bearer " + testDataUtils.getLoginAccessTokenFromUser(adminUser))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isNoContent());

		//then
		Recruitments updatedRecruitments = recruitmentRepository.findById(recruitments.getId()).get();
		assertTrue(updatedRecruitments.getIsFinish());
	}

}