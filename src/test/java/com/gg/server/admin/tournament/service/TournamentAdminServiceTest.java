package com.gg.server.admin.tournament.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.gg.server.admin.tournament.dto.TournamentAdminCreateRequestDto;
import com.gg.server.admin.tournament.dto.TournamentAdminAddUserRequestDto;
import com.gg.server.admin.tournament.dto.TournamentAdminUpdateRequestDto;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentGameRepository;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.tournament.data.TournamentUser;
import com.gg.server.domain.tournament.data.TournamentUserRepository;
import com.gg.server.domain.tournament.exception.TournamentConflictException;
import com.gg.server.domain.tournament.exception.TournamentNotFoundException;
import com.gg.server.domain.tournament.exception.TournamentTitleConflictException;
import com.gg.server.domain.tournament.exception.TournamentUpdateException;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.global.exception.custom.InvalidParameterException;
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
class TournamentAdminServiceTest {
    @Mock
    TournamentRepository tournamentRepository;
    @Mock
    TournamentGameRepository tournamentGameRepository;
    @Mock
    TournamentUserRepository tournamentUserRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    TournamentAdminService tournamentAdminService;

    // 토너먼트 생성 서비스 테스트
    @Nested
    @DisplayName("토너먼트 관리자 생성 서비스 테스트")
    class TournamentAdminServiceCreateTest {
        @Test
        @DisplayName("토너먼트 생성 성공")
        void success() {
            // given
            TournamentAdminCreateRequestDto tournamentAdminCreateRequestDto = createTournamentCreateRequestDto(
                    "1st tournament",
                    getTargetTime(3, 1), getTargetTime(3, 3));
            List<Tournament> tournamentList = createTournaments(1L, 2, getTargetTime(2, 1));
            Tournament tournament = tournamentList.get(0);
            TournamentGame tournamentGame = createTournamentGame(tournament, TournamentRound.THE_FINAL);

            given(tournamentRepository.findByTitle(tournament.getTitle())).willReturn(Optional.empty());
            given(tournamentRepository.findAllByStatusIsNot(TournamentStatus.END)).willReturn(tournamentList);
            given(tournamentRepository.save(any(Tournament.class))).willReturn(tournament);

            // when
            tournamentAdminService.createTournament(tournamentAdminCreateRequestDto);
        }

        @Test
        @DisplayName("토너먼트 제목 중복")
        public void titleDup() {
            //given
            Tournament tournament = createTournament(1L, TournamentStatus.BEFORE,
                getTargetTime(0, -1), getTargetTime(0, 1));
            TournamentAdminCreateRequestDto tournamentAdminCreateRequestDto = createTournamentCreateRequestDto(
                    "1st tournament",
                    getTargetTime(3, 1), getTargetTime(3, 3));
            given(tournamentRepository.findByTitle(tournament.getTitle())).willReturn(Optional.of(tournament));
            // when, then
            assertThatThrownBy(() -> tournamentAdminService.createTournament(tournamentAdminCreateRequestDto))
                    .isInstanceOf(TournamentTitleConflictException.class);
        }

        @Test
        @DisplayName("유효하지 않은 시간 입력")
        public void invalidTime() {
            //given
            Tournament tournament = createTournament(1L, TournamentStatus.BEFORE,
                    getTargetTime(0, 0), getTargetTime(0, 1));

            TournamentAdminCreateRequestDto tournamentAdminCreateRequestDto1 = createTournamentCreateRequestDto(
                    "1st tournament",
                    getTargetTime(1, 1), getTargetTime(1, 3));
            TournamentAdminCreateRequestDto tournamentAdminCreateRequestDto2 = createTournamentCreateRequestDto(
                    "1st tournament",
                    getTargetTime(3, 3), getTargetTime(3, 1));
            TournamentAdminCreateRequestDto tournamentAdminCreateRequestDto3 = createTournamentCreateRequestDto(
                    "1st tournament",
                    getTargetTime(3, 3), getTargetTime(3, 3));
            TournamentAdminCreateRequestDto tournamentAdminCreateRequestDto4 = createTournamentCreateRequestDto(
                    "1st tournament",
                    getTargetTime(3, 1), getTargetTime(3, 2));

            given(tournamentRepository.findByTitle(tournament.getTitle())).willReturn(Optional.empty());
            // when, then
            assertThatThrownBy(() -> tournamentAdminService.createTournament(tournamentAdminCreateRequestDto1))
                    .isInstanceOf(InvalidParameterException.class);
            assertThatThrownBy(() -> tournamentAdminService.createTournament(tournamentAdminCreateRequestDto2))
                    .isInstanceOf(InvalidParameterException.class);
            assertThatThrownBy(() -> tournamentAdminService.createTournament(tournamentAdminCreateRequestDto3))
                    .isInstanceOf(InvalidParameterException.class);
            assertThatThrownBy(() -> tournamentAdminService.createTournament(tournamentAdminCreateRequestDto4))
                    .isInstanceOf(InvalidParameterException.class);
        }

