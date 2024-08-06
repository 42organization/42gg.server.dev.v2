package gg.agenda.api.admin.agenda.controller;

import static gg.data.agenda.type.AgendaStatus.*;
import static gg.data.agenda.type.Location.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.agenda.api.AgendaMockData;
import gg.agenda.api.admin.agenda.controller.request.AgendaAdminUpdateReqDto;
import gg.agenda.api.admin.agenda.controller.response.AgendaAdminResDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.Location;
import gg.data.user.User;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.converter.MultiValueMapConverter;
import gg.utils.dto.PageRequestDto;
import gg.utils.file.handler.AwsImageHandler;
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
	AgendaAdminRepository agendaAdminRepository;

	@Value("${info.image.defaultUrl}")
	private String defaultUri;

	@MockBean
	private AwsImageHandler imageHandler;

	private User user;

	private String accessToken;

	@BeforeEach
	void setUp() {
		user = testDataUtils.createAdminUser();
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
			agendas.addAll(agendaMockData.createOfficialAgendaList(5, AgendaStatus.OPEN));
			agendas.addAll(agendaMockData.createOfficialAgendaList(5, AgendaStatus.FINISH));
			agendas.addAll(agendaMockData.createOfficialAgendaList(5, AgendaStatus.CANCEL));
			agendas.addAll(agendaMockData.createNonOfficialAgendaList(5, AgendaStatus.OPEN));
			agendas.addAll(agendaMockData.createNonOfficialAgendaList(5, AgendaStatus.FINISH));
			agendas.addAll(agendaMockData.createNonOfficialAgendaList(5, AgendaStatus.CANCEL));
			PageRequestDto pageRequestDto = new PageRequestDto(page, size);
			String request = objectMapper.writeValueAsString(pageRequestDto);

			// when
			String response = mockMvc.perform(get("/agenda/admin/request/list")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AgendaAdminResDto[] result = objectMapper.readValue(response, AgendaAdminResDto[].class);

			// then
			assertThat(result).hasSize(
				((page - 1) * size) < agendas.size() ? Math.min(size, agendas.size() - (page - 1) * size) : 0);
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
			String response = mockMvc.perform(get("/agenda/admin/request/list")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AgendaAdminResDto[] result = objectMapper.readValue(response, AgendaAdminResDto[].class);

			// then
			assertThat(result).isEmpty();
		}
	}

	@Nested
	@DisplayName("Admin Agenda 수정 및 삭제")
	class UpdateAgendaAdmin {

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 성공 - 기본 정보")
		void updateAgendaAdminSuccessWithInformation() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgendaWithTeam(10);
			AgendaAdminUpdateReqDto agendaDto =
				AgendaAdminUpdateReqDto.builder().agendaTitle("updated title").agendaContent("updated content")
					.agendaStatus(FINISH).isOfficial(!agenda.getIsOfficial())
					.isRanking(!agenda.getIsRanking()).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// when
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNoContent());
			Optional<Agenda> updated = agendaAdminRepository.findByAgendaKey(agenda.getAgendaKey());

			// then
			assert (updated.isPresent());
			assertThat(updated.get().getTitle()).isEqualTo(agendaDto.getAgendaTitle());
			assertThat(updated.get().getContent()).isEqualTo(agendaDto.getAgendaContent());
			assertThat(updated.get().getStatus()).isEqualTo(agendaDto.getAgendaStatus());
			assertThat(updated.get().getIsOfficial()).isEqualTo(agendaDto.getIsOfficial());
			assertThat(updated.get().getIsRanking()).isEqualTo(agendaDto.getIsRanking());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 성공 - 스케쥴 정보")
		void updateAgendaAdminSuccessWithSchedule() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgendaWithTeam(10);
			AgendaAdminUpdateReqDto agendaDto = AgendaAdminUpdateReqDto.builder()
				.agendaDeadLine(agenda.getDeadline().plusDays(1))
				.agendaStartTime(agenda.getStartTime().plusDays(1))
				.agendaEndTime(agenda.getEndTime().plusDays(1))
				.build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// when
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNoContent());
			Optional<Agenda> updated = agendaAdminRepository.findByAgendaKey(agenda.getAgendaKey());

			// then
			assert (updated.isPresent());
			assertThat(updated.get().getDeadline()).isEqualTo(agendaDto.getAgendaDeadLine());
			assertThat(updated.get().getStartTime()).isEqualTo(agendaDto.getAgendaStartTime());
			assertThat(updated.get().getEndTime()).isEqualTo(agendaDto.getAgendaEndTime());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 성공 - 서울 대회를 MIX로 변경")
		void updateAgendaAdminSuccessWithLocationSeoulToMix() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgendaWithTeam(10);    // SEOUL
			AgendaAdminUpdateReqDto agendaDto = AgendaAdminUpdateReqDto.builder().agendaLocation(MIX).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// when
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNoContent());
			Optional<Agenda> updated = agendaAdminRepository.findByAgendaKey(agenda.getAgendaKey());

			// then
			assert (updated.isPresent());
			assertThat(updated.get().getLocation()).isEqualTo(agendaDto.getAgendaLocation());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 성공 - 서울 대회를 경산으로 변경")
		void updateAgendaAdminSuccessWithLocationSeoulToGyeongsan() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgenda();
			AgendaAdminUpdateReqDto agendaDto = AgendaAdminUpdateReqDto.builder().agendaLocation(GYEONGSAN).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// when
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNoContent());
			Optional<Agenda> updated = agendaAdminRepository.findByAgendaKey(agenda.getAgendaKey());

			// then
			assert (updated.isPresent());
			assertThat(updated.get().getLocation()).isEqualTo(agendaDto.getAgendaLocation());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 성공 - 경산 대회를 서울로 변경")
		void updateAgendaAdminSuccessWithLocationGyeongsanToSeoul() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgenda(GYEONGSAN);
			AgendaAdminUpdateReqDto agendaDto = AgendaAdminUpdateReqDto.builder().agendaLocation(GYEONGSAN).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// when
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNoContent());
			Optional<Agenda> updated = agendaAdminRepository.findByAgendaKey(agenda.getAgendaKey());

			// then
			assert (updated.isPresent());
			assertThat(updated.get().getLocation()).isEqualTo(agendaDto.getAgendaLocation());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 성공 - 경산 대회를 MIX로 변경")
		void updateAgendaAdminSuccessWithLocationGyeongsanToMix() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgendaWithTeamGyeongsan(10);    // SEOUL
			AgendaAdminUpdateReqDto agendaDto = AgendaAdminUpdateReqDto.builder().agendaLocation(MIX).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// when
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNoContent());
			Optional<Agenda> updated = agendaAdminRepository.findByAgendaKey(agenda.getAgendaKey());

			// then
			assert (updated.isPresent());
			assertThat(updated.get().getLocation()).isEqualTo(agendaDto.getAgendaLocation());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 성공 - Agenda 팀 제한 정보")
		void updateAgendaAdminSuccessWithAgendaCapacity() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgendaWithTeam(10);
			AgendaAdminUpdateReqDto agendaDto = AgendaAdminUpdateReqDto.builder().agendaMinTeam(agenda.getMinTeam() + 1)
				.agendaMaxTeam(agenda.getMaxTeam() + 1).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// when
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNoContent());
			Optional<Agenda> updated = agendaAdminRepository.findByAgendaKey(agenda.getAgendaKey());

			// then
			assert (updated.isPresent());
			assertThat(updated.get().getMinTeam()).isEqualTo(agendaDto.getAgendaMinTeam());
			assertThat(updated.get().getMaxTeam()).isEqualTo(agendaDto.getAgendaMaxTeam());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 성공 - Agenda 팀 허용 인원 제한 정보")
		void updateAgendaAdminSuccessWithAgendaTeamCapacity() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgendaWithTeam(10);
			AgendaAdminUpdateReqDto agendaDto =
				AgendaAdminUpdateReqDto.builder().agendaMinPeople(agenda.getMinPeople() + 1)
					.agendaMaxPeople(agenda.getMaxPeople() + 1).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// when
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNoContent());
			Optional<Agenda> updated = agendaAdminRepository.findByAgendaKey(agenda.getAgendaKey());

			// then
			assert (updated.isPresent());
			assertThat(updated.get().getMinPeople()).isEqualTo(agendaDto.getAgendaMinPeople());
			assertThat(updated.get().getMaxPeople()).isEqualTo(agendaDto.getAgendaMaxPeople());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 실패 - 대회가 존재하지 않는 경우")
		void updateAgendaAdminFailedWithNoAgenda() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			AgendaAdminUpdateReqDto agendaDto = AgendaAdminUpdateReqDto.builder().build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// expected
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", UUID.randomUUID().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 실패 - 서울 대회를 경산으로 변경할 수 없는 경우")
		void updateAgendaAdminFailedWithLocationSeoulToGyeongSan() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgendaWithTeam(10);
			AgendaAdminUpdateReqDto agendaDto = AgendaAdminUpdateReqDto.builder().agendaLocation(GYEONGSAN).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// expected
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 실패 - 경산 대회를 서울 대회로 변경할 수 없는 경우")
		void updateAgendaAdminFailedWithLocationGyeongSanToSeoul() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgendaWithTeamGyeongsan(10);
			AgendaAdminUpdateReqDto agendaDto = AgendaAdminUpdateReqDto.builder().agendaLocation(SEOUL).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// expected
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isBadRequest());
		}

		@ParameterizedTest
		@EnumSource(value = Location.class, names = {"SEOUL", "GYEONGSAN"})
		@DisplayName("Admin Agenda 수정 및 삭제 실패 - 혼합 대회를 다른 지역 대회로 변경할 수 없는 경우")
		void updateAgendaAdminFailedWithLocationMixToSeoul() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgendaWithTeamMix(10);
			AgendaAdminUpdateReqDto agendaDto = AgendaAdminUpdateReqDto.builder().agendaLocation(SEOUL).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// expected
			mockMvc.perform(multipart("/agenda/admin/request")
					.params(params)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 실패 - 이미 maxTeam 이상의 팀이 존재하는 경우")
		void updateAgendaAdminFailedWithAgendaInvalidCapacity() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgendaWithTeamAndAgendaCapacity(10, 2, 10);
			AgendaAdminUpdateReqDto agendaDto =
				AgendaAdminUpdateReqDto.builder().agendaMinTeam(10).agendaMaxTeam(2).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// expected
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 실패 - 이미 maxTeam 이상의 팀이 존재하는 경우")
		void updateAgendaAdminFailedWithMaxTeam() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgendaWithTeamAndAgendaCapacity(10, 2, 10);
			AgendaAdminUpdateReqDto agendaDto = AgendaAdminUpdateReqDto.builder().agendaMinTeam(agenda.getMinTeam())
				.agendaMaxTeam(agenda.getMaxTeam() - 5).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// expected
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 실패 - 이미 종료한 대회에 minTeam 이하의 팀이 참여한 경우")
		void updateAgendaAdminFailedWithMinTeam() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgendaWithTeamAndAgendaCapacityAndFinish(5, 5, 10);
			AgendaAdminUpdateReqDto agendaDto = AgendaAdminUpdateReqDto.builder().agendaMinTeam(agenda.getMinTeam() + 2)
				.agendaMaxTeam((agenda.getMaxTeam())).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// expected
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 실패 - minPeople이 maxPeople보다 큰 경우")
		void updateAgendaAdminFailedWithAgendaTeamInvalidCapacity() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgendaWithTeamAndAgendaTeamCapacity(10, 2, 10);
			AgendaAdminUpdateReqDto agendaDto =
				AgendaAdminUpdateReqDto.builder().agendaMinPeople(10).agendaMaxPeople(2).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// expected
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 실패 - 이미 팀에 maxPeople 이상의 인원이 참여한 경우")
		void updateAgendaAdminFailedWithMaxPeople() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgendaWithTeamAndAgendaTeamCapacity(10, 2, 10);
			AgendaAdminUpdateReqDto agendaDto = AgendaAdminUpdateReqDto.builder().agendaMinPeople(agenda.getMinPeople())
				.agendaMaxPeople(agenda.getMaxPeople() - 5).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// expected
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin Agenda 수정 및 삭제 실패 - 이미 확정된 팀에 minPeople 이하의 인원이 참여한 경우")
		void updateAgendaAdminFailedWithMinPeople() throws Exception {
			// given
			URL mockUrl = new URL(defaultUri);
			Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(mockUrl);
			Agenda agenda = agendaMockData.createAgendaWithStatusAndTeamWithAgendaTeamCapacity(OPEN,
				10, 3, 10);
			AgendaAdminUpdateReqDto agendaDto =
				AgendaAdminUpdateReqDto.builder().agendaMinPeople(5).agendaMaxPeople(agenda.getMaxPeople()).build();
			MultiValueMap<String, String> params = MultiValueMapConverter.convert(objectMapper, agendaDto);

			// expected
			mockMvc.perform(multipart("/agenda/admin/request")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.params(params)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isBadRequest());
		}
	}
}
