package gg.agenda.api.admin.agenda.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaTeamAdminRepository;
import gg.agenda.api.admin.agenda.controller.request.AgendaAdminUpdateReqDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.data.agenda.type.Location;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.custom.InvalidParameterException;
import gg.utils.exception.custom.NotExistException;
import gg.utils.file.handler.AwsImageHandler;

@UnitTest
public class AgendaAdminServiceTest {

	@Mock
	AgendaAdminRepository agendaAdminRepository;

	@Mock
	AgendaTeamAdminRepository agendaTeamAdminRepository;

	@Mock
	AwsImageHandler imageHandler;

	@InjectMocks
	AgendaAdminService agendaAdminService;

	@Nested
	@DisplayName("Admin Agenda 상세 조회")
	class GetAgendaAdmin {

		@Test
		@DisplayName("Admin Agenda 상세 조회 성공")
		void findAgendaByAgendaKeySuccessAdmin() {
			// given
			Pageable pageable = mock(Pageable.class);
			List<Agenda> agendas = new ArrayList<>();
			agendas.add(Agenda.builder().build());
			Page<Agenda> page = new PageImpl<>(agendas);
			when(agendaAdminRepository.findAll(pageable)).thenReturn(page);

			// when
			List<Agenda> result = agendaAdminService.getAgendaRequestList(pageable);

			// then
			verify(agendaAdminRepository, times(1)).findAll(pageable);
			assertThat(result).isNotEmpty();
		}

		@Test
		@DisplayName("Admin Agenda 상세 조회 성공 - 빈 리스트인 경우")
		void findAgendaByAgendaKeySuccessAdminWithNoContent() {
			// given
			Pageable pageable = mock(Pageable.class);
			Page<Agenda> page = new PageImpl<>(List.of());
			when(agendaAdminRepository.findAll(pageable)).thenReturn(page);

			// when
			List<Agenda> result = agendaAdminService.getAgendaRequestList(pageable);

			// then
			verify(agendaAdminRepository, times(1)).findAll(pageable);
			assertThat(result).isEmpty();
		}
	}

	@Nested
	@DisplayName("Admin Agenda 수정")
	class UpdateAgenda {

		private final String defaultUrl = "http://localhost:8080/images/image.jpeg";

