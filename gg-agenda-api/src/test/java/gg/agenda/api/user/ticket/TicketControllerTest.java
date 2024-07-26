package gg.agenda.api.user.ticket;

import static gg.data.agenda.type.Location.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;
import gg.data.user.User;
import gg.repo.agenda.AgendaTeamRepository;
import gg.repo.agenda.TicketRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.fixture.agenda.AgendaFixture;
import gg.utils.fixture.agenda.AgendaProfileFixture;
import gg.utils.fixture.agenda.AgendaTeamFixture;
import gg.utils.fixture.agenda.AgendaTeamProfileFixture;
import gg.utils.fixture.agenda.TicketFixture;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class TicketControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TestDataUtils testDataUtils;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private AgendaTeamRepository agendaTeamRepository;
	@Autowired
	private AgendaFixture agendaFixture;
	@Autowired
	private AgendaTeamFixture agendaTeamFixture;
	@Autowired
	private AgendaProfileFixture agendaProfileFixture;
	@Autowired
	private AgendaTeamProfileFixture agendaTeamProfileFixture;
	@Autowired
	private TicketFixture ticketFixture;
	User seoulUser;
	User gyeongsanUser;
	String seoulUserAccessToken;
	String gyeongsanUserAccessToken;
	AgendaProfile seoulUserAgendaProfile;
	AgendaProfile gyeongsanUserAgendaProfile;

	@Nested
	@DisplayName("Apporve되어 있지 않은 티켓 생성 테스트")
	class AddTeamTest {
		@BeforeEach
		void beforeEach() {
			seoulUser = testDataUtils.createNewUser();
			seoulUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(seoulUser);
			seoulUserAgendaProfile = agendaProfileFixture.createAgendaProfile(seoulUser, SEOUL);
			gyeongsanUser = testDataUtils.createNewUser();
			gyeongsanUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(gyeongsanUser);
			gyeongsanUserAgendaProfile = agendaProfileFixture.createAgendaProfile(gyeongsanUser, GYEONGSAN);
		}

		@Test
		@DisplayName("티켓 생성 성공")
		void addTicketSetupSuccess() throws Exception {
			//given && when
			mockMvc.perform(
					post("/agenda/ticket")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
			// then
			Ticket createdTicket = ticketRepository.findByAgendaProfileId(seoulUserAgendaProfile.getId())
				.orElseThrow();
			assertThat(createdTicket.getAgendaProfile().getId()).isEqualTo(seoulUserAgendaProfile.getId());
			assertThat(createdTicket.getIsApproved()).isFalse();
			assertThat(createdTicket.getIsUsed()).isFalse();
		}

		@Test
		@DisplayName("404 티켓 생성 실패 - 프로필이 존재하지 않는 경우")
		void addTicketSetupFailToNotFoundProfile() throws Exception {
			//given
			User notExistUser = testDataUtils.createNewUser();
			String notExistUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(notExistUser);
			//when
			mockMvc.perform(
					post("/agenda/ticket")
						.header("Authorization", "Bearer " + notExistUserAccessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("409 티켓 생성 실패 - 이미 티켓이 존재하는 경우")
		void addTicketSetupFailToAnotherTicketSet() throws Exception {
			//given
			ticketFixture.createNotApporveTicket(seoulUserAgendaProfile);
			//when
			mockMvc.perform(
					post("/agenda/ticket")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
		}
	}
}
