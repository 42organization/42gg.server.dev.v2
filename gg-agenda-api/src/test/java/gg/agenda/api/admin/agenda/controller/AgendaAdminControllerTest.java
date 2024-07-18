package gg.agenda.api.admin.agenda.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.agenda.api.AgendaMockData;
import gg.agenda.api.admin.agenda.controller.response.AgendaAdminResDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.type.AgendaStatus;
import gg.data.user.User;
import gg.repo.agenda.AgendaRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.dto.PageRequestDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class AgendaAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestDataUtils testDataUtils;

	@Autowired
	private AgendaMockData agendaMockData;

	@Autowired
	EntityManager em;

	@Autowired
	AgendaRepository agendaRepository;

	private User user;

	private String accessToken;

	@BeforeEach
	void setUp() {
		user = testDataUtils.createNewUser();
		accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
	}

	@Nested
	@DisplayName("Admin Agenda 상세 조회")
	class GetAgendaAdmin {

		@ParameterizedTest
		@ValueSource(ints = {1, 2, 3, 4, 5})
		@DisplayName("Admin Agenda 상세 조회 성공")
		void findAgendaByAgendaKeySuccessAdmin(int page) throws Exception {
			// given
			int size = 10;
			List<Agenda> agendas = new ArrayList<>();
			agendas.addAll(agendaMockData.createOfficialAgendaList(5, AgendaStatus.ON_GOING));
			agendas.addAll(agendaMockData.createOfficialAgendaList(5, AgendaStatus.CONFIRM));
			agendas.addAll(agendaMockData.createOfficialAgendaList(5, AgendaStatus.CANCEL));
			agendas.addAll(agendaMockData.createNonOfficialAgendaList(5, AgendaStatus.ON_GOING));
			agendas.addAll(agendaMockData.createNonOfficialAgendaList(5, AgendaStatus.CONFIRM));
			agendas.addAll(agendaMockData.createNonOfficialAgendaList(5, AgendaStatus.CANCEL));
			PageRequestDto pageRequestDto = new PageRequestDto(page, size);
			String request = objectMapper.writeValueAsString(pageRequestDto);

			// when
			String response = mockMvc.perform(get("/admin/agenda/request/list")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AgendaAdminResDto[] result = objectMapper.readValue(response, AgendaAdminResDto[].class);

			// then
			assertThat(result).hasSize(((page - 1) * size) < agendas.size()
					? Math.min(size, agendas.size() - (page - 1) * size) : 0);
			agendas.sort((a, b) -> b.getId().compareTo(a.getId()));
			for (int i = 0; i < result.length; i++) {
				assertThat(result[i].getAgendaId()).isEqualTo(agendas.get(i + (page - 1) * size).getId());
			}
		}

		@Test
		@DisplayName("Admin Agenda 상세 조회 성공 - 대회가 존재하지 않는 경우")
		void findAgendaByAgendaKeySuccessAdminWithNoContent() throws Exception {
			// given
			int page = 1;
			int size = 10;
			PageRequestDto pageRequestDto = new PageRequestDto(page, size);
			String request = objectMapper.writeValueAsString(pageRequestDto);

			// when
			String response = mockMvc.perform(get("/admin/agenda/request/list")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AgendaAdminResDto[] result = objectMapper.readValue(response, AgendaAdminResDto[].class);

			// then
			assertThat(result).isEmpty();
		}
	}

	@Nested
	@DisplayName("Admin Agenda 수정 맟 삭제")
	class UpdateAgendaAdmin {

		@Test
		@DisplayName("Admin Agenda 수정 맟 삭제 성공")
		void updateAgendaAdminSuccess() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("Admin Agenda 수정 맟 삭제 실패 - 대회가 존재하지 않는 경우")
		void updateAgendaAdminFailedWithNoAgenda() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("Admin Agenda 수정 맟 삭제 실패 - 이미 다른 Location이 존재하는 경우")
		void updateAgendaAdminFailedWithLocation() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("Admin Agenda 수정 맟 삭제 실패 - 이미 maxTeam 이상의 팀이 존재하는 경우")
		void updateAgendaAdminFailedWithMaxTeam() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("Admin Agenda 수정 맟 삭제 실패 - 이미 확정된 대회에 minTeam 이하의 팀이 참여한 경우")
		void updateAgendaAdminFailedWithMinTeam() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("Admin Agenda 수정 맟 삭제 실패 - 이미 팀에 maxPeople 이상의 인원이 참여한 경우")
		void updateAgendaAdminFailedWithMaxPeople() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("Admin Agenda 수정 맟 삭제 실패 - 이미 확정된 팀에 minPeople 이하의 인원이 참여한 경우")
		void updateAgendaAdminFailedWithMinPeople() {
			// given
			// when
			// then
		}
	}
}
