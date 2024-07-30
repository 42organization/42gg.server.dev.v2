package gg.agenda.api.admin.agendateam.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaProfileAdminRepository;
import gg.admin.repo.agenda.AgendaTeamAdminRepository;
import gg.admin.repo.agenda.AgendaTeamProfileAdminRepository;
import gg.agenda.api.admin.agendateam.controller.request.AgendaTeamMateReqDto;
import gg.agenda.api.admin.agendateam.controller.request.AgendaTeamUpdateDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.custom.NotExistException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UnitTest
public class AgendaTeamAdminServiceTest {

	@Mock
	private AgendaAdminRepository agendaAdminRepository;

	@Mock
	private AgendaTeamAdminRepository agendaTeamAdminRepository;

	@Mock
	private AgendaProfileAdminRepository agendaProfileAdminRepository;

	@Mock
	private AgendaTeamProfileAdminRepository agendaTeamProfileAdminRepository;

	@InjectMocks
	private AgendaTeamAdminService agendaTeamAdminService;

	@Nested
	@DisplayName("Admin AgendaTeam 전체 조회")
	class GetAgendaTeamListAdmin {

		@Test
		@DisplayName("Admin AgendaTeam 전체 조회 성공")
		void getAgendaTeamListAdminSuccess() {
			// given
			Agenda agenda = Agenda.builder().build();
			List<AgendaTeam> announcements = new ArrayList<>();
			Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
			when(agendaAdminRepository.findByAgendaKey(any(UUID.class))).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any(Agenda.class), any(Pageable.class)))
				.thenReturn(new PageImpl<>(announcements));

			// when
			List<AgendaTeam> result = agendaTeamAdminService.getAgendaTeamList(agenda.getAgendaKey(), pageable);

