package gg.party.api.admin.penalty;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.auth.utils.AuthTokenProvider;
import gg.data.party.PartyPenalty;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.admin.penalty.controller.request.PartyPenaltyAdminReqDto;
import gg.party.api.admin.penalty.controller.response.PartyPenaltyListAdminResDto;
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
	private User reportedTester;
	private User adminUser;
	private String adminAccessToken;
	private Long testPenaltyId;

	@BeforeEach
	void beforeEach() {
		userTester = testDataUtils.createNewUser("user1", "emailTester",
			RacketType.DUAL, SnsType.SLACK, RoleType.USER);
		reportedTester = testDataUtils.createNewUser("reportedUser", "reportedTester",
			RacketType.DUAL, SnsType.SLACK, RoleType.USER);
		adminUser = testDataUtils.createNewUser("adminUser", "adminTester@example.com",
			RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
		PartyPenalty testPenalty = testDataUtils.createNewPenalty(reportedTester, "test_penalty",
			"이유는_테스트라서", LocalDateTime.now(), 60);
		testPenaltyId = testPenalty.getId();
		adminAccessToken = tokenProvider.createToken(adminUser.getId());
	}

	@Nested
	@DisplayName("패널티 테스트")
	class PenaltyAdminTests {

		@Test
		@DisplayName("패널티 조회 - 200")
		void testRetrievePenaltiesList() throws Exception {
			//given
			for (int i = 1; i <= 9; i++) {
				testDataUtils.createNewPenalty(reportedTester, "test_penalty_" + i,
					"test_reason" + i, LocalDateTime.now(), 60);
			}
			int pageSize = 10;
			int pageNumber = 1;
			String url = String.format("/party/admin/penalties?page=%d&size=%d", pageNumber, pageSize);

			//when
			MvcResult result = mockMvc.perform(get(url)
					.header("Authorization", "Bearer " + adminAccessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

			//then
			String content = result.getResponse().getContentAsString();
			PartyPenaltyListAdminResDto responseDto =
				objectMapper.readValue(content, PartyPenaltyListAdminResDto.class);

			assertEquals(pageSize, responseDto.getPenaltyList().size());
		}

		@Test
		@DisplayName("패널티 조회(pagination) - 200")
		void testPaginationPenaltiesList() throws Exception {
			//given
			for (int i = 1; i <= 15; i++) {
				testDataUtils.createNewPenalty(reportedTester, "test_penalty_" + i,
					"test_reason" + i, LocalDateTime.now(), 60);
			}
			int pageSize = 10;
			int pageNumber = 2;
			String url = String.format("/party/admin/penalties?page=%d&size=%d", pageNumber, pageSize);

			//when
			MvcResult result = mockMvc.perform(get(url)
					.header("Authorization", "Bearer " + adminAccessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

			//then
			String content = result.getResponse().getContentAsString();
			PartyPenaltyListAdminResDto responseDto =
				objectMapper.readValue(content, PartyPenaltyListAdminResDto.class);

			int expectedPageSize = 6;
			assertEquals(expectedPageSize, responseDto.getPenaltyList().size());
		}

		@Test
		@DisplayName("패널티 수정 - 204")
		void testModifyAdminPenalty() throws Exception {
			//given
			PartyPenaltyAdminReqDto penaltyDto = new PartyPenaltyAdminReqDto("test_penalty", "Test reason", 60, reportedTester.getIntraId());

			//when
			mockMvc.perform(patch("/party/admin/penalties/{penaltyId}", testPenaltyId)
					.header("Authorization", "Bearer " + adminAccessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(penaltyDto)))
				.andExpect(status().isNoContent());

			//then
			PartyPenalty updatedPenalty = partyPenaltyRepository.findById(testPenaltyId).orElseThrow();

			assertEquals(penaltyDto.getPenaltyType(), updatedPenalty.getPenaltyType());
			assertEquals(penaltyDto.getMessage(), updatedPenalty.getMessage());
			assertEquals(penaltyDto.getPenaltyTime(), updatedPenalty.getPenaltyTime().intValue());
		}

		@Test
		@DisplayName("패널티 수정 - 실패 시나리오(없는 유저) - 404")
		void testModifyAdminPenalty_NotFound() throws Exception {
			//given
			Long nonExistentPenaltyId = 999L;

			PartyPenaltyAdminReqDto penaltyDto = new PartyPenaltyAdminReqDto(
				"test_penalty",
				"test_reason",
				60,
				"nonexistentIntraId"
			);

			//when
			mockMvc.perform(patch("/party/admin/penalties/{penaltyId}", nonExistentPenaltyId)
					.header("Authorization", "Bearer " + adminAccessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(penaltyDto)))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("패널티 부여 (일반적인 상황) - 201")
		void testGiveAdminPenalty() throws Exception {
			//given
			PartyPenaltyAdminReqDto penaltyDto = new PartyPenaltyAdminReqDto(
				"test_penalty",
				"Test reason",
				60,
				userTester.getIntraId()
			);

			long penaltyCountBefore = partyPenaltyRepository.count();

			//when
			mockMvc.perform(post("/party/admin/penalties")
					.header("Authorization", "Bearer " + adminAccessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(penaltyDto)))
				.andExpect(status().isCreated());

			//then
			long penaltyCountAfter = partyPenaltyRepository.count();
			assertEquals(penaltyCountBefore + 1, penaltyCountAfter);
		}

		@Test
		@DisplayName("패널티 부여 (패널티된 유저에게 추가 패널티 부여) - 201")
		void testAddAdminPenalty() throws Exception {
			//given
			PartyPenaltyAdminReqDto morePenaltyDto = new PartyPenaltyAdminReqDto(
				"test_penalty",
				"Test reason",
				60,
				reportedTester.getIntraId()
			);

			//when
			mockMvc.perform(post("/party/admin/penalties")
					.header("Authorization", "Bearer " + adminAccessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(morePenaltyDto)))
				.andExpect(status().isCreated());

			//then
			List<PartyPenalty> penalties = partyPenaltyRepository.findAllByUserId(reportedTester.getId());
			int totalPenaltyTime = penalties.stream().mapToInt(PartyPenalty::getPenaltyTime).sum();

			assertEquals(120, totalPenaltyTime);
		}

		@Test
		@DisplayName("패널티 부여 - 실패 시나리오(없는 유저) - 404")
		void testAddAdminPenalty_UserNotFound() throws Exception {
			//given
			PartyPenaltyAdminReqDto penaltyDto = new PartyPenaltyAdminReqDto(
				"test_penalty",
				"Test reason",
				60,
				"nonexistentIntraId"
			);

			//when
			mockMvc.perform(post("/party/admin/penalties")
					.header("Authorization", "Bearer " + adminAccessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(penaltyDto)))
				.andExpect(status().isNotFound());
		}
	}
}