        @Test
        @DisplayName("기존에 있는 토너먼트와 겹치는 토너먼트 시간")
        public void tournamentTimeConflict() {
            // given
            List<Tournament> tournamentList = createTournaments(1L, 2, getTargetTime(3, 1));
            Tournament tournament = tournamentList.get(0);
            TournamentAdminCreateRequestDto createRequestDto1 = createTournamentCreateRequestDto(
                    "1st tournament",
                    getTargetTime(3, 2), getTargetTime(3, 5));
            TournamentAdminCreateRequestDto createRequestDto2 = createTournamentCreateRequestDto(
                    "1st tournament",
                    getTargetTime(3, 0), getTargetTime(3, 2));
            TournamentAdminCreateRequestDto createRequestDto3 = createTournamentCreateRequestDto(
                    "1st tournament",
                    getTargetTime(3, 0), getTargetTime(3, 4));
            TournamentAdminCreateRequestDto createRequestDto4 = createTournamentCreateRequestDto(
                    "1st tournament",
                    getTargetTime(3, 1), getTargetTime(3, 5));
            TournamentAdminCreateRequestDto createRequestDto5 = createTournamentCreateRequestDto(
                    "1st tournament",
                    getTargetTime(2, 3), getTargetTime(3, 1));
            TournamentAdminCreateRequestDto createRequestDto6 = createTournamentCreateRequestDto(
                    "1st tournament",
                    getTargetTime(2, 3), getTargetTime(3, 3));
            TournamentAdminCreateRequestDto createRequestDto7 = createTournamentCreateRequestDto(
                    "1st tournament",
                    LocalDateTime.now().plusDays(2).plusMinutes(30), LocalDateTime.now().plusDays(3).plusHours(1));
            given(tournamentRepository.findByTitle(tournament.getTitle())).willReturn(Optional.empty());
            given(tournamentRepository.findAllByStatusIsNot(TournamentStatus.END)).willReturn(tournamentList);

            // when, then
            assertThatThrownBy(() -> tournamentAdminService.createTournament(createRequestDto1))
                    .isInstanceOf(TournamentConflictException.class);
            assertThatThrownBy(() -> tournamentAdminService.createTournament(createRequestDto2))
                    .isInstanceOf(TournamentConflictException.class);
            assertThatThrownBy(() -> tournamentAdminService.createTournament(createRequestDto3))
                    .isInstanceOf(TournamentConflictException.class);
            assertThatThrownBy(() -> tournamentAdminService.createTournament(createRequestDto4))
                    .isInstanceOf(TournamentConflictException.class);
            assertThatThrownBy(() -> tournamentAdminService.createTournament(createRequestDto5))
                    .isInstanceOf(TournamentConflictException.class);
            assertThatThrownBy(() -> tournamentAdminService.createTournament(createRequestDto6))
                    .isInstanceOf(TournamentConflictException.class);
            assertThatThrownBy(() -> tournamentAdminService.createTournament(createRequestDto7))
                    .isInstanceOf(TournamentConflictException.class);
        }
    }

