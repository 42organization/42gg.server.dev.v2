package com.gg.server.domain.match.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.type.TournamentMatch;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentGameRepository;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.user.data.User;
import com.gg.server.utils.TestDataUtils;
import com.gg.server.utils.annotation.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@SpringBootTest
@Transactional
public class MatchTournamentTest {
    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    SlotManagementRepository slotManagementRepository;
    @Autowired
    TournamentGameRepository tournamentGameRepository;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    MatchTournamentService mathTournamentService;
    @Autowired
    MatchTestUtils matchTestUtils;

    Tournament tournament;
    List<TournamentGame> tournamentGameList;
    Season season;
    SlotManagement slotManagement;

    @BeforeEach
    public void beforeEach() {
        season = testDataUtils.createSeason();
        slotManagement = SlotManagement.builder()
            .pastSlotTime(0)
            .futureSlotTime(0)
            .openMinute(0)
            .gameInterval(15)
            .startTime(LocalDateTime.now().minusHours(1))
            .build();
        slotManagementRepository.save(slotManagement);
        tournament = testDataUtils.createTournamentWithUser(Tournament.ALLOWED_JOINED_NUMBER, 4, "test");
        tournamentGameList = testDataUtils.createTournamentGameList(tournament, 7);
        for (TournamentGame tournamentGame : tournamentGameList) {
            tournament.addTournamentGame(tournamentGame);
        }
    }

    @Nested
    @DisplayName("토너먼트 라운드별 경기 생성 테스트")
    class MatchTournament {
        @Test
        @DisplayName("8강 경기 매칭 테스트")
        public void quarterTest() {
            // when
            mathTournamentService.matchGames(tournament, TournamentRound.QUARTER_FINAL_1);

            // then
            List<TournamentRound> quarterRounds = TournamentRound.getSameRounds(TournamentRound.QUARTER_FINAL_1);
            List<TournamentGame> quarterRoundGames = tournamentGameRepository.findAllByTournamentId(tournament.getId()).stream()
                .filter(o -> quarterRounds.contains(o.getTournamentRound()))
                .collect(Collectors.toList());
            // 4개의 8강 경기가 생성되었는지 확인
            assertThat(quarterRoundGames.size()).isEqualTo(Tournament.ALLOWED_JOINED_NUMBER / 2);
            for (TournamentGame tournamentGame : quarterRoundGames) {
                assertThat(tournamentGame.getGame()).isNotNull();
                assertThat(tournamentGame.getGame().getStatus()).isEqualTo(StatusType.BEFORE);
            }
        }

        @Test
        @DisplayName("4강 경기 매칭 테스트")
        public void semiTest() {
            // given
            // 8강 경기 결과
            List<TournamentGame> tournamentGames = matchTestUtils.matchTournamentGames(tournament, TournamentRound.QUARTER_FINAL_1);
            matchTestUtils.updateTournamentGamesResult(tournamentGames, List.of(2, 0));

            // when
            mathTournamentService.matchGames(tournament, TournamentRound.SEMI_FINAL_1);

            // then
            List<TournamentRound> semiRounds = TournamentRound.getSameRounds(TournamentRound.SEMI_FINAL_1);
            List<TournamentGame> semiRoundGames = tournamentGameRepository.findAllByTournamentId(tournament.getId()).stream()
                .filter(o -> semiRounds.contains(o.getTournamentRound()))
                .sorted(Comparator.comparing(TournamentGame::getTournamentRound))
                .collect(Collectors.toList());
            // 2개의 4강 경기가 생성되었는지 확인
            assertThat(semiRoundGames.size()).isEqualTo(Tournament.ALLOWED_JOINED_NUMBER / 4);
            for (TournamentGame tournamentGame : semiRoundGames) {
                assertThat(tournamentGame.getGame()).isNotNull();
                assertThat(tournamentGame.getGame().getStatus()).isEqualTo(StatusType.BEFORE);
            }
            // 8강에서 이긴 유저끼리 4강에 매칭되었는지 확인
            List<TournamentRound> quarterRounds = TournamentRound.getSameRounds(TournamentRound.QUARTER_FINAL_1);
            List<TournamentGame> quarterRoundGames = tournamentGameRepository.findAllByTournamentId(tournament.getId()).stream()
                .filter(o -> quarterRounds.contains(o.getTournamentRound()))
                .sorted(Comparator.comparing(TournamentGame::getTournamentRound))
                .collect(Collectors.toList());
            List<User> semiTeams = new ArrayList<>();
            List<User> quarterWinningTeams = new ArrayList<>();
            for(TournamentGame semiRoundGame : semiRoundGames) {
                semiTeams.add(semiRoundGame.getGame().getTeams().get(0).getTeamUsers().get(0).getUser());
                semiTeams.add(semiRoundGame.getGame().getTeams().get(1).getTeamUsers().get(0).getUser());
            }
            for (TournamentGame quarterRoundGame : quarterRoundGames) {
                quarterWinningTeams.add(quarterRoundGame.getGame().getWinningTeam().get().getTeamUsers().get(0).getUser());
            }
            assertThat(semiTeams).contains(quarterWinningTeams.get(0));
            assertThat(semiTeams).contains(quarterWinningTeams.get(1));
        }

