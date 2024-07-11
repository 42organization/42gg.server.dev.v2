package gg.agenda.api.user.agenda.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import gg.agenda.api.user.agenda.controller.request.AgendaConfirmRequestDto;
import gg.agenda.api.user.agenda.controller.request.AgendaCreateDto;
import gg.agenda.api.user.agenda.controller.request.AgendaTeamAwardDto;
import gg.agenda.api.user.agenda.controller.response.AgendaKeyResponseDto;
import gg.agenda.api.user.agenda.controller.response.AgendaSimpleResponseDto;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.AgendaStatus;
import gg.repo.agenda.AgendaAnnouncementRepository;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.InvalidParameterException;
import gg.utils.exception.custom.NotExistException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UnitTest
class AgendaServiceTest {

	@Mock
	AgendaRepository agendaRepository;

	@Mock
	AgendaAnnouncementRepository agendaAnnouncementRepository;

	@Mock
	AgendaTeamRepository agendaTeamRepository;

	@InjectMocks
	AgendaService agendaService;

	@Nested
	@DisplayName("Agenda 단건 조회")
	class GetAgenda {

		@Test
		@DisplayName("Agenda 단건 조회 성공")
		void getAgendaSuccess() {
			// given
			UUID agendaKey = UUID.randomUUID();
			Agenda agenda = mock(Agenda.class);
			when(agendaRepository.findByAgendaKey(agendaKey)).thenReturn(Optional.of(agenda));
			when(agendaAnnouncementRepository.findLatestByAgenda(agenda)).thenReturn(Optional.empty());

			// when
			agendaService.findAgendaWithLatestAnnouncement(agendaKey);

			// then
			verify(agendaRepository, times(1)).findByAgendaKey(agendaKey);
			verify(agendaAnnouncementRepository, times(1)).findLatestByAgenda(agenda);
		}

		@Test
		@DisplayName("Agenda 단건 조회 실패")
		void getAgendaFailedWithnoAgenda() {
			// given
			UUID agendaKey = UUID.randomUUID();
			Agenda agenda = mock(Agenda.class);
			when(agendaRepository.findByAgendaKey(agendaKey)).thenReturn(Optional.empty());

			// expected
			assertThrows(NotExistException.class, () -> agendaService.findAgendaWithLatestAnnouncement(agendaKey));
			verify(agendaRepository, times(1)).findByAgendaKey(agendaKey);
			verify(agendaAnnouncementRepository, never()).findLatestByAgenda(agenda);
		}
	}

	@Nested
	@DisplayName("Agenda 현황 전체 조회")
	class GetAgendaListCurrent {

		@Test
		@DisplayName("Agenda 현황 전체를 반환합니다.")
		void getAgendaListSuccess() {
			// given
			int officialSize = 3;
			int nonOfficialSize = 6;
			List<Agenda> agendas = new ArrayList<>();
			IntStream.range(0, officialSize).forEach(i -> agendas.add(Agenda.builder().isOfficial(true)
				.deadline(LocalDateTime.now().plusDays(i + 3)).build()));
			IntStream.range(0, nonOfficialSize).forEach(i -> agendas.add(Agenda.builder().isOfficial(false)
				.deadline(LocalDateTime.now().plusDays(i + 3)).build()));
			when(agendaRepository.findAllByStatusIs(AgendaStatus.ON_GOING)).thenReturn(agendas);

			// when
			List<AgendaSimpleResponseDto> result = agendaService.findCurrentAgendaList();

			// then
			verify(agendaRepository, times(1)).findAllByStatusIs(any());
			for (int i = 0; i < result.size(); i++) {
				assertThat(result.get(i).getIsOfficial()).isEqualTo(i < officialSize);
				if (i == 0 || i == officialSize) {
					continue;
				}
				assertThat(result.get(i).getAgendaDeadLine()).isBefore(result.get(i - 1).getAgendaDeadLine());
			}
		}

