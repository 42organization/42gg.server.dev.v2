package gg.agenda.api.admin.agendateam.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaTeamAdminRepository;
import gg.admin.repo.agenda.AgendaTeamProfileAdminRepository;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.custom.NotExistException;
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

@UnitTest
public class AgendaTeamAdminServiceTest {

	@Mock
	private AgendaAdminRepository agendaAdminRepository;

	@Mock
	private AgendaTeamAdminRepository agendaTeamAdminRepository;

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
}