		@Test
		@DisplayName("Admin Agenda 수정 성공 - 기본 정보")
		void updateAgendaSuccessWithInformation() throws IOException {
			// given
			MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
				"image/jpeg", "test".getBytes());
			int teamCount = 3;
			List<AgendaTeam> teams = new ArrayList<>();
			for (int i = 0; i < teamCount; i++) {
				teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3).build());
			}
			Agenda agenda = Agenda.builder().title("title").content("content").posterUri("posterUri").isOfficial(false)
				.isRanking(true).status(AgendaStatus.FINISH).build();
			AgendaAdminUpdateReqDto agendaDto =
				AgendaAdminUpdateReqDto.builder().agendaTitle("Updated title").agendaContent("Updated content")
					.isOfficial(true).isRanking(true)
					.agendaStatus(AgendaStatus.CANCEL).build();
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any())).thenReturn(teams);
			when(imageHandler.uploadImageOrDefault(any(), any(), any())).thenReturn(new URL(defaultUrl));

			// when
			agendaAdminService.updateAgenda(agenda.getAgendaKey(), agendaDto, file);

			// then
			verify(agendaAdminRepository, times(1)).findByAgendaKey(any());
			verify(agendaTeamAdminRepository, times(1)).findAllByAgenda(any());
			assertThat(agenda.getTitle()).isEqualTo(agendaDto.getAgendaTitle());
			assertThat(agenda.getContent()).isEqualTo(agendaDto.getAgendaContent());
			assertThat(agenda.getPosterUri()).isEqualTo(defaultUrl);
			assertThat(agenda.getIsOfficial()).isEqualTo(agendaDto.getIsOfficial());
			assertThat(agenda.getIsRanking()).isEqualTo(agendaDto.getIsRanking());
			assertThat(agenda.getStatus()).isEqualTo(agendaDto.getAgendaStatus());
		}

		@Test
		@DisplayName("Admin Agenda 수정 성공 - 스케줄 정보")
		void updateAgendaSuccessWithSchedule() throws IOException {
			// given
			MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
				"image/jpeg", "test".getBytes());
			int teamCount = 3;
			List<AgendaTeam> teams = new ArrayList<>();
			for (int i = 0; i < teamCount; i++) {
				teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3).build());
			}
			Agenda agenda =
				Agenda.builder().deadline(LocalDateTime.now().minusDays(3)).startTime(LocalDateTime.now().plusDays(1))
					.endTime(LocalDateTime.now().plusDays(3)).build();
			AgendaAdminUpdateReqDto agendaDto = AgendaAdminUpdateReqDto.builder().agendaDeadLine(LocalDateTime.now())
				.agendaStartTime(LocalDateTime.now().plusDays(3)).agendaEndTime(LocalDateTime.now().plusDays(5))
				.build();
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any())).thenReturn(teams);
			when(imageHandler.uploadImageOrDefault(any(), any(), any())).thenReturn(new URL(defaultUrl));

			// when
			agendaAdminService.updateAgenda(agenda.getAgendaKey(), agendaDto, file);

			// then
			verify(agendaAdminRepository, times(1)).findByAgendaKey(any());
			verify(agendaTeamAdminRepository, times(1)).findAllByAgenda(any());
			assertThat(agenda.getDeadline()).isEqualTo(agendaDto.getAgendaDeadLine());
			assertThat(agenda.getStartTime()).isEqualTo(agendaDto.getAgendaStartTime());
			assertThat(agenda.getEndTime()).isEqualTo(agendaDto.getAgendaEndTime());
		}

		@Test
		@DisplayName("Admin Agenda 수정 성공 - 지역 정보")
		void updateAgendaSuccessWithLocation() throws IOException {
			// given
			MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
				"image/jpeg", "test".getBytes());
			int teamCount = 3;
			List<AgendaTeam> teams = new ArrayList<>();
			for (int i = 0; i < teamCount; i++) {
				teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3).build());
			}
			Agenda agenda = Agenda.builder().location(Location.SEOUL).build();
			AgendaAdminUpdateReqDto agendaDto = AgendaAdminUpdateReqDto.builder().agendaLocation(Location.MIX).build();
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any())).thenReturn(teams);
			when(imageHandler.uploadImageOrDefault(any(), any(), any())).thenReturn(new URL(defaultUrl));

			// when
			agendaAdminService.updateAgenda(agenda.getAgendaKey(), agendaDto, file);

			// then
			verify(agendaAdminRepository, times(1)).findByAgendaKey(any());
			verify(agendaTeamAdminRepository, times(1)).findAllByAgenda(any());
			assertThat(agenda.getLocation()).isEqualTo(agendaDto.getAgendaLocation());
		}

		@Test
		@DisplayName("Admin Agenda 수정 성공 - Agenda 팀 제한 정보")
		void updateAgendaSuccessWithAgendaCapacity() throws IOException {
			// given
			MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
				"image/jpeg", "test".getBytes());
			int teamCount = 3;
			List<AgendaTeam> teams = new ArrayList<>();
			for (int i = 0; i < teamCount; i++) {
				teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3).build());
			}
			Agenda agenda = Agenda.builder().minTeam(5).maxTeam(10).build();
			AgendaAdminUpdateReqDto agendaDto =
				AgendaAdminUpdateReqDto.builder().agendaMinTeam(2).agendaMaxTeam(20).build();
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any())).thenReturn(teams);
			when(imageHandler.uploadImageOrDefault(any(), any(), any())).thenReturn(new URL(defaultUrl));

			// when
			agendaAdminService.updateAgenda(agenda.getAgendaKey(), agendaDto, file);

			// then
			verify(agendaAdminRepository, times(1)).findByAgendaKey(any());
			verify(agendaTeamAdminRepository, times(1)).findAllByAgenda(any());
			assertThat(agenda.getMinTeam()).isEqualTo(agendaDto.getAgendaMinTeam());
			assertThat(agenda.getMaxTeam()).isEqualTo(agendaDto.getAgendaMaxTeam());
		}

		@Test
		@DisplayName("Admin Agenda 수정 성공 - Agenda 팀 인원 제한 정보")
		void updateAgendaSuccessWithAgendaTeamCapacity() throws IOException {
			// given
			MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
				"image/jpeg", "test".getBytes());
			int teamCount = 3;
			List<AgendaTeam> teams = new ArrayList<>();
			for (int i = 0; i < teamCount; i++) {
				teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3).build());
			}
			Agenda agenda = Agenda.builder().minPeople(1).maxPeople(10).build();
			AgendaAdminUpdateReqDto agendaDto =
				AgendaAdminUpdateReqDto.builder().agendaMinPeople(2).agendaMaxPeople(20).build();
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any())).thenReturn(teams);
			when(imageHandler.uploadImageOrDefault(any(), any(), any())).thenReturn(new URL(defaultUrl));

			// when
			agendaAdminService.updateAgenda(agenda.getAgendaKey(), agendaDto, file);

			// then
			verify(agendaAdminRepository, times(1)).findByAgendaKey(any());
			verify(agendaTeamAdminRepository, times(1)).findAllByAgenda(any());
			assertThat(agenda.getMinTeam()).isEqualTo(agendaDto.getAgendaMinTeam());
			assertThat(agenda.getMaxTeam()).isEqualTo(agendaDto.getAgendaMaxTeam());
		}

		@Test
		@DisplayName("Admin Agenda 수정 실패 - Agenda를 찾을 수 없음")
		void updateAgendaFailAdminWithNotExistAgenda() {
			// given
			MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
				"image/jpeg", "test".getBytes());
			AgendaAdminUpdateReqDto agendaDto = mock(AgendaAdminUpdateReqDto.class);
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.empty());

			// expected
			assertThrows(NotExistException.class,
				() -> agendaAdminService.updateAgenda(UUID.randomUUID(), agendaDto, file));
		}

		@Test
		@DisplayName("Admin Agenda 수정 실패 - Agenda 지역을 변경할 수 없음")
		void updateAgendaFailAdminWithCannotChangeLocation() throws IOException {
			// given
			MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
				"image/jpeg", "test".getBytes());
			int teamCount = 3;
			List<AgendaTeam> teams = new ArrayList<>();
			for (int i = 0; i < teamCount; i++) {
				teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3).build());
			}    // SEOUL
			teams.add(AgendaTeam.builder().location(Location.GYEONGSAN).mateCount(3).build());    // GYEONGSAN
			Agenda agenda = Agenda.builder().currentTeam(teams.size()).location(Location.MIX).build();
			AgendaAdminUpdateReqDto agendaDto =
				AgendaAdminUpdateReqDto.builder().agendaLocation(Location.SEOUL).build();
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any())).thenReturn(teams);
			when(imageHandler.uploadImageOrDefault(any(), any(), any())).thenReturn(new URL(defaultUrl));

			// expected
			assertThrows(InvalidParameterException.class,
				() -> agendaAdminService.updateAgenda(agenda.getAgendaKey(), agendaDto, file));
		}

		@Test
		@DisplayName("Admin Agenda 수정 실패 - Agenda 팀 제한을 변경할 수 없음")
		void updateAgendaFailAdminWithCannotChangeMinTeam() throws IOException {
			// given
			MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
				"image/jpeg", "test".getBytes());
			int teamCount = 5;
			List<AgendaTeam> teams = new ArrayList<>();
			for (int i = 0; i < teamCount; i++) {
				teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3).build());
			}    // 10개 팀
			Agenda agenda =
				Agenda.builder().currentTeam(teams.size()).minTeam(5).maxTeam(10).status(AgendaStatus.FINISH).build();
			AgendaAdminUpdateReqDto agendaDto =
				AgendaAdminUpdateReqDto.builder().agendaMinTeam(10).agendaMaxTeam(20).build();
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any())).thenReturn(teams);
			when(imageHandler.uploadImageOrDefault(any(), any(), any())).thenReturn(new URL(defaultUrl));

			// expected
			assertThrows(InvalidParameterException.class,
				() -> agendaAdminService.updateAgenda(agenda.getAgendaKey(), agendaDto, file));
		}

		@Test
		@DisplayName("Admin Agenda 수정 실패 - Agenda 팀 제한을 변경할 수 없음")
		void updateAgendaFailAdminWithCannotChangeMaxTeam() throws IOException {
			// given
			MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
				"image/jpeg", "test".getBytes());
			int teamCount = 10;
			List<AgendaTeam> teams = new ArrayList<>();
			for (int i = 0; i < teamCount; i++) {
				teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3).build());
			}    // 10개 팀
			Agenda agenda = Agenda.builder().currentTeam(teams.size()).minTeam(5).maxTeam(10).build();
			AgendaAdminUpdateReqDto agendaDto =
				AgendaAdminUpdateReqDto.builder().agendaMinTeam(2).agendaMaxTeam(5).build();
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any())).thenReturn(teams);
			when(imageHandler.uploadImageOrDefault(any(), any(), any())).thenReturn(new URL(defaultUrl));

			// expected
			assertThrows(InvalidParameterException.class,
				() -> agendaAdminService.updateAgenda(agenda.getAgendaKey(), agendaDto, file));
		}

		@Test
		@DisplayName("Admin Agenda 수정 실패 - Agenda 팀 인원 제한을 변경할 수 없음")
		void updateAgendaFailAdminWithCannotChangeMaxPeople() throws IOException {
			// given
			MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
				"image/jpeg", "test".getBytes());
			int teamCount = 3;
			List<AgendaTeam> teams = new ArrayList<>();
			for (int i = 0; i < teamCount; i++) {
				teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3).build());
			}
			teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(10).status(AgendaTeamStatus.CONFIRM)
				.build());    // mateCount 10
			Agenda agenda = Agenda.builder().currentTeam(teams.size()).minPeople(1).maxPeople(10).build();
			AgendaAdminUpdateReqDto agendaDto =
				AgendaAdminUpdateReqDto.builder().agendaMinPeople(2).agendaMaxPeople(5).build();
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any())).thenReturn(teams);
			when(imageHandler.uploadImageOrDefault(any(), any(), any())).thenReturn(new URL(defaultUrl));

			// expected
			assertThrows(InvalidParameterException.class,
				() -> agendaAdminService.updateAgenda(agenda.getAgendaKey(), agendaDto, file));
		}

		@Test
		@DisplayName("Admin Agenda 수정 실패 - Agenda 팀 인원 제한을 변경할 수 없음")
		void updateAgendaFailAdminWithCannotChangeMinPeople() throws IOException {
			// given
			MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
				"image/jpeg", "test".getBytes());
			int teamCount = 3;
			List<AgendaTeam> teams = new ArrayList<>();
			for (int i = 0; i < teamCount; i++) {
				teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3).build());
			}
			teams.add(
				AgendaTeam.builder().location(Location.SEOUL).mateCount(3).status(AgendaTeamStatus.CONFIRM).build());
			Agenda agenda = Agenda.builder().currentTeam(teams.size()).minPeople(1).maxPeople(10).build();
			AgendaAdminUpdateReqDto agendaDto =
				AgendaAdminUpdateReqDto.builder().agendaMinPeople(5).agendaMaxPeople(20).build();
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any())).thenReturn(teams);
			when(imageHandler.uploadImageOrDefault(any(), any(), any())).thenReturn(new URL(defaultUrl));

			// expected
			assertThrows(InvalidParameterException.class,
				() -> agendaAdminService.updateAgenda(agenda.getAgendaKey(), agendaDto, file));
		}
	}
}