    // 토너먼트 수정 서비스 테스트
    @Nested
    @DisplayName("토너먼트 관리자 서비스 수정 테스트")
    class TournamentAdminServiceUpdateTest {
        @Test
        @DisplayName("토너먼트_업데이트_성공")
        public void success() {
            // given
            List<Tournament> tournamentList = createTournaments(1L, 2, getTargetTime(2, 1));
            Tournament tournament = tournamentList.get(0);
            TournamentAdminUpdateRequestDto updateRequestDto = createTournamentAdminUpdateRequestDto(
                getTargetTime(3, 1), getTargetTime(3, 3));
            given(tournamentRepository.findById(1L)).willReturn(Optional.of(tournament));
            given(tournamentRepository.findAllByStatusIsNot(TournamentStatus.END)).willReturn(tournamentList);
            given(tournamentRepository.save(any(Tournament.class))).willReturn(tournament);
            // when
            Tournament changedTournament = tournamentAdminService.updateTournamentInfo(1L, updateRequestDto);
            // then
            assertThat(changedTournament.getId()).isEqualTo(tournament.getId());
            assertThat(changedTournament.getTitle()).isEqualTo(updateRequestDto.getTitle());
            assertThat(changedTournament.getContents()).isEqualTo(updateRequestDto.getContents());
            assertThat(changedTournament.getStartTime()).isEqualTo(updateRequestDto.getStartTime());
            assertThat(changedTournament.getEndTime()).isEqualTo(updateRequestDto.getEndTime());
            assertThat(changedTournament.getType()).isEqualTo(updateRequestDto.getType());
            assertThat(changedTournament.getStatus()).isEqualTo(tournament.getStatus());
        }

        @Test
        @DisplayName("타겟_토너먼트_없음")
        public void tournamentNotFound() {
            // given
            Tournament tournament = createTournament(1234L, TournamentStatus.BEFORE,
                getTargetTime(2, 1), getTargetTime(2, 3));
            TournamentAdminUpdateRequestDto updateRequestDto = createTournamentAdminUpdateRequestDto(
                getTargetTime(2, 1), getTargetTime(2, 3));

            given(tournamentRepository.findById(tournament.getId())).willReturn(Optional.empty());
            // when, then
            assertThatThrownBy(() -> tournamentAdminService.updateTournamentInfo(tournament.getId(), updateRequestDto))
                .isInstanceOf(TournamentNotFoundException.class);
        }

        @Test
        @DisplayName("토너먼트_업데이트_불가_상태")
        public void canNotUpdate() {
            // given
            Tournament tournamentLive = createTournament(1L, TournamentStatus.LIVE,
                getTargetTime(0, -1), getTargetTime(0, 1));
            Tournament tournamentEnd = createTournament(2L, TournamentStatus.END,
                getTargetTime(0, -3), getTargetTime(0, -1));
            TournamentAdminUpdateRequestDto updateRequestDto = createTournamentAdminUpdateRequestDto(
                getTargetTime(2, 1), getTargetTime(2, 3));
            given(tournamentRepository.findById(tournamentLive.getId())).willReturn(Optional.of(tournamentLive));
            given(tournamentRepository.findById(tournamentEnd.getId())).willReturn(Optional.of(tournamentEnd));
            // when, then
            assertThatThrownBy(() -> tournamentAdminService.updateTournamentInfo(tournamentLive.getId(), updateRequestDto))
                .isInstanceOf(TournamentUpdateException.class);
            assertThatThrownBy(() -> tournamentAdminService.updateTournamentInfo(tournamentEnd.getId(), updateRequestDto))
                .isInstanceOf(TournamentUpdateException.class);
        }