        @Test
        @DisplayName("결승 경기 매칭 테스트")
        public void finalTest() {
            // given
            // 8강 & 4강 경기 결과 입력
            List<TournamentGame> tournamentGames = matchTestUtils.matchTournamentGames(tournament, TournamentRound.QUARTER_FINAL_1);
            tournamentGames.addAll(matchTestUtils.matchTournamentGames(tournament, TournamentRound.SEMI_FINAL_1));
            matchTestUtils.updateTournamentGamesResult(tournamentGames, List.of(2, 0));

            // when
            mathTournamentService.matchGames(tournament, TournamentRound.THE_FINAL);

            // then
            // 1개의 결승 경기가 생성되었는지 확인
            TournamentGame finalRoundGame = tournamentGameRepository.findAllByTournamentId(tournament.getId()).stream()
                .filter(o -> TournamentRound.THE_FINAL.equals(o.getTournamentRound())).findAny().orElse(null);
            assertThat(finalRoundGame.getGame()).isNotNull();
        }
    }

    @Nested
    @DisplayName("토너먼트 종료 테스트")
    class CloseTournament {
        @Test
        @DisplayName("결승 경기 점수 입력 후 토너먼트 END 상태로 업데이트 테스트")
        public void finalEndTest() {
            // given
            // 8강 & 4강 & 결승 경기 결과 입력
            List<TournamentGame> tournamentGames = matchTestUtils.matchTournamentGames(tournament, TournamentRound.QUARTER_FINAL_1);
            tournamentGames.addAll(matchTestUtils.matchTournamentGames(tournament, TournamentRound.SEMI_FINAL_1));
            matchTestUtils.updateTournamentGamesResult(tournamentGames, List.of(2, 0));
            Game finalGame = matchTestUtils.matchTournamentGames(tournament, TournamentRound.THE_FINAL).get(0).getGame();
            matchTestUtils.updateTournamentGameResult(finalGame, List.of(2, 0));

            // when
            TournamentMatch tournamentMatch = mathTournamentService.checkTournamentGame(finalGame);

            // then
            // 토너먼트 상태가 END로 변경되었는지
            // winner가 존재하는지 확인
            assertThat(TournamentMatch.IMPOSSIBLE).isEqualTo(tournamentMatch);
            assertThat(tournament.getStatus()).isEqualTo(TournamentStatus.END);
            assertThat(tournament.getWinner()).isNotNull();
        }
    }

    @Nested
    @DisplayName("위너 변경에 따른 다음 경기 팀 변경 테스트")
    class ChangeTeam {
        @Test
        @DisplayName("8강 경기에서 4강 경기로 팀 변경 테스트")
        public void quarterToSemiTest() {
            // given
            // 8강 경기 결과 + 4강 매칭
            List<TournamentGame> tournamentGames = matchTestUtils.matchTournamentGames(tournament, TournamentRound.QUARTER_FINAL_1);
            matchTestUtils.updateTournamentGamesResult(tournamentGames, List.of(2, 0));
            matchTestUtils.matchTournamentGames(tournament, TournamentRound.SEMI_FINAL_1);
            Game game = tournamentGames.get(0).getGame();
            TournamentRound nextRound = tournamentGames.get(0).getTournamentRound().getNextRound();
            Game nextMatchedGame = tournamentGameRepository.findByTournamentIdAndTournamentRound(tournament.getId(), nextRound)
                .orElseThrow(() -> new IllegalArgumentException("다음 경기가 존재하지 않습니다.")).getGame();

            // when
            List<Team> beforeNextGameTeams = nextMatchedGame.getTeams();
            List<Team> beforeGameTeams = game.getTeams();
            beforeGameTeams.get(0).updateScore(1, false);
            beforeGameTeams.get(1).updateScore(2, true);
            mathTournamentService.updateMatchedGameUser(game, nextMatchedGame);

            // then
            // 8강 경기에서 이긴 팀이 4강 경기로 변경되었는지 확인
            List<Team> afterNextGameTeams = nextMatchedGame.getTeams();
            for (Team team : afterNextGameTeams) {
            }

        }
    }
}