		@Test
		@DisplayName("생성된 Agenda가 없는 경우 빈 리스트를 반환합니다.")
		void getAgendaListWithNoContent() {
			// given
			List<Agenda> agendas = new ArrayList<>();
			when(agendaRepository.findAllByStatusIs(AgendaStatus.ON_GOING)).thenReturn(agendas);

			// when
			agendaService.findCurrentAgendaList();

			// then
			verify(agendaRepository, times(1)).findAllByStatusIs(any());
		}
	}

	@Nested
	@DisplayName("Agenda 생성")
	class CreateAgenda {

		@Test
		@DisplayName("Agenda 생성 성공")
		void createAgendaSuccess() {
			// given
			UserDto user = UserDto.builder().intraId("intraId").build();
			AgendaCreateDto agendaCreateDto = AgendaCreateDto.builder()
				.agendaDeadLine(LocalDateTime.now().plusDays(5))
				.agendaStartTime(LocalDateTime.now().plusDays(8))
				.agendaEndTime(LocalDateTime.now().plusDays(10))
				.agendaMinTeam(2).agendaMaxTeam(5)
				.agendaMinPeople(1).agendaMaxPeople(5)
				.build();
			Agenda agenda = Agenda.builder().build();
			when(agendaRepository.save(any(Agenda.class))).thenReturn(agenda);

			// when
			AgendaKeyResponseDto agendaKeyResponseDto = agendaService.addAgenda(agendaCreateDto, user);

			// then
			verify(agendaRepository, times(1)).save(any(Agenda.class));
			assertThat(agendaKeyResponseDto).isNotNull();
			assertThat(agendaKeyResponseDto.getAgendaKey()).isEqualTo(agenda.getAgendaKey());
		}
	}

	@Nested
	@DisplayName("지난 Agenda 조회")
	class GetAgendaListHistory {

		@Test
		@DisplayName("지난 Agenda 조회 성공")
		void getAgendaListHistorySuccess() {
			// given
			int page = 1;
			int size = 10;
			Pageable pageable = PageRequest.of(page - 1, size, Sort.by("startTime").descending());
			List<Agenda> agendas = new ArrayList<>();
			IntStream.range(0, size * 2).forEach(i -> agendas.add(Agenda.builder()
				.startTime(LocalDateTime.now().minusDays(i))
				.build()
			));
			Page<Agenda> agendaPage = new PageImpl<>(agendas.subList(0, 10), pageable, size);
			when(agendaRepository.findAllByStatusIs(any(Pageable.class), eq(AgendaStatus.CONFIRM)))
				.thenReturn(agendaPage);

			// when
			List<AgendaSimpleResponseDto> result = agendaService.findHistoryAgendaList(pageable);

			// then
			verify(agendaRepository, times(1))
				.findAllByStatusIs(pageable, AgendaStatus.CONFIRM);
			assertThat(result.size()).isEqualTo(size);
			for (int i = 1; i < result.size(); i++) {
				assertThat(result.get(i).getAgendaStartTime())
					.isBefore(result.get(i - 1).getAgendaStartTime());
			}
		}
	}

	@Nested
	@DisplayName("Agenda 시상 및 확정")
	class ConfirmAgenda {

		int seq;