        @Test
        @DisplayName("업데이트_토너먼트_Dto_Invalid_Time")
        public void Dto_Invalid_Time() {
            // given
            Tournament tournament = createTournament(1L, TournamentStatus.BEFORE,
                getTargetTime(2, 1), getTargetTime(2, 3));
            TournamentAdminUpdateRequestDto invalidRequestDto1 = createTournamentAdminUpdateRequestDto(
                getTargetTime(2, 3), getTargetTime(2, 1));
            TournamentAdminUpdateRequestDto invalidRequestDto2 = createTournamentAdminUpdateRequestDto(
                invalidRequestDto1.getStartTime(), invalidRequestDto1.getStartTime());
            TournamentAdminUpdateRequestDto invalidRequestDto3 = createTournamentAdminUpdateRequestDto(
                getTargetTime(2, -1), getTargetTime(2, 1));

            given(tournamentRepository.findById(1L)).willReturn(Optional.of(tournament));
            // when then
            assertThatThrownBy(() -> tournamentAdminService.updateTournamentInfo(tournament.getId(), invalidRequestDto1))
                .isInstanceOf(InvalidParameterException.class);
            assertThatThrownBy(() -> tournamentAdminService.updateTournamentInfo(tournament.getId(), invalidRequestDto2))
                .isInstanceOf(InvalidParameterException.class);
            assertThatThrownBy(() -> tournamentAdminService.updateTournamentInfo(tournament.getId(), invalidRequestDto3))
                .isInstanceOf(InvalidParameterException.class);
        }

        @Test
        @DisplayName("Dto_기간_토너먼트_기간_겹침")
        public void tournamentTimeConflict() {
            // given
            List<Tournament> tournamentList = createTournaments(1L, 2, getTargetTime(2, 1));
            Tournament tournament = tournamentList.get(0);
            TournamentAdminUpdateRequestDto updateRequestDto = createTournamentAdminUpdateRequestDto(
                LocalDateTime.now().plusDays(2).plusHours(3), LocalDateTime.now().plusDays(2).plusHours(5));
            given(tournamentRepository.findById(1L)).willReturn(Optional.of(tournament));
            given(tournamentRepository.findAllByStatusIsNot(TournamentStatus.END)).willReturn(tournamentList);
            // when, then
            assertThatThrownBy(() -> tournamentAdminService.updateTournamentInfo(tournament.getId(), updateRequestDto))
                .isInstanceOf(TournamentConflictException.class);
        }
    }

    // 토너먼트 삭제 서비스 테스트
    @Nested
    @DisplayName("토너먼트 관리자 서비스 삭제 테스트")
    class TournamentAdminServiceDeleteTest {
        @Test
        @DisplayName("토너먼트_삭제_성공")
        void success() {
            // given
            int tournamentGameCnt = 7;
            Tournament tournament = createTournament(1L, TournamentStatus.BEFORE,
                getTargetTime(2, 1), getTargetTime(2, 3));
            List<TournamentGame> tournamentGameList = createTournamentGames(1L, tournament, tournamentGameCnt);
            given(tournamentRepository.findById(1L)).willReturn(Optional.of(tournament));
            given(tournamentGameRepository.findAllByTournamentId(tournament.getId())).willReturn(tournamentGameList);
            // when, then
            tournamentAdminService.deleteTournament(tournament.getId());
        }
        @Test
        @DisplayName("타겟_토너먼트_없음")
        public void tournamentNotFound() {
            // given
            Tournament tournament = createTournament(1L, TournamentStatus.BEFORE,
                getTargetTime(2, 1), getTargetTime(2, 3));
            given(tournamentRepository.findById(1L)).willReturn(Optional.empty());
            // when, then
            assertThatThrownBy(() -> tournamentAdminService.deleteTournament(tournament.getId()))
                .isInstanceOf(TournamentNotFoundException.class);
        }

        @Test
        @DisplayName("토너먼트_삭제_불가_상태")
        public void canNotDelete() {
            // given
            Tournament liveTournament = createTournament(1L, TournamentStatus.LIVE,
                getTargetTime(0, -1), getTargetTime(0, 1));
            Tournament endTournament = createTournament(1L, TournamentStatus.END,
                getTargetTime(-2, 5), getTargetTime(-2, 7));
            given(tournamentRepository.findById(liveTournament.getId())).willReturn(Optional.of(liveTournament));
            given(tournamentRepository.findById(endTournament.getId())).willReturn(Optional.of(endTournament));
            // when, then
            assertThatThrownBy(() -> tournamentAdminService.deleteTournament(liveTournament.getId()))
                .isInstanceOf(TournamentUpdateException.class);
            assertThatThrownBy(() -> tournamentAdminService.deleteTournament(endTournament.getId()))
                .isInstanceOf(TournamentUpdateException.class);
        }

    }