			// then
			verify(agendaAdminRepository, times(1)).findByAgendaKey(any(UUID.class));
			verify(agendaTeamAdminRepository, times(1))
				.findAllByAgenda(any(Agenda.class), any(Pageable.class));
			assertThat(result).isNotNull();
		}

		@Test
		@DisplayName("Admin AgendaTeam 전체 조회 실패 - Agenda 없음")
		void getAgendaTeamListAdminFailedWithNoAgenda() {
			// given
			Pageable pageable = mock(Pageable.class);
			when(agendaAdminRepository.findByAgendaKey(any(UUID.class))).thenReturn(Optional.empty());

			// expected
			assertThrows(NotExistException.class,
				() -> agendaTeamAdminService.getAgendaTeamList(UUID.randomUUID(), pageable));
		}
	}

	@Nested
	@DisplayName("Admin AgendaTeam Team Key로 조회")
	class GetAgendaTeamByTeamKey {

		@Test
		@DisplayName("Admin AgendaTeam Team Key로 조회 성공")
		void getAgendaTeamByTeamKeySuccess() {
			// given
			UUID teamKey = UUID.randomUUID();
			AgendaTeam agendaTeam = mock(AgendaTeam.class);
			when(agendaTeamAdminRepository.findByTeamKey(teamKey)).thenReturn(Optional.of(agendaTeam));

			// when
			AgendaTeam agendaTeamByTeamKey = agendaTeamAdminService.getAgendaTeamByTeamKey(teamKey);

			// then
			verify(agendaTeamAdminRepository, times(1)).findByTeamKey(teamKey);
			assertThat(agendaTeamByTeamKey).isNotNull();
		}

		@Test
		@DisplayName("Admin AgendaTeam 상세 조회 실패 - Team Key 없음")
		void getAgendaTeamByTeamKeyFailedWithNoTeam() {
			// given
			UUID teamKey = UUID.randomUUID();
			when(agendaTeamAdminRepository.findByTeamKey(teamKey)).thenReturn(Optional.empty());

			// expected
			assertThrows(NotExistException.class,
				() -> agendaTeamAdminService.getAgendaTeamByTeamKey(teamKey));
		}
	}

	@Nested
	@DisplayName("Admin AgendaTeam teamMates 조회")
	class GetAgendaProfileListByAgendaTeam {

		@Test
		@DisplayName("Admin AgendaTeam teamMates 조회 성공")
		void getAgendaProfileListByAgendaTeamSuccess() {
			// given
			AgendaTeam agendaTeam = mock(AgendaTeam.class);
			List<AgendaTeamProfile> participants = new ArrayList<>();
			for (int i = 0; i < 5; i++) {
				AgendaProfile profile = AgendaProfile.builder().build();
				AgendaTeamProfile participant = AgendaTeamProfile.builder()
					.profile(profile).build();
				participants.add(participant);
			}
			when(agendaTeamProfileAdminRepository.findAllByAgendaTeamAndIsExistIsTrue(agendaTeam))
				.thenReturn(participants);

			// when
			List<AgendaProfile> result = agendaTeamAdminService.getAgendaProfileListByAgendaTeam(agendaTeam);

			// then
			verify(agendaTeamProfileAdminRepository, times(1))
				.findAllByAgendaTeamAndIsExistIsTrue(agendaTeam);
			assertThat(result).isNotNull();
		}

		@Test
		@DisplayName("Admin AgendaTeam teamMates 조회 성공 - teamMates 없는 경우 빈 리스트 반환")
		void getAgendaProfileListByAgendaTeamFailedWithNoTeam() {
			// given
			AgendaTeam agendaTeam = mock(AgendaTeam.class);
			List<AgendaTeamProfile> participants = new ArrayList<>();
			when(agendaTeamProfileAdminRepository.findAllByAgendaTeamAndIsExistIsTrue(agendaTeam))
				.thenReturn(participants);

			// when
			List<AgendaProfile> result = agendaTeamAdminService.getAgendaProfileListByAgendaTeam(agendaTeam);

			// then
			verify(agendaTeamProfileAdminRepository, times(1))
				.findAllByAgendaTeamAndIsExistIsTrue(agendaTeam);
			assertThat(result).isNotNull();
			assertThat(result).isEmpty();

		}
	}

	@Nested
	@DisplayName("Admin AgendaTeam 수정")
	class UpdateAgendaTeamAdmin {

		@Test
		@DisplayName("Admin AgendaTeam 수정 성공")
		void updateAgendaTeamAdminSuccess() {
			// given
			Agenda agenda = Agenda.builder().build();
			AgendaTeam team = AgendaTeam.builder().teamKey(UUID.randomUUID()).agenda(agenda).build();
			AgendaProfile profile = AgendaProfile.builder().intraId("intra").build();
			AgendaTeamProfile participant = AgendaTeamProfile.builder()
				.agendaTeam(team).agenda(agenda).profile(profile).build();
			AgendaTeamMateReqDto agendaTeamMateReqDto = AgendaTeamMateReqDto.builder()
				.intraId("intra").build();
			AgendaTeamUpdateDto agendaTeamUpdateDto = AgendaTeamUpdateDto.builder()
				.teamKey(team.getTeamKey()).teamMates(List.of(agendaTeamMateReqDto)).build();
			when(agendaTeamAdminRepository.findByTeamKey(any(UUID.class))).thenReturn(Optional.of(team));
			when(agendaTeamProfileAdminRepository.findAllByAgendaTeamAndIsExistIsTrue(any(AgendaTeam.class)))
				.thenReturn(List.of(participant));

			// when
			agendaTeamAdminService.updateAgendaTeam(agendaTeamUpdateDto);

			// then
			verify(agendaTeamAdminRepository, times(1))
				.findByTeamKey(any(UUID.class));
			verify(agendaTeamProfileAdminRepository, times(1))
				.findAllByAgendaTeamAndIsExistIsTrue(any(AgendaTeam.class));
		}

		@Test
		@DisplayName("Admin AgendaTeam 수정 성공 - 팀원 추가하기")
		void updateAgendaTeamAdminSuccessWithAddTeammate() {
			// given
			Agenda agenda = Agenda.builder().maxPeople(10).build();
			AgendaTeam team = AgendaTeam.builder().teamKey(UUID.randomUUID()).agenda(agenda).build();
			AgendaProfile profile = AgendaProfile.builder().intraId("intra").build();
			AgendaTeamProfile participant = AgendaTeamProfile.builder()
				.agendaTeam(team).agenda(agenda).profile(profile).build();

			AgendaProfile newProfile = AgendaProfile.builder().intraId("newIntra").build();
			List<AgendaTeamMateReqDto> updateTeamMates = new ArrayList<>();
			updateTeamMates.add(AgendaTeamMateReqDto.builder()
				.intraId(profile.getIntraId()).build());
			updateTeamMates.add(AgendaTeamMateReqDto.builder()
				.intraId(newProfile.getIntraId()).build());

			AgendaTeamUpdateDto agendaTeamUpdateDto = AgendaTeamUpdateDto.builder()
				.teamKey(team.getTeamKey()).teamMates(updateTeamMates).build();

			when(agendaTeamAdminRepository.findByTeamKey(any(UUID.class))).thenReturn(Optional.of(team));
			when(agendaTeamProfileAdminRepository.findAllByAgendaTeamAndIsExistIsTrue(any(AgendaTeam.class)))
				.thenReturn(List.of(participant));
			when(agendaProfileAdminRepository.findByIntraId("newIntra"))
				.thenReturn(Optional.of(newProfile));
			when(agendaTeamProfileAdminRepository.save(any(AgendaTeamProfile.class)))
				.thenReturn(mock(AgendaTeamProfile.class));

			// when
			agendaTeamAdminService.updateAgendaTeam(agendaTeamUpdateDto);

			// then
			verify(agendaTeamAdminRepository, times(1))
				.findByTeamKey(any(UUID.class));
			verify(agendaTeamProfileAdminRepository, times(1))
				.findAllByAgendaTeamAndIsExistIsTrue(any(AgendaTeam.class));
			verify(agendaProfileAdminRepository, times(1))
				.findByIntraId("newIntra");
			verify(agendaTeamProfileAdminRepository, times(1))
				.save(any(AgendaTeamProfile.class));
		}

		@Test
		@DisplayName("Admin AgendaTeam 수정 성공 - 팀원 삭제하기")
		void updateAgendaTeamAdminSuccessWithRemoveTeammate() {
			// given
			Agenda agenda = Agenda.builder().build();
			AgendaTeam team = AgendaTeam.builder().teamKey(UUID.randomUUID()).agenda(agenda).build();
			AgendaProfile profile = AgendaProfile.builder().intraId("intra").build();
			AgendaTeamProfile participant = AgendaTeamProfile.builder()
				.agendaTeam(team).agenda(agenda).profile(profile).build();

			AgendaTeamUpdateDto agendaTeamUpdateDto = AgendaTeamUpdateDto.builder()
				.teamKey(team.getTeamKey()).teamMates(List.of()).build();

			when(agendaTeamAdminRepository.findByTeamKey(any(UUID.class))).thenReturn(Optional.of(team));
			when(agendaTeamProfileAdminRepository.findAllByAgendaTeamAndIsExistIsTrue(any(AgendaTeam.class)))
				.thenReturn(List.of(participant));

			// when
			agendaTeamAdminService.updateAgendaTeam(agendaTeamUpdateDto);

			// then
			verify(agendaTeamAdminRepository, times(1))
				.findByTeamKey(any(UUID.class));
			verify(agendaTeamProfileAdminRepository, times(1))
				.findAllByAgendaTeamAndIsExistIsTrue(any(AgendaTeam.class));
			assertThat(participant.getIsExist()).isFalse();    // leaveTeam() 호출 확인
		}

		@Test
		@DisplayName("Admin AgendaTeam 수정 실패 - 존재하지 않는 Team Key")
		void updateAgendaTeamAdminFailedWithInvalidTeamKey() {
			// given
			AgendaTeamUpdateDto agendaTeamUpdateDto = AgendaTeamUpdateDto.builder()
				.teamKey(UUID.randomUUID()).teamMates(List.of()).build();
			when(agendaTeamAdminRepository.findByTeamKey(any(UUID.class))).thenReturn(Optional.empty());

			// expected
			assertThrows(NotExistException.class,
				() -> agendaTeamAdminService.updateAgendaTeam(agendaTeamUpdateDto));
		}

		@Test
		@DisplayName("Admin AgendaTeam 수정 실패 - 존재하지 않는 Intra ID")
		void updateAgendaTeamAdminFailedWithInvalidIntraId() {
			// given
			Agenda agenda = Agenda.builder().build();
			AgendaTeam team = AgendaTeam.builder().teamKey(UUID.randomUUID()).agenda(agenda).build();
			AgendaProfile profile = AgendaProfile.builder().intraId("intra").build();
			AgendaTeamProfile participant = AgendaTeamProfile.builder()
				.agendaTeam(team).agenda(agenda).profile(profile).build();

			AgendaProfile newProfile = AgendaProfile.builder().intraId("newIntra").build();
			List<AgendaTeamMateReqDto> updateTeamMates = new ArrayList<>();
			updateTeamMates.add(AgendaTeamMateReqDto.builder()
				.intraId(profile.getIntraId()).build());
			updateTeamMates.add(AgendaTeamMateReqDto.builder()
				.intraId(newProfile.getIntraId()).build());

			AgendaTeamUpdateDto agendaTeamUpdateDto = AgendaTeamUpdateDto.builder()
				.teamKey(team.getTeamKey()).teamMates(updateTeamMates).build();

			when(agendaTeamAdminRepository.findByTeamKey(any(UUID.class))).thenReturn(Optional.of(team));
			when(agendaTeamProfileAdminRepository.findAllByAgendaTeamAndIsExistIsTrue(any(AgendaTeam.class)))
				.thenReturn(List.of(participant));
			when(agendaProfileAdminRepository.findByIntraId("newIntra"))
				.thenReturn(Optional.empty());

			// expected
			assertThrows(NotExistException.class,
				() -> agendaTeamAdminService.updateAgendaTeam(agendaTeamUpdateDto));
		}
	}
}
