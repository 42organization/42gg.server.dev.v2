package gg.party.api.admin.penalty;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import gg.party.api.admin.penalty.controller.response.PartyPenaltyListAdminResDto;
import gg.party.api.admin.penalty.service.PartyPenaltyAdminService;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
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

	@MockBean
	private PartyPenaltyAdminService partyPenaltyAdminService;

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
		PartyPenalty testPenalty = testDataUtils.createNewPenalty(reportedTester, "test_penalty", "becauseTest",
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
			PartyPenaltyListAdminResDto responseDto = new PartyPenaltyListAdminResDto();

			when(partyPenaltyAdminService.findAllPenalty(any(PageReqDto.class))).thenReturn(responseDto);

			mockMvc.perform(get("/party/admin/penalties?page=1&size=10") //정확한 페이지, 사이즈 정보 필요
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isOk());

			verify(partyPenaltyAdminService).findAllPenalty(any(PageReqDto.class));
		}

		@Test
		@DisplayName("패널티 수정")
		void testModifyAdminPenalty() throws Exception {
			Long penaltyId = 1L;
			PartyPenaltyAdminReqDto reqDto = new PartyPenaltyAdminReqDto();

			mockMvc.perform(patch("/party/admin/penalties/{penaltyId}", penaltyId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isNoContent());

			verify(partyPenaltyAdminService).modifyAdminPenalty(eq(penaltyId), any(PartyPenaltyAdminReqDto.class));
		}

		@Test
		@DisplayName("패널티 부여")
		void testAddAdminPenalty() throws Exception {
			PartyPenaltyAdminReqDto reqDto = new PartyPenaltyAdminReqDto();

			mockMvc.perform(post("/party/admin/penalties")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isCreated());

			verify(partyPenaltyAdminService).addAdminPenalty(any(PartyPenaltyAdminReqDto.class));
		}
	}
}
