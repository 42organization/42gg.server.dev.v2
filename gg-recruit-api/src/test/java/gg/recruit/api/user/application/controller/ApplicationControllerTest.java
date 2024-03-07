package gg.recruit.api.user.application.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.data.recruit.recruitment.Recruitments;
import gg.data.user.User;
import gg.recruit.api.user.application.controller.response.MyApplicationsResDto;
import gg.utils.RecruitMockData;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc
class ApplicationControllerTest {

	@Autowired
	RecruitMockData recruitMockData;

	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	MockMvc mockMvc;

	@Test
	@DisplayName("GET /recruitments/applications -> 200 OK TEST")
	public void getMyApplicationsTest() throws Exception {
		//given
		User user = testDataUtils.createNewUser();
		String accessToken = testDataUtils.getLoginAccessTokenFromUser(user);

		Recruitments recruitments = recruitMockData.createRecruitments();
		Recruitments recruitments2 = recruitMockData.createRecruitments();
		Recruitments recruitments3 = recruitMockData.createRecruitments();

		recruitMockData.createApplication(user, recruitments);
		recruitMockData.createApplication(user, recruitments2);
		recruitMockData.createApplication(user, recruitments3);

		//when
		String res = mockMvc.perform(get(("/recruitments/applications"))
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		//then
		MyApplicationsResDto myApplicationsResDto = objectMapper.readValue(res, MyApplicationsResDto.class);
		assertEquals(3, myApplicationsResDto.getApplications().size());
	}
}