    @Nested
    @DisplayName("관리자_토너먼트_유저_추가_테스트")
    class TournamentAdminServiceAddUserTest {
        @Test
        @DisplayName("유저_추가_성공")
        public void success() {
            // given
            List<Tournament> tournamentList = createTournaments(1L, 2, getTargetTime(2, 1));
            Tournament tournament = tournamentList.get(0);
            TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto("testUser");
            User user = createUser("testUser");
            TournamentUser tournamentUser = new TournamentUser(user, tournament, true, LocalDateTime.now());
            given(tournamentRepository.findById(1L)).willReturn(Optional.of(tournament));
            given(userRepository.findByIntraId("testUser")).willReturn(Optional.of(user));
            given(tournamentRepository.findAllByStatusIsNot(TournamentStatus.END)).willReturn(tournamentList);
            given(tournamentUserRepository.save(any(TournamentUser.class))).willReturn(tournamentUser);

            // when, then
            tournamentAdminService.addTournamentUser(1L, requestDto);
        }

        @Test
        @DisplayName("타겟_토너먼트_없음")
        public void tournamentNotFound() {
            // given
            TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto("test");

            given(tournamentRepository.findById(any(Long.class))).willReturn(Optional.empty());
            // when, then
            assertThatThrownBy(() -> tournamentAdminService.addTournamentUser(1L, requestDto))
                .isInstanceOf(TournamentNotFoundException.class);
        }

        @Test
        @DisplayName("토너먼트_업데이트_불가_상태")
        public void canNotAdd() {
            // given
            Tournament tournamentLive = createTournament(1L, TournamentStatus.LIVE,
                getTargetTime(0, -1), getTargetTime(0, 1));
            Tournament tournamentEnd = createTournament(2L, TournamentStatus.END,
                getTargetTime(0, -3), getTargetTime(0, -1));
            TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto("test");
            given(tournamentRepository.findById(tournamentLive.getId())).willReturn(Optional.of(tournamentLive));
            given(tournamentRepository.findById(tournamentEnd.getId())).willReturn(Optional.of(tournamentEnd));
            // when, then
            assertThatThrownBy(() -> tournamentAdminService.addTournamentUser(tournamentLive.getId(), requestDto))
                .isInstanceOf(TournamentUpdateException.class);
            assertThatThrownBy(() -> tournamentAdminService.addTournamentUser(tournamentEnd.getId(), requestDto))
                .isInstanceOf(TournamentUpdateException.class);
        }

        @Test
        @DisplayName("찾을_수_없는_유저")
        public void userNotFound() {
            // given
            Tournament tournament = createTournament(1L, TournamentStatus.BEFORE,
                getTargetTime(0, -1), getTargetTime(0, 1));
            TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto("test");
            given(tournamentRepository.findById(1L)).willReturn(Optional.of(tournament));
            given(userRepository.findByIntraId("test")).willReturn(Optional.empty());

            // when then
            assertThatThrownBy(() -> tournamentAdminService.addTournamentUser(tournament.getId(), requestDto))
                .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("이미_해당_토너먼트_참가중인_유저")
        public void alreadyTournamentParticipant() {
            // given
            List<Tournament> tournamentList = createTournaments(1L, 2, getTargetTime(2, 1));
            Tournament tournament = tournamentList.get(0);
            TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto("testUser");
            User user = createUser("testUser");
            TournamentUser tournamentUser = new TournamentUser(user, tournament, true, LocalDateTime.now());
            tournament.addTournamentUser(tournamentUser);
            given(tournamentRepository.findById(1L)).willReturn(Optional.of(tournament));
            given(userRepository.findByIntraId("testUser")).willReturn(Optional.of(user));
            given(tournamentRepository.findAllByStatusIsNot(TournamentStatus.END)).willReturn(tournamentList);

            // when, then
            assertThatThrownBy(() -> tournamentAdminService.addTournamentUser(tournament.getId(), requestDto))
                .isInstanceOf(TournamentConflictException.class);
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