package gg.party.api.user.penalty;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.auth.utils.AuthTokenProvider;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.user.penalty.controller.response.PenaltyResDto;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class PartyPenaltyControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AuthTokenProvider tokenProvider;
	User userTester;
	User reportedTester;
	String userAccessToken;
	String reportedAccessToken;

	@Nested
	@DisplayName("패널티 조회 테스트")
	class FindPenaltyTest {
		@BeforeEach
		void beforeEach() {
			userTester = testDataUtils.createNewUser("userTester", "userTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			userAccessToken = tokenProvider.createToken(userTester.getId());
			reportedTester = testDataUtils.createNewUser("reportedTester", "reportedTester",
				RacketType.DUAL, SnsType.SLACK, RoleType.USER);
			reportedAccessToken = tokenProvider.createToken(reportedTester.getId());
			testDataUtils.createNewPenalty(reportedTester, "test", "test", LocalDateTime.of(2222, 2, 22, 2, 22), 60);
		}

		@Test
		@DisplayName("패널티 조회 성공 200")
		public void penaltySuccess() throws Exception {
			//given
			String url = "/party/penalty";
			//when
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + reportedAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			PenaltyResDto prd = objectMapper.readValue(contentAsString, PenaltyResDto.class);
			assertThat(prd.getPenaltyEndTime()).isEqualTo(LocalDateTime.of(2222, 2, 22, 3, 22));
		}

		@Test
		@DisplayName("패널티 없는 사람 조회 성공 200")
		public void userSuccess() throws Exception {
			//given
			String url = "/party/penalty";
			//when
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + userAccessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			//then
			PenaltyResDto prd = objectMapper.readValue(contentAsString, PenaltyResDto.class);
			assertThat(prd.getPenaltyEndTime()).isNull();
		}
	}
}