		@BeforeEach
		void setUp() {
			seq = 0;
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 성공")
		void confirmAgendaSuccess() {
			// given
			Agenda agenda = Agenda.builder()
				.hostIntraId("intraId").startTime(LocalDateTime.now().minusDays(1))
				.status(AgendaStatus.ON_GOING).isRanking(true).build();
			List<AgendaTeam> agendaTeams = new ArrayList<>();
			IntStream.range(0, 10).forEach(i -> agendaTeams.add(AgendaTeam.builder().name("team" + i).build()));
			AgendaTeamAwardDto awardDto = AgendaTeamAwardDto.builder()
				.teamName("team1").awardName("award").awardPriority(1).build();
			UserDto user = UserDto.builder().intraId(agenda.getHostIntraId()).build();
			UUID agendaKey = agenda.getAgendaKey();
			AgendaConfirmRequestDto confirmDto = AgendaConfirmRequestDto.builder()
				.awards(List.of(awardDto)).build();

			when(agendaRepository.findByAgendaKey(any(UUID.class))).thenReturn(Optional.of(agenda));
			when(agendaTeamRepository.findByAgendaAndNameAndStatus(any(), any(), any()))
				.thenReturn(Optional.of(agendaTeams.get(seq++)));

			// when
			agendaService.confirmAgenda(user, agendaKey, confirmDto);

			// then
			verify(agendaRepository, times(1)).findByAgendaKey(agendaKey);
			verify(agendaTeamRepository, times(1)).findByAgendaAndNameAndStatus(any(), any(), any());
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 실패 - Agenda가 없는 경우")
		void confirmAgendaFailedWithNoAgenda() {
			UserDto user = mock(UserDto.class);
			AgendaConfirmRequestDto confirmDto = mock(AgendaConfirmRequestDto.class);
			UUID agendaKey = UUID.randomUUID();
			when(agendaRepository.findByAgendaKey(any())).thenReturn(Optional.empty());

			// expected
			assertThrows(NotExistException.class,
				() -> agendaService.confirmAgenda(user, agendaKey, confirmDto));
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 실패 - 개최자가 아닌 경우")
		void confirmAgendaFailedNotHost() {
			// given
			AgendaConfirmRequestDto confirmDto = mock(AgendaConfirmRequestDto.class);
			UUID agendaKey = UUID.randomUUID();
			Agenda agenda = Agenda.builder().hostIntraId("intraId").build();
			UserDto user = UserDto.builder().intraId("another").build();    // 개최자가 아닌 경우
			when(agendaRepository.findByAgendaKey(any())).thenReturn(Optional.of(agenda));

			// expected
			assertThrows(ForbiddenException.class,
				() -> agendaService.confirmAgenda(user, agendaKey, confirmDto));
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 실패 - 시상 내역이 없는 경우")
		void confirmAgendaFailedWithNoAwards() {
			// given
			Agenda agenda = Agenda.builder()
				.hostIntraId("intraId").startTime(LocalDateTime.now().minusDays(1))
				.status(AgendaStatus.ON_GOING).isRanking(true).build();
			UserDto user = UserDto.builder().intraId(agenda.getHostIntraId()).build();
			UUID agendaKey = agenda.getAgendaKey();

			AgendaConfirmRequestDto confirmDto = AgendaConfirmRequestDto.builder().build();

			when(agendaRepository.findByAgendaKey(any(UUID.class))).thenReturn(Optional.of(agenda));

			// expected
			assertThrows(InvalidParameterException.class,
				() -> agendaService.confirmAgenda(user, agendaKey, confirmDto));
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 실패 - 존재하지 않는 팀에 대한 시상인 경우")
		void confirmAgendaFailedWithInvalidTeam() {
			// given
			Agenda agenda = Agenda.builder()
				.hostIntraId("intraId").startTime(LocalDateTime.now().minusDays(1))
				.status(AgendaStatus.ON_GOING).isRanking(true).build();
			UserDto user = UserDto.builder().intraId(agenda.getHostIntraId()).build();
			UUID agendaKey = agenda.getAgendaKey();
			AgendaTeamAwardDto awardDto = AgendaTeamAwardDto.builder()
				.teamName("invalidTeam").awardName("award").awardPriority(1).build();
			AgendaConfirmRequestDto confirmDto = AgendaConfirmRequestDto.builder()
				.awards(List.of(awardDto)).build();

			when(agendaRepository.findByAgendaKey(any(UUID.class))).thenReturn(Optional.of(agenda));
			when(agendaTeamRepository.findByAgendaAndNameAndStatus(any(), any(), any()))
				.thenReturn(Optional.empty());

			// expected
			assertThrows(NotExistException.class,
				() -> agendaService.confirmAgenda(user, agendaKey, confirmDto));
		}
	}
}
