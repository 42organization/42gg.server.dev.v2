package gg.recruit.api.user.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.data.recruit.application.Application;
import gg.data.recruit.recruitment.Recruitments;
import gg.data.user.User;
import gg.recruit.api.user.RecruitMockData;
import gg.recruit.api.user.controller.response.ActiveRecruitmentListResDto;
import gg.recruit.api.user.controller.response.RecruitmentDetailResDto;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc
class RecruitmentControllerTest {
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private RecruitMockData recruitMockData;
	@Autowired
	private TestDataUtils testDataUtils;

	@Test
	@DisplayName("GET /recruitments -> 200 OK TEST")
	void findActiveRecruitmentList() throws Exception {
		//given
		User user = testDataUtils.createNewUser();
		String accessToken = testDataUtils.getLoginAccessTokenFromUser(user);

		Recruitments recruitments = recruitMockData.createRecruitments();
		Recruitments recruitments2 = recruitMockData.createRecruitments();
		Recruitments recruitments3 = recruitMockData.createRecruitments();
		//when
		String res = mockMvc.perform(get("/recruitments")
				.param("page", "1")
				.param("size", "10")
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		//then
		ActiveRecruitmentListResDto resDto = objectMapper.readValue(res, ActiveRecruitmentListResDto.class);
		assertThat(resDto.getRecruitments().size()).isEqualTo(3);
		assertThat(resDto.getTotalPage()).isEqualTo(1);
	}

	@Test
	@DisplayName("GET /recruitments/{recruitmentId} -> 200 OK TEST")
	void findRecruitmentDetail() throws Exception {
		//given
		User user = testDataUtils.createNewUser();
		String accessToken = testDataUtils.getLoginAccessTokenFromUser(user);

		Recruitments recruitments = recruitMockData.createRecruitments();
		//when
		String res = mockMvc.perform(get("/recruitments/" + recruitments.getId())
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		//then
	}

	@Test
	@DisplayName("GET /recruitments/{recruitmentId} -> 404 NOT FOUND TEST")
	void findRecruitmentDetailDel() throws Exception {
		//given
		User user = testDataUtils.createNewUser();
		String accessToken = testDataUtils.getLoginAccessTokenFromUser(user);

		Recruitments recruitments = recruitMockData.createRecruitmentsDel();
		//when
		String res = mockMvc.perform(get("/recruitments/" + recruitments.getId())
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();

		//then
	}

	@Test
	@DisplayName("GET /recruitments/{recruitmentId} 지원서 id 조회 -> 200 OK TEST")
	void findRecruitmentDetailAndApplicationId() throws Exception {
		//given
		User user = testDataUtils.createNewUser();
		String accessToken = testDataUtils.getLoginAccessTokenFromUser(user);

		Recruitments recruitments = recruitMockData.createRecruitments();
		Application application = recruitMockData.createApplication(user, recruitments);
		//when
		String result = mockMvc.perform(get("/recruitments/" + recruitments.getId())
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		//then
		RecruitmentDetailResDto res = objectMapper.readValue(result, RecruitmentDetailResDto.class);
		assertThat(res.getApplicationId()).isNotNull();
	}
}
