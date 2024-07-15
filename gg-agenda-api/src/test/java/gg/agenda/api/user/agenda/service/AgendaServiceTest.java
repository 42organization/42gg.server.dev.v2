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

import gg.agenda.api.user.agenda.controller.request.AgendaConfirmReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaCreateReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaTeamAwardDto;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.AgendaStatus;
import gg.repo.agenda.AgendaAnnouncementRepository;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.utils.annotation.UnitTest;
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
	@DisplayName("Agenda 상세 조회")
	class GetAgenda {
		@Test
		@DisplayName("AgendaKey로 Agenda 상세 조회")
		void findAgendaByAgendaKeySuccess() {
			// given
			Agenda agenda = Agenda.builder().build();
			UUID agendaKey = agenda.getAgendaKey();
			when(agendaRepository.findByAgendaKey(agendaKey)).thenReturn(Optional.of(agenda));

			// when
			Agenda result = agendaService.findAgendaByAgendaKey(agendaKey);

			// then
			verify(agendaRepository, times(1)).findByAgendaKey(agendaKey);
			assertThat(result).isEqualTo(agenda);
		}

		@Test
		@DisplayName("AgendaKey로 Agenda 상세 조회 - 존재하지 않는 AgendaKey인 경우")
		void findAgendaByAgendaKeyFailedWithNoAgenda() {
			// given
			UUID agendaKey = UUID.randomUUID();
			when(agendaRepository.findByAgendaKey(agendaKey)).thenReturn(Optional.empty());

			// expected
			assertThrows(NotExistException.class,
				() -> agendaService.findAgendaByAgendaKey(agendaKey));
		}

		@Test
		@DisplayName("AgendaAnnouncement 조회 성공 - 최신 공지사항이 있는 경우")
		void findAgendaAnnouncementSuccess() {
			// given
			Agenda agenda = Agenda.builder().build();
			AgendaAnnouncement announcement = AgendaAnnouncement.builder().title("title").content("content").build();
			when(agendaAnnouncementRepository.findLatestByAgenda(agenda)).thenReturn(Optional.of(announcement));

			// when
			Optional<AgendaAnnouncement> result = agendaService.findAgendaWithLatestAnnouncement(agenda);

			// then
			verify(agendaAnnouncementRepository, times(1)).findLatestByAgenda(agenda);
			assertThat(result).isPresent();
			assertThat(result.get().getTitle()).isEqualTo(announcement.getTitle());
		}

		@Test
		@DisplayName("AgendaAnnouncement 조회 성공 - 최신 공지사항이 없는 경우")
		void findAgendaAnnouncementSuccessWithNoAnnounce() {
			// given
			Agenda agenda = Agenda.builder().build();
			when(agendaAnnouncementRepository.findLatestByAgenda(agenda)).thenReturn(Optional.empty());

			// when
			Optional<AgendaAnnouncement> result = agendaService.findAgendaWithLatestAnnouncement(agenda);

			// then
			verify(agendaAnnouncementRepository, times(1)).findLatestByAgenda(agenda);
			assertThat(result).isEmpty();
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
			List<Agenda> result = agendaService.findCurrentAgendaList();

			// then
			verify(agendaRepository, times(1)).findAllByStatusIs(any());
			for (int i = 0; i < result.size(); i++) {
				assertThat(result.get(i).getIsOfficial()).isEqualTo(i < officialSize);
				if (i == 0 || i == officialSize) {
					continue;
				}
				assertThat(result.get(i).getDeadline()).isBefore(result.get(i - 1).getDeadline());
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
			AgendaCreateReqDto agendaCreateReqDto = AgendaCreateReqDto.builder()
				.agendaDeadLine(LocalDateTime.now().plusDays(5))
				.agendaStartTime(LocalDateTime.now().plusDays(8))
				.agendaEndTime(LocalDateTime.now().plusDays(10))
				.agendaMinTeam(2).agendaMaxTeam(5)
				.agendaMinPeople(1).agendaMaxPeople(5)
				.build();
			Agenda agenda = Agenda.builder().build();
			when(agendaRepository.save(any(Agenda.class))).thenReturn(agenda);

			// when
			Agenda result = agendaService.addAgenda(agendaCreateReqDto, user);

			// then
			verify(agendaRepository, times(1)).save(any(Agenda.class));
			assertThat(result.getAgendaKey()).isEqualTo(agenda.getAgendaKey());
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
			List<Agenda> result = agendaService.findHistoryAgendaList(pageable);

			// then
			verify(agendaRepository, times(1))
				.findAllByStatusIs(pageable, AgendaStatus.CONFIRM);
			assertThat(result.size()).isEqualTo(size);
			for (int i = 1; i < result.size(); i++) {
				assertThat(result.get(i).getStartTime())
					.isBefore(result.get(i - 1).getStartTime());
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
			AgendaConfirmReqDto confirmDto = AgendaConfirmReqDto.builder()
				.awards(List.of(awardDto)).build();

			when(agendaTeamRepository.findByAgendaAndNameAndStatus(any(), any(), any()))
				.thenReturn(Optional.of(agendaTeams.get(seq++)));

			// when
			agendaService.confirmAgenda(confirmDto, agenda);

			// then
			verify(agendaTeamRepository, times(1)).findByAgendaAndNameAndStatus(any(), any(), any());
			assertThat(agenda.getStatus()).isEqualTo(AgendaStatus.CONFIRM);
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 성공 - 시상하지 않는 대회인 경우")
		void confirmAgendaSuccessWithNoRank() {
			// given
			Agenda agenda = Agenda.builder()
				.hostIntraId("intraId").startTime(LocalDateTime.now().minusDays(1))
				.status(AgendaStatus.ON_GOING).isRanking(false).build();
			UserDto user = UserDto.builder().intraId(agenda.getHostIntraId()).build();
			UUID agendaKey = agenda.getAgendaKey();

			// when
			agendaService.confirmAgenda(null, agenda);

			// then
			assertThat(agenda.getStatus()).isEqualTo(AgendaStatus.CONFIRM);
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 성공 - 시상하지 않는 대회에 시상 내역이 들어온 경우")
		void confirmAgendaSuccessWithNoRankAndAwards() {
			// given
			Agenda agenda = Agenda.builder()
				.hostIntraId("intraId").startTime(LocalDateTime.now().minusDays(1))
				.status(AgendaStatus.ON_GOING).isRanking(false).build();
			List<AgendaTeam> agendaTeams = new ArrayList<>();
			IntStream.range(0, 10).forEach(i -> agendaTeams.add(AgendaTeam.builder().name("team" + i).build()));
			AgendaTeamAwardDto awardDto = AgendaTeamAwardDto.builder()
				.teamName("team1").awardName("award").awardPriority(1).build();
			UserDto user = UserDto.builder().intraId(agenda.getHostIntraId()).build();
			UUID agendaKey = agenda.getAgendaKey();
			AgendaConfirmReqDto confirmDto = AgendaConfirmReqDto.builder()
				.awards(List.of(awardDto)).build();

			// when
			agendaService.confirmAgenda(confirmDto, agenda);

			// then
			verify(agendaTeamRepository, never()).findByAgendaAndNameAndStatus(any(), any(), any());
			assertThat(agenda.getStatus()).isEqualTo(AgendaStatus.CONFIRM);
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 성공 - 시상하지 않는 대회에 시상 내역이 빈 리스트로 들어온 경우")
		void confirmAgendaSuccessWithNoRankAndEmtpyAwards() {
			// given
			Agenda agenda = Agenda.builder()
				.hostIntraId("intraId").startTime(LocalDateTime.now().minusDays(1))
				.status(AgendaStatus.ON_GOING).isRanking(false).build();
			List<AgendaTeam> agendaTeams = new ArrayList<>();
			IntStream.range(0, 10).forEach(i -> agendaTeams.add(AgendaTeam.builder().name("team" + i).build()));
			UserDto user = UserDto.builder().intraId(agenda.getHostIntraId()).build();
			UUID agendaKey = agenda.getAgendaKey();
			AgendaConfirmReqDto confirmDto = AgendaConfirmReqDto.builder()
				.awards(List.of()).build();

			// when
			agendaService.confirmAgenda(confirmDto, agenda);

			// then
			verify(agendaTeamRepository, never()).findByAgendaAndNameAndStatus(any(), any(), any());
			assertThat(agenda.getStatus()).isEqualTo(AgendaStatus.CONFIRM);
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 성공 - 시상하지 않는 대회에 시상 내역이 null로 들어온 경우")
		void confirmAgendaSuccessWithNoRankAndNullAwards() {
			// given
			Agenda agenda = Agenda.builder()
				.hostIntraId("intraId").startTime(LocalDateTime.now().minusDays(1))
				.status(AgendaStatus.ON_GOING).isRanking(false).build();
			List<AgendaTeam> agendaTeams = new ArrayList<>();
			IntStream.range(0, 10).forEach(i -> agendaTeams.add(AgendaTeam.builder().name("team" + i).build()));
			UserDto user = UserDto.builder().intraId(agenda.getHostIntraId()).build();
			UUID agendaKey = agenda.getAgendaKey();
			AgendaConfirmReqDto confirmDto = AgendaConfirmReqDto.builder().build();

			// when
			agendaService.confirmAgenda(confirmDto, agenda);

			// then
			verify(agendaTeamRepository, never()).findByAgendaAndNameAndStatus(any(), any(), any());
			assertThat(agenda.getStatus()).isEqualTo(AgendaStatus.CONFIRM);
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 실패 - 시상 내역이 null인 경우")
		void confirmAgendaFailedWithoutAwards() {
			// given
			Agenda agenda = Agenda.builder()
				.hostIntraId("intraId").startTime(LocalDateTime.now().minusDays(1))
				.status(AgendaStatus.ON_GOING).isRanking(true).build();

			AgendaConfirmReqDto confirmDto = AgendaConfirmReqDto.builder().build();

			// expected
			assertThrows(NullPointerException.class,
				() -> agendaService.confirmAgenda(confirmDto, agenda));
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 실패 - 매개변수가 null인 경우")
		void confirmAgendaFailedWithNullDto() {
			// given
			Agenda agenda = Agenda.builder()
				.hostIntraId("intraId").startTime(LocalDateTime.now().minusDays(1))
				.status(AgendaStatus.ON_GOING).isRanking(true).build();

			// expected
			assertThrows(NullPointerException.class,
				() -> agendaService.confirmAgenda(null, agenda));
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 실패 - 존재하지 않는 팀에 대한 시상인 경우")
		void confirmAgendaFailedWithInvalidTeam() {
			// given
			Agenda agenda = Agenda.builder()
				.hostIntraId("intraId").startTime(LocalDateTime.now().minusDays(1))
				.status(AgendaStatus.ON_GOING).isRanking(true).build();
			AgendaTeamAwardDto awardDto = AgendaTeamAwardDto.builder()
				.teamName("invalidTeam").awardName("award").awardPriority(1).build();
			AgendaConfirmReqDto confirmDto = AgendaConfirmReqDto.builder()
				.awards(List.of(awardDto)).build();

			when(agendaTeamRepository.findByAgendaAndNameAndStatus(any(), any(), any()))
				.thenReturn(Optional.empty());

			// expected
			assertThrows(NotExistException.class,
				() -> agendaService.confirmAgenda(confirmDto, agenda));
		}
	}
}
