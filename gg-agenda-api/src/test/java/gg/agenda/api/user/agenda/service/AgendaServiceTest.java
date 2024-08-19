package gg.agenda.api.user.agenda.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import gg.agenda.api.user.agenda.controller.request.AgendaAwardsReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaCreateReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaTeamAward;
import gg.agenda.api.user.agendateam.service.AgendaTeamService;
import gg.agenda.api.user.ticket.service.TicketService;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.AgendaTeamProfileRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.custom.InvalidParameterException;
import gg.utils.exception.custom.NotExistException;
import gg.utils.file.handler.ImageHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UnitTest
class AgendaServiceTest {

	@Mock
	AgendaRepository agendaRepository;

	@Mock
	AgendaTeamRepository agendaTeamRepository;

	@Mock
	AgendaTeamProfileRepository agendaTeamProfileRepository;

	@Mock
	AgendaTeamService agendaTeamService;

	@Mock
	TicketService ticketService;

	@Mock
	ImageHandler imageHandler;

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
			when(agendaRepository.findAllByStatusIs(AgendaStatus.OPEN)).thenReturn(agendas);

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
			when(agendaRepository.findAllByStatusIs(AgendaStatus.OPEN)).thenReturn(agendas);

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
		void createAgendaSuccess() throws IOException {
			// given
			AgendaCreateReqDto agendaCreateReqDto = AgendaCreateReqDto.builder().build();
			UserDto user = UserDto.builder().intraId("intraId").build();
			Agenda agenda = Agenda.builder().build();
			when(agendaRepository.save(any())).thenReturn(agenda);
			when(imageHandler.uploadImageOrDefault(any(), any(), any())).thenReturn(new URL("http://localhost"));

			// when
			Agenda result = agendaService.addAgenda(agendaCreateReqDto, null, user);

			// then
			verify(agendaRepository, times(1)).save(any());
			verify(imageHandler, times(1)).uploadImageOrDefault(any(), any(), any());
			assertThat(result).isEqualTo(agenda);
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
			when(agendaRepository.findAllByStatusIs(eq(AgendaStatus.FINISH), any(Pageable.class)))
				.thenReturn(agendaPage);

			// when
			Page<Agenda> res = agendaService.findHistoryAgendaList(pageable);
			List<Agenda> result = res.getContent();

			// then
			verify(agendaRepository, times(1))
				.findAllByStatusIs(AgendaStatus.FINISH, pageable);
			assertThat(result.size()).isEqualTo(size);
			for (int i = 1; i < result.size(); i++) {
				assertThat(result.get(i).getStartTime())
					.isBefore(result.get(i - 1).getStartTime());
			}
		}
	}

	@Nested
	@DisplayName("Agenda 시상 및 확정")
	class FinishAgenda {

		int seq;

		@BeforeEach
		void setUp() {
			seq = 0;
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 성공")
		void finishAgendaSuccess() {
			// given
			Agenda agenda = Agenda.builder()
				.hostIntraId("intraId").startTime(LocalDateTime.now().minusDays(1))
				.status(AgendaStatus.CONFIRM).isRanking(true).build();
			List<AgendaTeam> agendaTeams = new ArrayList<>();
			IntStream.range(0, 10).forEach(i -> agendaTeams.add(AgendaTeam.builder().name("team" + i).build()));
			AgendaTeamAward awardDto = AgendaTeamAward.builder()
				.teamName("team1").awardName("award").awardPriority(1).build();
			AgendaAwardsReqDto confirmDto = AgendaAwardsReqDto.builder()
				.awards(List.of(awardDto)).build();

			when(agendaTeamRepository.findAllByAgendaAndStatus(any(), any()))
				.thenReturn(agendaTeams);

			// when
			agendaService.awardAgenda(confirmDto, agenda);

			// then
			verify(agendaTeamRepository, times(1)).findAllByAgendaAndStatus(any(), any());
			assertThat(agenda.getStatus()).isEqualTo(AgendaStatus.CONFIRM);
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 성공 - 시상하지 않는 대회에 시상 내역이 빈 리스트로 들어온 경우")
		void finishAgendaSuccessWithNoRankAndEmptyAwards() {
			// given
			Agenda agenda = Agenda.builder()
				.hostIntraId("intraId").startTime(LocalDateTime.now().minusDays(1))
				.status(AgendaStatus.CONFIRM).isRanking(false).build();
			AgendaAwardsReqDto confirmDto = AgendaAwardsReqDto.builder()
				.awards(List.of()).build();
			when(agendaTeamRepository.findAllByAgendaAndStatus(any(), any()))
				.thenReturn(List.of());

			// when
			agendaService.awardAgenda(confirmDto, agenda);

			// then
			verify(agendaTeamRepository, times(1)).findAllByAgendaAndStatus(any(), any());
			assertThat(agenda.getStatus()).isEqualTo(AgendaStatus.CONFIRM);
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 실패 - 시상 내역이 null인 경우")
		void finishAgendaFailedWithoutAwards() {
			// given
			Agenda agenda = Agenda.builder()
				.hostIntraId("intraId").startTime(LocalDateTime.now().minusDays(1))
				.status(AgendaStatus.CONFIRM).isRanking(true).build();

			AgendaAwardsReqDto confirmDto = AgendaAwardsReqDto.builder().build();

			// expected
			assertThrows(NullPointerException.class,
				() -> agendaService.awardAgenda(confirmDto, agenda));
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 실패 - 매개변수가 null인 경우")
		void finishAgendaFailedWithNullDto() {
			// given
			Agenda agenda = Agenda.builder()
				.hostIntraId("intraId").startTime(LocalDateTime.now().minusDays(1))
				.status(AgendaStatus.CONFIRM).isRanking(true).build();

			// expected
			assertThrows(NullPointerException.class,
				() -> agendaService.awardAgenda(null, agenda));
		}

		@Test
		@DisplayName("Agenda 시상 및 확정 실패 - 존재하지 않는 팀에 대한 시상인 경우")
		void finishAgendaFailedWithInvalidTeam() {
			// given
			Agenda agenda = Agenda.builder()
				.hostIntraId("intraId").startTime(LocalDateTime.now().minusDays(1))
				.status(AgendaStatus.CONFIRM).isRanking(true).build();
			AgendaTeamAward awardDto = AgendaTeamAward.builder()
				.teamName("invalidTeam").awardName("award").awardPriority(1).build();
			AgendaAwardsReqDto confirmDto = AgendaAwardsReqDto.builder()
				.awards(List.of(awardDto)).build();

			when(agendaTeamRepository.findAllByAgendaAndStatus(any(), any()))
				.thenReturn(List.of());

			// expected
			assertThrows(NotExistException.class,
				() -> agendaService.awardAgenda(confirmDto, agenda));
		}
	}

	@Nested
	@DisplayName("Agenda 확정하기")
	class ConfirmAgenda {

		@Test
		@DisplayName("Agenda 확정하기 성공")
		void confirmAgendaSuccess() {
			// given
			Agenda agenda = Agenda.builder().hostIntraId("intraId").status(AgendaStatus.OPEN).build();
			AgendaTeam agendaTeam = AgendaTeam.builder().status(AgendaTeamStatus.OPEN).build();
			AgendaTeamProfile participant = AgendaTeamProfile.builder()
				.profile(AgendaProfile.builder().build()).build();
			when(agendaTeamRepository.findAllByAgendaAndStatus(agenda, AgendaTeamStatus.OPEN))
				.thenReturn(List.of(agendaTeam));
			doNothing().when(agendaTeamService).leaveTeamAll(any());

			// when
			agendaService.confirmAgendaAndRefundTicketForOpenTeam(agenda);

			// then
			verify(agendaTeamRepository, times(1)).findAllByAgendaAndStatus(agenda, AgendaTeamStatus.OPEN);
			assertThat(agenda.getStatus()).isEqualTo(AgendaStatus.CONFIRM);
		}

		@Test
		@DisplayName("Agenda 확정하기 실패 - AgendaTeam이 없는 경우")
		void confirmAgendaFailedWithNoAgenda() {
			// given
			Agenda agenda = Agenda.builder().hostIntraId("intraId").status(AgendaStatus.OPEN).build();
			when(agendaTeamRepository.findAllByAgendaAndStatus(agenda, AgendaTeamStatus.OPEN))
				.thenReturn(List.of());

			// when
			agendaService.confirmAgendaAndRefundTicketForOpenTeam(agenda);

			// then
			verify(agendaTeamRepository, times(1)).findAllByAgendaAndStatus(agenda, AgendaTeamStatus.OPEN);
			assertThat(agenda.getStatus()).isEqualTo(AgendaStatus.CONFIRM);
		}

		@ParameterizedTest
		@EnumSource(value = AgendaStatus.class, names = {"CONFIRM", "FINISH", "CANCEL"})
		@DisplayName("Agenda 확정하기 실패 - 대회의 상태가 OPEN이 아닌 경우")
		void confirmAgendaFailedWithAlreadyConfirm(AgendaStatus status) {
			// given
			Agenda agenda = Agenda.builder().hostIntraId("intraId").status(status).build();
			AgendaTeam agendaTeam = AgendaTeam.builder().status(AgendaTeamStatus.OPEN).build();
			when(agendaTeamRepository.findAllByAgendaAndStatus(agenda, AgendaTeamStatus.OPEN))
				.thenReturn(List.of(agendaTeam));

			// expected
			assertThrows(InvalidParameterException.class,
				() -> agendaService.confirmAgendaAndRefundTicketForOpenTeam(agenda));
			verify(agendaTeamRepository, times(1)).findAllByAgendaAndStatus(agenda, AgendaTeamStatus.OPEN);
		}
	}

	@Nested
	@DisplayName("Agenda 취소하기")
	class CancelAgenda {
		@Test
		@DisplayName("Agenda 취소하기 성공")
		void cancelAgendaSuccess() {
			// given
			Agenda agenda = Agenda.builder().status(AgendaStatus.OPEN).build();
			List<AgendaTeam> agendaTeams = List.of(mock(AgendaTeam.class));
			when(agendaTeamRepository.findAllByAgendaAndStatus(any(), any(), any()))
				.thenReturn(agendaTeams);
			doNothing().when(agendaTeamService).leaveTeamAll(any());

			// when
			agendaService.cancelAgenda(agenda);

			// then
			verify(agendaTeamRepository, times(1)).findAllByAgendaAndStatus(any(), any(), any());
			verify(agendaTeamService, times(agendaTeams.size())).leaveTeamAll(any());
			assertThat(agenda.getStatus()).isEqualTo(AgendaStatus.CANCEL);
		}

		@ParameterizedTest
		@EnumSource(value = AgendaStatus.class, names = {"CONFIRM", "FINISH", "CANCEL"})
		@DisplayName("Agenda 취소하기 실패 - AgendaStatus가 OPEN이 아닌 경우")
		void cancelAgendaFailedWithNotOpen(AgendaStatus status) {
			// given
			Agenda agenda = Agenda.builder().status(status).build();
			List<AgendaTeam> agendaTeam = List.of(mock(AgendaTeam.class));
			when(agendaTeamRepository.findAllByAgendaAndStatus(any(), any(), any()))
				.thenReturn(agendaTeam);
			doNothing().when(agendaTeamService).leaveTeamAll(any());

			// expected
			assertThrows(InvalidParameterException.class,
				() -> agendaService.cancelAgenda(agenda));
		}

		@Test
		@DisplayName("Agenda 취소하기 성공 - AgendaTeam이 없는 경우")
		void cancelAgendaSuccessWithNoAgendaTeam() {
			// given
			Agenda agenda = Agenda.builder().status(AgendaStatus.OPEN).build();
			when(agendaTeamRepository.findAllByAgendaAndStatus(any(), any(), any()))
				.thenReturn(List.of());

			// when
			agendaService.cancelAgenda(agenda);

			verify(agendaTeamRepository, times(1)).findAllByAgendaAndStatus(any(), any(), any());
			verify(agendaTeamService, never()).leaveTeamAll(any());
			assertThat(agenda.getStatus()).isEqualTo(AgendaStatus.CANCEL);
		}
	}
}
