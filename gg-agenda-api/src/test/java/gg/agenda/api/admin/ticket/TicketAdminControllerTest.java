package gg.agenda.api.admin.ticket;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.admin.repo.agenda.TicketAdminRepository;
import gg.agenda.api.admin.ticket.controller.request.TicketAddAdminReqDto;
import gg.agenda.api.admin.ticket.controller.response.TicketAddAdminResDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.Location;
import gg.data.user.User;
import gg.data.user.type.RoleType;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.fixture.agenda.AgendaFixture;
import gg.utils.fixture.agenda.AgendaProfileFixture;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class TicketAdminControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TestDataUtils testDataUtils;
	@Autowired
	private AgendaProfileFixture agendaProfileFixture;
	@Autowired
	private AgendaFixture agendaFixture;
	@Autowired
	private TicketAdminRepository ticketAdminRepository;
	User user;
	String accessToken;
	AgendaProfile agendaProfile;

	@Nested
	@DisplayName("티켓 발급 요청")
	class AddTicket {
		@BeforeEach
		void beforeEach() {
			user = testDataUtils.createNewAdminUser(RoleType.ADMIN);
			accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
			agendaProfile = agendaProfileFixture.createAgendaProfile(user, Location.SEOUL);
		}

		@Test
		@DisplayName("issuedFromKey = null로 티켓이 잘 생성되어 201 반환 및 ticketId 반환")
		void createTicketWithNullIssuedFromKey() throws Exception {
			// Given
			String content = objectMapper.writeValueAsString(new TicketAddAdminReqDto(null));

			// When
			String responseContent = mockMvc.perform(
					post("/agenda/admin/ticket")
						.param("intraId", user.getIntraId())
						.header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(content))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

			TicketAddAdminResDto response = objectMapper.readValue(responseContent, TicketAddAdminResDto.class);
			Ticket createdTicket = ticketAdminRepository.findByAgendaProfile(agendaProfile)
				.orElseThrow();
			// Then
			assertThat(response.getTicketId()).isNotNull();
			assertThat(createdTicket.getAgendaProfile().getId()).isEqualTo(agendaProfile.getId());
			assertThat(createdTicket.getIssuedFrom()).isNull();
			assertThat(createdTicket.getIsApproved()).isTrue();
			assertThat(createdTicket.getIsUsed()).isFalse();
		}

		@Test
		@DisplayName("존재하는 대회의 issuedFromKey 넣어 티켓(환불티켓생성)이 잘 생성되어 201 반환 및 ticketId 반환")
		void createTicketWithNotNullIssuedFromKey() throws Exception {
			// Given
			User agendaCreateUser = testDataUtils.createNewUser();
			Agenda agenda = agendaFixture.createAgenda(agendaCreateUser.getIntraId(), AgendaStatus.OPEN);
			String content = objectMapper.writeValueAsString(new TicketAddAdminReqDto(agenda.getAgendaKey()));

			// When
			String responseContent = mockMvc.perform(
					post("/agenda/admin/ticket")
						.param("intraId", user.getIntraId())
						.header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(content))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

			TicketAddAdminResDto response = objectMapper.readValue(responseContent, TicketAddAdminResDto.class);
			Ticket createdTicket = ticketAdminRepository.findByAgendaProfile(agendaProfile)
				.orElseThrow();
			// Then
			assertThat(response.getTicketId()).isNotNull();
			assertThat(createdTicket.getAgendaProfile().getId()).isEqualTo(agendaProfile.getId());
			assertThat(createdTicket.getIssuedFrom()).isEqualTo(agenda.getAgendaKey());
			assertThat(createdTicket.getIsApproved()).isTrue();
			assertThat(createdTicket.getIsUsed()).isFalse();
		}

		@Test
		@DisplayName("존재하지않는 대회의 issuedFromKey 넣어 404 반환")
		void createTicketWithAgendaNotExit() throws Exception {
			// Given
			UUID nonExistentAgendaKey = UUID.randomUUID();
			String content = objectMapper.writeValueAsString(new TicketAddAdminReqDto(nonExistentAgendaKey));

			// When & Then
			mockMvc.perform(
					post("/agenda/admin/ticket")
						.param("intraId", user.getIntraId())
						.header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(content))
				.andExpect(status().isNotFound());
		}
	}
}
