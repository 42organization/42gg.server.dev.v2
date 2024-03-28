package gg.pingpong.api.user.tournament.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import gg.data.pingpong.manage.SlotManagement;
import gg.data.pingpong.tournament.Tournament;
import gg.data.pingpong.tournament.TournamentGame;
import gg.data.pingpong.tournament.type.TournamentStatus;
import gg.pingpong.api.admin.noti.controller.request.SendNotiAdminRequestDto;
import gg.pingpong.api.admin.noti.service.NotiAdminService;
import gg.pingpong.api.user.tournament.service.TournamentService;
import gg.repo.manage.SlotManagementRepository;
import gg.repo.tournarment.TournamentGameRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@Transactional
public class TournamentSchedulerTest {
	@Autowired
	TournamentService tournamentService;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	SlotManagementRepository slotManagementRepository;
	@Autowired
	TournamentGameRepository tournamentGameRepository;
	@MockBean
	NotiAdminService notiAdminService;

	@Nested
	@DisplayName("토너먼트 스케줄러 테스트")
	class TournamentSchedulerTests {
		@Test
		@DisplayName("토너먼트 시작 성공")
		public void startTournamentTest() {
			// given
			testDataUtils.createSeason();
			// BEFORE로 토너먼트 생성
			Tournament tournament = testDataUtils.createTournamentWithUser(Tournament.ALLOWED_JOINED_NUMBER, 4, "test");
			List<TournamentGame> tournamentGameList = testDataUtils.createTournamentGameList(tournament, 7);
			SlotManagement slotManagement = SlotManagement.builder()
				.pastSlotTime(0)
				.futureSlotTime(0)
				.openMinute(0)
				.gameInterval(15)
				.startTime(LocalDateTime.now().minusHours(1))
				.build();
			slotManagementRepository.save(slotManagement);

			// when
			tournamentService.startTournament();

			// then
			// 토너먼트의 상태가 LIVE로 변경되었는지 확인
			assertThat(tournament.getStatus())
				.isEqualTo(TournamentStatus.LIVE);

			// game이 생성되었는지 확인
			List<TournamentGame> tournamentGames = tournamentGameRepository.findAllByTournamentId(tournament.getId())
				.stream()
				.filter(o -> o.getGame() != null)
				.collect(Collectors.toList());
			assertThat(tournamentGames.size()).isEqualTo(Tournament.ALLOWED_JOINED_NUMBER / 2);
			for (TournamentGame tournamentGame : tournamentGames) {
				assertThat(tournamentGame.getGame()).isNotNull();
			}
			//참가자에게 토너먼트 시작 및 매칭 알림이 전송되었는지 확인
			verify(notiAdminService, times(Tournament.ALLOWED_JOINED_NUMBER)).sendAnnounceNotiToUser(
				Mockito.any(SendNotiAdminRequestDto.class));
		}

		@Test
		@DisplayName("토너먼트 취소 성공")
		public void cancelTournamentTest() {
			// given
			testDataUtils.createSeason();
			// BEFORE로 토너먼트 생성
			Tournament tournament = testDataUtils.createTournamentWithUser(7, 0, "test");
			SlotManagement slotManagement = SlotManagement.builder()
				.pastSlotTime(0)
				.futureSlotTime(0)
				.openMinute(0)
				.gameInterval(15)
				.startTime(LocalDateTime.now().minusHours(1))
				.build();
			slotManagementRepository.save(slotManagement);

			// when
			tournamentService.startTournament();

			// then
			//참가자에게 취소 알림이 전송되었는지 확인
			verify(notiAdminService, times(7)).sendAnnounceNotiToUser(Mockito.any(SendNotiAdminRequestDto.class));
		}
	}

}
