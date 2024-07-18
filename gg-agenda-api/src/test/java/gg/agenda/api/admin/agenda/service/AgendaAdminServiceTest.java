package gg.agenda.api.admin.agenda.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UnitTest
public class AgendaAdminServiceTest {

	@Mock
	AgendaAdminRepository agendaAdminRepository;

	@Mock
	AgendaTeamAdminRepository agendaTeamAdminRepository;

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

		@Test
		@DisplayName("Admin Agenda 수정 성공")
		void updateAgendaSuccessAdmin() {
			// given
			int teamCount = 3;
			List<AgendaTeam> teams = new ArrayList<>();
			for (int i = 0; i < teamCount; i++) {
				teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3).build());
			}
			Agenda agenda;
			{
				agenda = Agenda.builder()
					.title("title")
					.content("content")
					.posterUri("posterUri")
					.hostIntraId("hostIntraId")
					.deadline(LocalDateTime.now().minusDays(3))
					.startTime(LocalDateTime.now().plusDays(1))
					.endTime(LocalDateTime.now().plusDays(3))
					.isOfficial(true)
					.isRanking(true)
					.minTeam(5)
					.maxTeam(10)
					.currentTeam(teams.size())
					.minPeople(1)
					.maxPeople(10)
					.location(Location.SEOUL)
					.status(AgendaStatus.ON_GOING)
					.build();
			}
			AgendaAdminUpdateReqDto agendaDto;
			{
				agendaDto = AgendaAdminUpdateReqDto.builder()
					.agendaTitle("Updated title")
					.agendaContents("Updated content")
					.agendaPoster("Updated posterUri")
					.isOfficial(false)
					.isRanking(false)
					.agendaStatus(AgendaStatus.CONFIRM)
					.agendaDeadLine(LocalDateTime.now())
					.agendaStartTime(LocalDateTime.now().plusDays(3))
					.agendaEndTime(LocalDateTime.now().plusDays(5))
					.agendaLocation(Location.MIX)
					.agendaMinTeam(2)
					.agendaMaxTeam(20)
					.agendaMinPeople(2)
					.agendaMaxPeople(20)
					.build();
			}
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any())).thenReturn(teams);

			// when
			agendaAdminService.updateAgenda(agenda.getAgendaKey(), agendaDto);

			// then
			verify(agendaAdminRepository, times(1)).findByAgendaKey(any());
			verify(agendaTeamAdminRepository, times(1)).findAllByAgenda(any());
			{
				assertThat(agenda.getTitle()).isEqualTo(agendaDto.getAgendaTitle());
				assertThat(agenda.getContent()).isEqualTo(agendaDto.getAgendaContents());
				assertThat(agenda.getPosterUri()).isEqualTo(agendaDto.getAgendaPoster());
				assertThat(agenda.getIsOfficial()).isEqualTo(agendaDto.getIsOfficial());
				assertThat(agenda.getIsRanking()).isEqualTo(agendaDto.getIsRanking());
				assertThat(agenda.getStatus()).isEqualTo(agendaDto.getAgendaStatus());
				assertThat(agenda.getDeadline()).isEqualTo(agendaDto.getAgendaDeadLine());
				assertThat(agenda.getStartTime()).isEqualTo(agendaDto.getAgendaStartTime());
				assertThat(agenda.getEndTime()).isEqualTo(agendaDto.getAgendaEndTime());
				assertThat(agenda.getLocation()).isEqualTo(agendaDto.getAgendaLocation());
				assertThat(agenda.getMinTeam()).isEqualTo(agendaDto.getAgendaMinTeam());
				assertThat(agenda.getMaxTeam()).isEqualTo(agendaDto.getAgendaMaxTeam());
				assertThat(agenda.getMinPeople()).isEqualTo(agendaDto.getAgendaMinPeople());
				assertThat(agenda.getMaxPeople()).isEqualTo(agendaDto.getAgendaMaxPeople());
			}
		}

		@Test
		@DisplayName("Admin Agenda 수정 실패 - Agenda를 찾을 수 없음")
		void updateAgendaFailAdminWithNotExistAgenda() {
			// given
			AgendaAdminUpdateReqDto agendaDto = mock(AgendaAdminUpdateReqDto.class);
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.empty());

			// expected
			assertThrows(NotExistException.class,
				() -> agendaAdminService.updateAgenda(UUID.randomUUID(), agendaDto));
		}

		@Test
		@DisplayName("Admin Agenda 수정 실패 - Agenda 지역을 변경할 수 없음")
		void updateAgendaFailAdminWithCannotChangeLocation() {
			// given
			int teamCount = 3;
			List<AgendaTeam> teams = new ArrayList<>();
			for (int i = 0; i < teamCount; i++) {
				teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3).build());
			}    // SEOUL
			teams.add(AgendaTeam.builder().location(Location.GYEONGSAN).mateCount(3).build());    // GYEONGSAN
			Agenda agenda;    // MIX
			{
				agenda = Agenda.builder()
					.title("title")
					.content("content")
					.posterUri("posterUri")
					.hostIntraId("hostIntraId")
					.deadline(LocalDateTime.now().minusDays(3))
					.startTime(LocalDateTime.now().plusDays(1))
					.endTime(LocalDateTime.now().plusDays(3))
					.isOfficial(true)
					.isRanking(true)
					.minTeam(5)
					.maxTeam(10)
					.currentTeam(teams.size())
					.minPeople(1)
					.maxPeople(10)
					.location(Location.MIX)
					.status(AgendaStatus.ON_GOING)
					.build();
			}
			AgendaAdminUpdateReqDto agendaDto;    // SEOUL 변경 불가능
			{
				agendaDto = AgendaAdminUpdateReqDto.builder()
					.agendaTitle("Updated title")
					.agendaContents("Updated content")
					.agendaPoster("Updated posterUri")
					.isOfficial(false)
					.isRanking(false)
					.agendaStatus(AgendaStatus.CONFIRM)
					.agendaDeadLine(LocalDateTime.now())
					.agendaStartTime(LocalDateTime.now().plusDays(3))
					.agendaEndTime(LocalDateTime.now().plusDays(5))
					.agendaLocation(Location.SEOUL)
					.agendaMinTeam(2)
					.agendaMaxTeam(20)
					.agendaMinPeople(2)
					.agendaMaxPeople(20)
					.build();
			}
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any())).thenReturn(teams);

			// expected
			assertThrows(InvalidParameterException.class,
				() -> agendaAdminService.updateAgenda(agenda.getAgendaKey(), agendaDto));
		}

		@Test
		@DisplayName("Admin Agenda 수정 실패 - Agenda 팀 제한을 변경할 수 없음")
		void updateAgendaFailAdminWithCannotChangeTeamLimit() {
			// given
			int teamCount = 10;
			List<AgendaTeam> teams = new ArrayList<>();
			for (int i = 0; i < teamCount; i++) {
				teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3).build());
			}    // 10개 팀
			Agenda agenda;    // maxTeam = 10
			{
				agenda = Agenda.builder()
					.title("title")
					.content("content")
					.posterUri("posterUri")
					.hostIntraId("hostIntraId")
					.deadline(LocalDateTime.now().minusDays(3))
					.startTime(LocalDateTime.now().plusDays(1))
					.endTime(LocalDateTime.now().plusDays(3))
					.isOfficial(true)
					.isRanking(true)
					.minTeam(5)
					.maxTeam(10)
					.currentTeam(teams.size())
					.minPeople(1)
					.maxPeople(10)
					.location(Location.MIX)
					.status(AgendaStatus.ON_GOING)
					.build();
			}
			AgendaAdminUpdateReqDto agendaDto;    // maxTeam 5로 변경 불가능
			{
				agendaDto = AgendaAdminUpdateReqDto.builder()
					.agendaTitle("Updated title")
					.agendaContents("Updated content")
					.agendaPoster("Updated posterUri")
					.isOfficial(false)
					.isRanking(false)
					.agendaStatus(AgendaStatus.CONFIRM)
					.agendaDeadLine(LocalDateTime.now())
					.agendaStartTime(LocalDateTime.now().plusDays(3))
					.agendaEndTime(LocalDateTime.now().plusDays(5))
					.agendaLocation(Location.SEOUL)
					.agendaMinTeam(2)
					.agendaMaxTeam(5)
					.agendaMinPeople(2)
					.agendaMaxPeople(20)
					.build();
			}
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any())).thenReturn(teams);

			// expected
			assertThrows(InvalidParameterException.class,
				() -> agendaAdminService.updateAgenda(agenda.getAgendaKey(), agendaDto));
		}

		@Test
		@DisplayName("Admin Agenda 수정 실패 - Agenda 팀 인원 제한을 변경할 수 없음")
		void updateAgendaFailAdminWithCannotChangeMaxPeople() {
			// given
			int teamCount = 3;
			List<AgendaTeam> teams = new ArrayList<>();
			for (int i = 0; i < teamCount; i++) {
				teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3).build());
			}
			teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(10)
				.status(AgendaTeamStatus.CONFIRM).build());    // mateCount 10
			Agenda agenda;    // maxPeople = 10
			{
				agenda = Agenda.builder()
					.title("title")
					.content("content")
					.posterUri("posterUri")
					.hostIntraId("hostIntraId")
					.deadline(LocalDateTime.now().minusDays(3))
					.startTime(LocalDateTime.now().plusDays(1))
					.endTime(LocalDateTime.now().plusDays(3))
					.isOfficial(true)
					.isRanking(true)
					.minTeam(5)
					.maxTeam(10)
					.currentTeam(teams.size())
					.minPeople(1)
					.maxPeople(10)
					.location(Location.MIX)
					.status(AgendaStatus.ON_GOING)
					.build();
			}
			AgendaAdminUpdateReqDto agendaDto;    // maxPeople 5로 변경 불가능
			{
				agendaDto = AgendaAdminUpdateReqDto.builder()
					.agendaTitle("Updated title")
					.agendaContents("Updated content")
					.agendaPoster("Updated posterUri")
					.isOfficial(false)
					.isRanking(false)
					.agendaStatus(AgendaStatus.CONFIRM)
					.agendaDeadLine(LocalDateTime.now())
					.agendaStartTime(LocalDateTime.now().plusDays(3))
					.agendaEndTime(LocalDateTime.now().plusDays(5))
					.agendaLocation(Location.SEOUL)
					.agendaMinTeam(2)
					.agendaMaxTeam(20)
					.agendaMinPeople(2)
					.agendaMaxPeople(5)    // here
					.build();
			}
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any())).thenReturn(teams);

			// expected
			assertThrows(InvalidParameterException.class,
				() -> agendaAdminService.updateAgenda(agenda.getAgendaKey(), agendaDto));
		}

		@Test
		@DisplayName("Admin Agenda 수정 실패 - Agenda 팀 인원 제한을 변경할 수 없음")
		void updateAgendaFailAdminWithCannotChangeMinPeople() {
			// given
			int teamCount = 3;
			List<AgendaTeam> teams = new ArrayList<>();
			for (int i = 0; i < teamCount; i++) {
				teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3).build());
			}
			teams.add(AgendaTeam.builder().location(Location.SEOUL).mateCount(3)
				.status(AgendaTeamStatus.CONFIRM).build());    // mateCount 3 of CONFIRM Team
			Agenda agenda;    // minPeople = 1
			{
				agenda = Agenda.builder()
					.title("title")
					.content("content")
					.posterUri("posterUri")
					.hostIntraId("hostIntraId")
					.deadline(LocalDateTime.now().minusDays(3))
					.startTime(LocalDateTime.now().plusDays(1))
					.endTime(LocalDateTime.now().plusDays(3))
					.isOfficial(true)
					.isRanking(true)
					.minTeam(5)
					.maxTeam(10)
					.currentTeam(teams.size())
					.minPeople(1)
					.maxPeople(10)
					.location(Location.MIX)
					.status(AgendaStatus.ON_GOING)
					.build();
			}
			AgendaAdminUpdateReqDto agendaDto;    // minPeople 5로 변경 불가능
			{
				agendaDto = AgendaAdminUpdateReqDto.builder()
					.agendaTitle("Updated title")
					.agendaContents("Updated content")
					.agendaPoster("Updated posterUri")
					.isOfficial(false)
					.isRanking(false)
					.agendaStatus(AgendaStatus.CONFIRM)
					.agendaDeadLine(LocalDateTime.now())
					.agendaStartTime(LocalDateTime.now().plusDays(3))
					.agendaEndTime(LocalDateTime.now().plusDays(5))
					.agendaLocation(Location.SEOUL)
					.agendaMinTeam(2)
					.agendaMaxTeam(20)
					.agendaMinPeople(5)    // here
					.agendaMaxPeople(20)
					.build();
			}
			when(agendaAdminRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any())).thenReturn(teams);

			// expected
			assertThrows(InvalidParameterException.class,
				() -> agendaAdminService.updateAgenda(agenda.getAgendaKey(), agendaDto));
		}
	}
}
