package com.gg.server.domain.tournament.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.gg.server.admin.tournament.dto.TournamentAdminCreateRequestDto;
import com.gg.server.admin.tournament.dto.TournamentAdminUpdateRequestDto;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentGameRepository;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.tournament.data.TournamentUser;
import com.gg.server.domain.tournament.data.TournamentUserRepository;
import com.gg.server.domain.tournament.dto.TournamentUserRegistrationResponseDto;
import com.gg.server.domain.tournament.exception.TournamentNotFoundException;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {
    @Mock
    TournamentRepository tournamentRepository;
    @Mock
    TournamentGameRepository tournamentGameRepository;
    @Mock
    TournamentUserRepository tournamentUserRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    TournamentService tournamentService;

    @Nested
    @DisplayName("토너먼트_유저_상태_테스트")
    class UserStatusInTournamentTest {
        @Test
        @DisplayName("유저_상태_반환_성공")
        void success() {
            // given
            Tournament tournament = createTournament(1L, TournamentStatus.BEFORE,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2));
            User user = createUser("testUser");
            TournamentUser tournamentUser = new TournamentUser(user, tournament, true, LocalDateTime.now());
            given(tournamentRepository.findById(tournament.getId())).willReturn(Optional.of(tournament));
            given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
            given(tournamentUserRepository.findByTournamentIdAndUserId(tournament.getId(), user.getId()))
                .willReturn(Optional.of(tournamentUser));

            // when, then
            TournamentUserRegistrationResponseDto responseDto =
                tournamentService.getUserStatusInTournament(tournament.getId(), UserDto.from(user));
        }

        @Test
        @DisplayName("찾을_수_없는_토너먼트")
        void tournamentNotFound() {
            // given
            Long tournamentId = 1L;
            User user = createUser("testUser");
            given(tournamentRepository.findById(tournamentId)).willReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> tournamentService.getUserStatusInTournament(tournamentId, UserDto.from(user)))
                .isInstanceOf(TournamentNotFoundException.class);
        }

        @Test
        @DisplayName("db에_없는_유저")
        void userNotFound() {
            // given
            Tournament tournament = createTournament(1L, TournamentStatus.BEFORE,
                LocalDateTime.now(), LocalDateTime.now().plusHours(2));
            User user = createUser("testUser");
            given(tournamentRepository.findById(tournament.getId())).willReturn(Optional.of(tournament));
            given(userRepository.findById(user.getId())).willReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> tournamentService.getUserStatusInTournament(tournament.getId(), UserDto.from(user)))
                .isInstanceOf(UserNotFoundException.class);
        }
    }

    /**
     * 토너먼트 생성 requestDto
     * @param startTime 토너먼트 시작 시간
     * @param endTime 토너먼트 종료 시간
     * @return
     */
    private TournamentAdminCreateRequestDto createTournamentCreateRequestDto(String title, LocalDateTime startTime, LocalDateTime endTime) {
        return new TournamentAdminCreateRequestDto(
                title,
                "제 1회 루키전 많관부!!",
                startTime,
                endTime,
                TournamentType.ROOKIE
        );
    }

    /**
     * 토너먼트 게임 테이블 생성
     * @param tournament 토너먼트
     * @param round 몇 번째 게임인지에 대한 정보
     * @return 새로 생성된 토너먼트 게임
     */
    private TournamentGame createTournamentGame(Tournament tournament, TournamentRound round) {
        TournamentGame tournamentGame = new TournamentGame(null, tournament, round);
        return tournamentGameRepository.save(tournamentGame);
    }

    /**
     * 현재 시간에서 days hours, 만큼 차이나는 시간을 구한다.
     * @param days
     * @param hours
     * @return
     */
    private LocalDateTime getTargetTime(long days, long hours) {
        return LocalDateTime.now().plusDays(days).plusHours(hours);
    }

    /**
     * 각 매개변수로 초기화 된 토너먼트를 반환
     * @param id
     * @param status
     * @param startTime
     * @param endTime
     * @return
     */
    private Tournament createTournament(Long id, TournamentStatus status, LocalDateTime startTime, LocalDateTime endTime) {
        return new Tournament(
            id,
            id + "st tournament",
            "",
            startTime,
            endTime,
            TournamentType.ROOKIE,
            status,
            null,
            new ArrayList<>(),
            new ArrayList<>()
            );
    }

    /**
     * <div>id 부터 cnt개 만큼의 토너먼트 리스트를 반환해준다.<div/>
     * 각 토너먼트는 1시간 길이이며, 토너먼트간 1시간의 간격이 있다.
     * @param id
     * @param cnt
     * @param startTime
     * @return
     */
    private List<Tournament> createTournaments(Long id, long cnt, LocalDateTime startTime) {
        List<Tournament> tournamentList = new ArrayList<>();
        for (long i=0; i<cnt; i++) {
            tournamentList.add(createTournament(id++, TournamentStatus.BEFORE,
                startTime.plusHours(i*2), startTime.plusHours((i*2+2))));
        }
        return tournamentList;
    }

    /**
     * 각 매개변수로 초기화된 TournamentAdminUpdateRequestDto를 반환
     * @param startTime
     * @param endTime
     * @return
     */
    private TournamentAdminUpdateRequestDto createTournamentAdminUpdateRequestDto(LocalDateTime startTime, LocalDateTime endTime) {
        return new TournamentAdminUpdateRequestDto(
            "tournament changed",
            "changed",
            startTime,
            endTime,
            TournamentType.ROOKIE
        );
    }

    /**
     * 유저 생성 매서드 - intraId로만 초기화
     * @param intraId
     * @return
     */
    private User createUser(String intraId) {
        return User.builder()
            .eMail("email")
            .intraId(intraId)
            .racketType(RacketType.PENHOLDER)
            .snsNotiOpt(SnsType.NONE)
            .roleType(RoleType.USER)
            .totalExp(1000)
            .build();
    }

    /**
     * cnt 사이즈의 토너먼트 게임 리스트 생성
     * @param id 토너먼트 게임 id
     * @param tournament 해당 토너먼트
     * @param cnt 토너먼트 게임 수
     * @return
     */
    private List<TournamentGame> createTournamentGames(Long id, Tournament tournament, int cnt) {
        List<TournamentGame> tournamentGameList = new ArrayList<>();
        TournamentRound [] values = TournamentRound.values();
        while (--cnt >= 0) {
            tournamentGameList.add(new TournamentGame(id, null, tournament, values[cnt]));
        }
        return tournamentGameList;
    }
}