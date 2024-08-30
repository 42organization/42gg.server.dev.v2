package gg.agenda.api.admin.ticket;

import static gg.data.agenda.type.Location.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gg.admin.repo.agenda.TicketAdminRepository;
import gg.agenda.api.admin.ticket.controller.request.TicketAddAdminReqDto;
import gg.agenda.api.admin.ticket.controller.request.TicketChangeAdminReqDto;
import gg.agenda.api.admin.ticket.controller.response.TicketAddAdminResDto;
import gg.agenda.api.admin.ticket.controller.response.TicketFindResDto;
import gg.agenda.api.user.ticket.controller.response.TicketHistoryResDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.Location;
import gg.data.user.User;
import gg.data.user.type.RoleType;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.dto.PageRequestDto;
import gg.utils.dto.PageResponseDto;
import gg.utils.fixture.agenda.AgendaFixture;
import gg.utils.fixture.agenda.AgendaProfileFixture;
import gg.utils.fixture.agenda.TicketFixture;

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
	private TicketFixture ticketFixture;
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

	@Nested
	@DisplayName("티켓 정보 변경")
	class UpdateTicket {
		@BeforeEach
		void beforeEach() {
			user = testDataUtils.createNewAdminUser(RoleType.ADMIN);
			accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
			agendaProfile = agendaProfileFixture.createAgendaProfile(user, Location.SEOUL);
		}

		@Test
		@DisplayName("유효한 정보로 티켓 정보를 변경합니다.")
		void updateTicketWithValidData() throws Exception {
			// Given
			User agendaCreateUser = testDataUtils.createNewUser();
			Ticket ticket = ticketFixture.createNotApporveTicket(agendaProfile);
			Agenda usedAgenda = agendaFixture.createAgenda(agendaCreateUser.getIntraId(), AgendaStatus.OPEN);
			Agenda refundedAgenda = agendaFixture.createAgenda(agendaCreateUser.getIntraId(), AgendaStatus.OPEN);
			String content = objectMapper.writeValueAsString(
				new TicketChangeAdminReqDto(refundedAgenda.getAgendaKey(), usedAgenda.getAgendaKey(), Boolean.TRUE,
					LocalDateTime.now(), Boolean.TRUE, LocalDateTime.now()));

			// When
			mockMvc.perform(
					patch("/agenda/admin/ticket")
						.param("ticketId", String.valueOf(ticket.getId()))
						.header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(content))
				.andExpect(status().isNoContent());

			// Then
			Ticket updatedTicket = ticketAdminRepository.findById(ticket.getId()).orElseThrow();
			assertThat(updatedTicket.getIssuedFrom()).isEqualTo(refundedAgenda.getAgendaKey());
			assertThat(updatedTicket.getUsedTo()).isEqualTo(usedAgenda.getAgendaKey());
			assertThat(updatedTicket.getIsApproved()).isTrue();
			assertThat(updatedTicket.getIsUsed()).isTrue();
		}

		@Test
		@DisplayName("존재하지않는 대회의 issuedFromKey 넣어 404 반환")
		void updateTicketWithInvalidIssuedFromKey() throws Exception {
			// Given
			Ticket ticket = ticketFixture.createNotApporveTicket(agendaProfile);
			String content = objectMapper.writeValueAsString(
				new TicketChangeAdminReqDto(UUID.randomUUID(), null, Boolean.TRUE,
					LocalDateTime.now(), Boolean.FALSE, LocalDateTime.now()));

			// When & Then
			mockMvc.perform(
					patch("/agenda/admin/ticket")
						.param("ticketId", String.valueOf(ticket.getId()))
						.header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(content))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("존재하지않는 대회의 usedToKey 넣어 404 반환")
		void updateTicketWithInvalidUsedToKey() throws Exception {
			// Given
			Ticket ticket = ticketFixture.createNotApporveTicket(agendaProfile);
			String content = objectMapper.writeValueAsString(
				new TicketChangeAdminReqDto(null, UUID.randomUUID(), Boolean.TRUE,
					LocalDateTime.now(), Boolean.TRUE, LocalDateTime.now()));

			// When & Then
			mockMvc.perform(
					patch("/agenda/admin/ticket")
						.param("ticketId", String.valueOf(ticket.getId()))
						.header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(content))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("티켓 목록 조회 테스트")
	class GetTicketList {

		@BeforeEach
		void beforeEach() {
			user = testDataUtils.createNewAdminUser(RoleType.ADMIN);
			accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
			agendaProfile = agendaProfileFixture.createAgendaProfile(user, Location.SEOUL);
		}

		@ParameterizedTest
		@ValueSource(ints = {1, 2, 3})
		@DisplayName("200 모든 티켓 목록 조회 성공")
		void getAllTicketsSuccess(int page) throws Exception {
			// Given
			int size = 10;
			int total = 25;
			List<Ticket> tickets = new ArrayList<>();
			for (int i = 0; i < total; i++) {
				tickets.add(ticketFixture.createTicket(agendaProfile));
			}
			tickets.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));

			PageRequestDto pageRequest = new PageRequestDto(page, size);
			String request = objectMapper.writeValueAsString(pageRequest);

			// When
			String res = mockMvc.perform(
					get("/agenda/admin/ticket/list/" + user.getIntraId())
						.header("Authorization", "Bearer " + accessToken)
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(size))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			PageResponseDto<TicketFindResDto> pageResponseDto = objectMapper.readValue(res, new TypeReference<>() {
			});
			List<TicketFindResDto> result = pageResponseDto.getContent();

			// Then
			int expectedSize = Math.min(size, total - (page - 1) * size);
			assertThat(result).hasSize(expectedSize);

			for (int i = 0; i < result.size(); i++) {
				TicketFindResDto actual = result.get(i);

				int ticketIndex = (page - 1) * size + i;
				if (ticketIndex >= tickets.size()) {
					break;
				}
				Ticket expected = tickets.get(ticketIndex);

				// 검증
				assertThat(actual.getTicketId()).isEqualTo(expected.getId());
				assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
				assertThat(actual.getIsApproved()).isEqualTo(expected.getIsApproved());
				assertThat(actual.getApprovedAt()).isEqualTo(expected.getApprovedAt());
				assertThat(actual.getIsUsed()).isEqualTo(expected.getIsUsed());
				assertThat(actual.getUsedAt()).isEqualTo(expected.getUsedAt());
			}
		}

		@Test
		@DisplayName("200 티켓 히스토리 조회 성공 - approve 되어있지 않은 경우")
		void findTicketHistorySuccessToNotApprove() throws Exception {
			//given
			ticketFixture.createTicket(agendaProfile, false, false, null, null);
			PageRequestDto req = new PageRequestDto(1, 5);
			//when
			String res = mockMvc.perform(
					get("/agenda/admin/ticket/list/" + user.getIntraId())
						.header("Authorization", "Bearer " + accessToken)
						.param("page", String.valueOf(req.getPage()))
						.param("size", String.valueOf(req.getSize())))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			PageResponseDto<TicketHistoryResDto> pageResponseDto = objectMapper.readValue(res, new TypeReference<>() {
			});
			List<TicketHistoryResDto> result = pageResponseDto.getContent();

			//then
			assertThat(result.size()).isEqualTo(1);
			assertThat(result.get(0).getIssuedFrom()).isEqualTo("42Intra");
			assertThat(result.get(0).getUsedTo()).isEqualTo("NotApproved");
		}

		@Test
		@DisplayName("200 티켓 히스토리 조회 성공 - approve 되어있고 used 되어있는 경우")
		void findTicketHistorySuccessToUsed() throws Exception {
			//given
			Agenda seoulAgenda = agendaFixture.createAgenda(SEOUL);
			Ticket ticket = ticketFixture.createTicket(agendaProfile, true, true, null,
				seoulAgenda.getAgendaKey());
			PageRequestDto req = new PageRequestDto(1, 5);
			//when
			String res = mockMvc.perform(
					get("/agenda/admin/ticket/list/" + user.getIntraId())
						.header("Authorization", "Bearer " + accessToken)
						.param("page", String.valueOf(req.getPage()))
						.param("size", String.valueOf(req.getSize())))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			PageResponseDto<TicketHistoryResDto> pageResponseDto = objectMapper.readValue(res, new TypeReference<>() {
			});
			List<TicketHistoryResDto> result = pageResponseDto.getContent();

			//then
			assertThat(result.size()).isEqualTo(1);
			assertThat(result.get(0).getIssuedFrom()).isEqualTo("42Intra");
			assertThat(result.get(0).getUsedTo()).isEqualTo(seoulAgenda.getTitle());
		}

		@Test
		@DisplayName("200 티켓 히스토리 조회 성공 - approve 되어있고 used 되어있지 않은 경우")
		void findTicketHistorySuccessToNotUsed() throws Exception {
			//given
			Agenda seoulAgenda = agendaFixture.createAgenda(SEOUL);
			Ticket ticket = ticketFixture.createTicket(agendaProfile, true, false, null,
				null);
			PageRequestDto req = new PageRequestDto(1, 5);
			String content = objectMapper.writeValueAsString(req);
			//when
			String res = mockMvc.perform(
					get("/agenda/admin/ticket/list/" + user.getIntraId())
						.header("Authorization", "Bearer " + accessToken)
						.param("page", String.valueOf(req.getPage()))
						.param("size", String.valueOf(req.getSize())))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			PageResponseDto<TicketHistoryResDto> pageResponseDto = objectMapper.readValue(res, new TypeReference<>() {
			});
			List<TicketHistoryResDto> result = pageResponseDto.getContent();
			//then
			assertThat(result.size()).isEqualTo(1);
			assertThat(result.get(0).getIssuedFrom()).isEqualTo("42Intra");
			assertThat(result.get(0).getUsedTo()).isEqualTo("NotUsed");
		}

		@Test
		@DisplayName("200 티켓 히스토리 조회 성공 - refund 되어있고 used 되어있지 않은 경우")
		void findTicketHistorySuccessToRefund() throws Exception {
			//given
			Agenda seoulAgenda = agendaFixture.createAgenda(SEOUL);
			Ticket ticket = ticketFixture.createTicket(agendaProfile, true, false, seoulAgenda.getAgendaKey(),
				null);
			PageRequestDto req = new PageRequestDto(1, 5);
			//when
			String res = mockMvc.perform(
					get("/agenda/admin/ticket/list/" + user.getIntraId())
						.header("Authorization", "Bearer " + accessToken)
						.param("page", String.valueOf(req.getPage()))
						.param("size", String.valueOf(req.getSize())))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			PageResponseDto<TicketHistoryResDto> pageResponseDto = objectMapper.readValue(res, new TypeReference<>() {
			});
			List<TicketHistoryResDto> result = pageResponseDto.getContent();
			//then
			assertThat(result.size()).isEqualTo(1);
			assertThat(result.get(0).getIssuedFrom()).isEqualTo(seoulAgenda.getTitle());
			assertThat(result.get(0).getUsedTo()).isEqualTo("NotUsed");
		}

		@Test
		@DisplayName("200 티켓이 없는 경우")
		void getAllTicketsEmpty() throws Exception {
			//Given
			PageRequestDto req = new PageRequestDto(1, 5);
			// When
			String res = mockMvc.perform(
					get("/agenda/admin/ticket/list/" + user.getIntraId())
						.header("Authorization", "Bearer " + accessToken)
						.param("page", "1")
						.param("size", "10")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			PageResponseDto<TicketFindResDto> pageResponseDto = objectMapper.readValue(res, new TypeReference<>() {
			});
			List<TicketFindResDto> result = pageResponseDto.getContent();

			// Then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("400 유효하지 않은 페이지 요청")
		void getAllTicketsInvalidPageRequest() throws Exception {
			// Given
			// When & Then
			mockMvc.perform(
					get("/agenda/admin/ticket/list/" + user.getIntraId())
						.header("Authorization", "Bearer " + accessToken)
						.param("page", "-1")
						.param("size", "10")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}
	}
}
