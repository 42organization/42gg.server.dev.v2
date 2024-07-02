package gg.agenda.api.user.integration;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.agenda.api.AgendaMockData;
import gg.data.agenda.Agenda;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class AgendaIntegrationTest {

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

	@Nested
	class GetAgenda {

		@Test
		@DisplayName("Agenda 단건 조회")
		void test() throws Exception {
			// given
			Agenda agenda = agendaMockData.createAgenda();

			// when
			String response = mockMvc.perform(get("/api/agenda")
					.param("agenda_id", agenda.getKey().toString()))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			Agenda result = objectMapper.readValue(response, Agenda.class);

			// then
			assertThat(result.getKey()).isEqualTo(agenda.getKey());
		}
	}
}
