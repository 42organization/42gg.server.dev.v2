package gg.agenda.api.user.agendaprofile;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gg.agenda.api.user.agendaprofile.controller.response.HostedAgendaResDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.type.AgendaStatus;
import gg.data.user.User;
import gg.utils.AgendaTestDataUtils;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.dto.PageResponseDto;
import gg.utils.fixture.agenda.AgendaFixture;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class AgendaHostControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestDataUtils testDataUtils;

	@Autowired
	private AgendaTestDataUtils agendaTestDataUtils;

	User user;

	String accessToken;

	@BeforeEach
	void beforeEach() {
		user = testDataUtils.createNewUser();
		accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
	}

	@Nested
	@DisplayName("내가 주최했던 Agenda 목록 조회")
	class HostedAgendaList {

		@ParameterizedTest
		@ValueSource(ints = {1, 2, 3, 4, 5})
		@DisplayName("내가 주최했던 Agenda 목록 조회 성공")
		void hostedAgendaListSuccess(int page) throws Exception {
			// given
			int size = 10;
			int eachCount = 10;
			List<Agenda> agendas = agendaTestDataUtils.createAgendasWithAllStatus(user, eachCount).stream()
				.filter(agenda ->
					agenda.getStatus() == AgendaStatus.FINISH || agenda.getStatus() == AgendaStatus.CANCEL)
				.sorted((a1, a2) -> a2.getId().compareTo(a1.getId()))
				.collect(Collectors.toList());

			// when
			String response = mockMvc.perform(get("/agenda/host/history/list")
					.header("Authorization", "Bearer " + accessToken)
					.param("page", String.valueOf(page))
					.param("size", String.valueOf(size)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			PageResponseDto<HostedAgendaResDto> pageResponseDto = objectMapper
				.readValue(response, new TypeReference<>() {
				});
			List<HostedAgendaResDto> result = pageResponseDto.getContent();

			// then
			assertThat(result.size()).isEqualTo(size * page <= agendas.size() ? size : agendas.size() % size);
			for (int i = 0; i < result.size(); i++) {
				assertThat(result.get(i).getAgendaTitle())
					.isEqualTo(agendas.get(size * (page - 1) + i).getTitle());
				assertThat(result.get(i).getAgendaStatus()).isNotEqualTo(AgendaStatus.OPEN);
				assertThat(result.get(i).getAgendaStatus()).isNotEqualTo(AgendaStatus.CONFIRM);
			}
		}
	}

	@Nested
	@DisplayName("내가 주최하고 있는 Agenda 목록 조회")
	class HostingAgendaList {

		@ParameterizedTest
		@ValueSource(ints = {1, 2, 3, 4, 5})
		@DisplayName("내가 주최하고 있는 Agenda 목록 조회 성공")
		void hostingAgendaListSuccess(int page) throws Exception {
			// given
			int size = 10;
			int eachCount = 10;
			List<Agenda> agendas = agendaTestDataUtils.createAgendasWithAllStatus(user, eachCount).stream()
				.filter(agenda ->
					agenda.getStatus() == AgendaStatus.OPEN || agenda.getStatus() == AgendaStatus.CONFIRM)
				.sorted((a1, a2) -> a2.getId().compareTo(a1.getId()))
				.collect(Collectors.toList());

			// when
			String response = mockMvc.perform(get("/agenda/host/history/list")
					.header("Authorization", "Bearer " + accessToken)
					.param("page", String.valueOf(page))
					.param("size", String.valueOf(size)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			PageResponseDto<HostedAgendaResDto> pageResponseDto = objectMapper
				.readValue(response, new TypeReference<>() {
				});
			List<HostedAgendaResDto> result = pageResponseDto.getContent();

			// then
			assertThat(result.size()).isEqualTo(size * page <= agendas.size() ? size : agendas.size() % size);
			for (int i = 0; i < result.size(); i++) {
				assertThat(result.get(i).getAgendaTitle())
					.isEqualTo(agendas.get(size * (page - 1) + i).getTitle());
				assertThat(result.get(i).getAgendaStatus()).isNotEqualTo(AgendaStatus.FINISH);
				assertThat(result.get(i).getAgendaStatus()).isNotEqualTo(AgendaStatus.CANCEL);
			}
		}
	}
}
