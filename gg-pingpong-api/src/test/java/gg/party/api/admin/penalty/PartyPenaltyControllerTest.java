package gg.party.api.admin.penalty;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.auth.utils.AuthTokenProvider;
import gg.data.party.PartyPenalty;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.admin.penalty.controller.request.PageReqDto;
import gg.party.api.admin.penalty.controller.request.PartyPenaltyAdminReqDto;
import gg.repo.party.PartyPenaltyRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.extern.slf4j.Slf4j;

@IntegrationTest
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@Slf4j
public class PartyPenaltyControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private TestDataUtils testDataUtils;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private AuthTokenProvider tokenProvider;
	@Autowired
	private PartyPenaltyRepository partyPenaltyRepository;

	private User userTester;
	User reportedTester;
	String userAccessToken;
	String reportedAccessToken;

	@BeforeEach
	void beforeEach() {
		userTester = testDataUtils.createNewUser("User1", "emailTester",
			RacketType.DUAL, SnsType.SLACK, RoleType.USER);
		reportedTester = testDataUtils.createNewUser("reportedUser", "reportedTester",
			RacketType.DUAL, SnsType.SLACK, RoleType.USER);
		PartyPenalty testPenalty = testDataUtils.createNewPenalty(reportedTester, "test_penalty",
			"becauseTest",
			LocalDateTime.now(), 60);
		userAccessToken = tokenProvider.createToken(userTester.getId());
		reportedAccessToken = tokenProvider.createToken(reportedTester.getId());
	}

	@Nested
	@DisplayName("패널티 테스트")
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	class PenaltyAdminTests {

		@Test
		@DisplayName("패널티 조회")
		void testRetrievePenaltiesList() throws Exception {
			PageReqDto reqDto = new PageReqDto(1, 10);

			mockMvc.perform(get("/party/admin/penalties?page=1&size=10") //정확한 페이지, 사이즈 정보 필요
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isOk());
		}

		@Test
		@DisplayName("패널티 수정")
		void testModifyAdminPenalty() throws Exception {
			Long penaltyId = 1L;

			PartyPenaltyAdminReqDto penaltyDto = new PartyPenaltyAdminReqDto(
				"test_penalty",
				"Test reason",
				60,
				reportedTester.getIntraId()
			);

			mockMvc.perform(patch("/party/admin/penalties/{penaltyId}", penaltyId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(penaltyDto)))
				.andExpect(status().isNoContent());
		}

		@Test
		@DisplayName("패널티 부여")
		void testAddAdminPenalty() throws Exception {
			PartyPenaltyAdminReqDto penaltyDto = new PartyPenaltyAdminReqDto(
				"test_penalty",
				"Test reason",
				60,
				reportedTester.getIntraId()
			);

			mockMvc.perform(post("/party/admin/penalties")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(penaltyDto)))
				.andExpect(status().isCreated());
		}
	}
}
