package com.gg.server.domain.tournament.controller;

import com.gg.server.admin.noti.dto.SendNotiAdminRequestDto;
import com.gg.server.admin.noti.service.NotiAdminService;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentGameRepository;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.tournament.service.TournamentService;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.global.scheduler.TournamentScheduler;
import com.gg.server.utils.TestDataUtils;
import com.gg.server.utils.annotation.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    class tournamentSchedulerTest {
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
            List<TournamentGame> tournamentGames = tournamentGameRepository.findAllByTournamentId(tournament.getId()).stream()
                .filter(o -> o.getGame() != null)
                .collect(Collectors.toList());
            assertThat(tournamentGames.size()).isEqualTo(Tournament.ALLOWED_JOINED_NUMBER / 2);
            for (TournamentGame tournamentGame : tournamentGames) {
                assertThat(tournamentGame.getGame()).isNotNull();
            }
            //참가자에게 토너먼트 시작 및 매칭 알림이 전송되었는지 확인
            verify(notiAdminService, times(16)).sendAnnounceNotiToUser(Mockito.any(SendNotiAdminRequestDto.class));
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
