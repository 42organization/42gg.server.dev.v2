package com.gg.server.domain.match.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamUser;
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
    @DisplayName("토너먼트 생성 테스트")
    class MatchTournament {
        @Test
        @DisplayName("8강 경기 매칭 테스트")
        public void quarterTest() {
            // when
            mathTournamentService.matchTournamentGame(tournament, TournamentRound.QUARTER_FINAL_1);

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
            // 8강 경기 결과 입력
            List<TournamentRound> quarterRounds = TournamentRound.getSameRounds(TournamentRound.QUARTER_FINAL_1);
            List<TournamentGame> quarterRoundGames = tournamentGameRepository.findAllByTournamentId(tournament.getId()).stream()
                .filter(o -> quarterRounds.contains(o.getTournamentRound()))
                .sorted(Comparator.comparing(TournamentGame::getTournamentRound))
                .collect(Collectors.toList());
            Game game = null;
            for (int i = 0; i < Tournament.ALLOWED_JOINED_NUMBER / 2; ++i) {
                game = new Game(season, StatusType.END, Mode.TOURNAMENT, LocalDateTime.now(), LocalDateTime.now());
                Team team1 = new Team(game, 2, true);
                Team team2 = new Team(game, 0, false);
                new TeamUser(team1, tournament.getTournamentUsers().get(i * 2).getUser());
                new TeamUser(team2, tournament.getTournamentUsers().get(i * 2 + 1).getUser());
                gameRepository.save(game);
                quarterRoundGames.get(i).updateGame(game);
            }

            // when
            mathTournamentService.checkTournamentGame(game);

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
            List<TournamentRound> rounds = TournamentRound.getSameRounds(TournamentRound.QUARTER_FINAL_1);
            rounds.addAll(TournamentRound.getSameRounds(TournamentRound.SEMI_FINAL_1));
            List<TournamentGame> roundGames = tournamentGameRepository.findAllByTournamentId(tournament.getId()).stream()
                .filter(o -> rounds.contains(o.getTournamentRound()))
                .collect(Collectors.toList());
            Game game = null;
            for (int i = 0; i < Tournament.ALLOWED_JOINED_NUMBER / 2; ++i) {
                game = new Game(season, StatusType.END, Mode.TOURNAMENT, LocalDateTime.now(), LocalDateTime.now());
                Team team1 = new Team(game, 2, true);
                Team team2 = new Team(game, 0, false);
                new TeamUser(team1, tournament.getTournamentUsers().get(i * 2).getUser());
                new TeamUser(team2, tournament.getTournamentUsers().get(i * 2 + 1).getUser());
                gameRepository.save(game);
                roundGames.get(i).updateGame(game);
            }
            for (int i = 0; i < Tournament.ALLOWED_JOINED_NUMBER / 4; ++i) {
                game = new Game(season, StatusType.END, Mode.TOURNAMENT, LocalDateTime.now(), LocalDateTime.now());
                Team team1 = new Team(game, 2, true);
                Team team2 = new Team(game, 0, false);
                new TeamUser(team1, tournament.getTournamentUsers().get(i * 2).getUser());
                new TeamUser(team2, tournament.getTournamentUsers().get(i * 2 + 1).getUser());
                gameRepository.save(game);
                roundGames.get(i + Tournament.ALLOWED_JOINED_NUMBER / 2).updateGame(game);
            }

            // when
            mathTournamentService.checkTournamentGame(game);

            // then
            // 1개의 결승 경기가 생성되었는지 확인
            TournamentGame finalRoundGame = tournamentGameRepository.findAllByTournamentId(tournament.getId()).stream()
                .filter(o -> TournamentRound.THE_FINAL.equals(o.getTournamentRound())).findAny().orElse(null);
            assertThat(finalRoundGame.getGame()).isNotNull();
        }

        @Test
        @DisplayName("결승 경기 점수 입력 후 토너먼트 END 상태로 업데이트 테스트")
        public void finalEndTest() {
            // given
            // 8강 & 4강 & 결승 경기 결과 입력
            List<TournamentGame> tournamentGames = tournamentGameRepository.findAllByTournamentId(tournament.getId()).stream()
                .sorted(Comparator.comparing(TournamentGame::getTournamentRound))
                .collect(Collectors.toList());
            for (int i = 0; i < Tournament.ALLOWED_JOINED_NUMBER / 2; ++i) {
                Game game = new Game(season, StatusType.END, Mode.TOURNAMENT, LocalDateTime.now(), LocalDateTime.now());
                Team team1 = new Team(game, 2, true);
                Team team2 = new Team(game, 0, false);
                new TeamUser(team1, tournament.getTournamentUsers().get(i * 2).getUser());
                new TeamUser(team2, tournament.getTournamentUsers().get(i * 2 + 1).getUser());
                gameRepository.save(game);
                tournamentGames.get(1 + i + Tournament.ALLOWED_JOINED_NUMBER / 4).updateGame(game);
            }
            for (int i = 0; i < Tournament.ALLOWED_JOINED_NUMBER / 4; ++i) {
                Game game = new Game(season, StatusType.END, Mode.TOURNAMENT, LocalDateTime.now(), LocalDateTime.now());
                Team team1 = new Team(game, 2, true);
                Team team2 = new Team(game, 0, false);
                new TeamUser(team1, tournament.getTournamentUsers().get(i * 2).getUser());
                new TeamUser(team2, tournament.getTournamentUsers().get(i * 2 + 1).getUser());
                gameRepository.save(game);
                tournamentGames.get(1 + i).updateGame(game);
            }
            Game game = new Game(season, StatusType.END, Mode.TOURNAMENT, LocalDateTime.now(), LocalDateTime.now());
            Team team1 = new Team(game, 2, true);
            Team team2 = new Team(game, 0, false);
            new TeamUser(team1, tournament.getTournamentUsers().get(0).getUser());
            new TeamUser(team2, tournament.getTournamentUsers().get(1).getUser());
            gameRepository.save(game);
            tournamentGames.get(0).updateGame(game);

            // when
            mathTournamentService.checkTournamentGame(game);

            // then
            // 토너먼트 상태가 END로 변경되었는지
            // winner가 존재하는지 확인
            assertThat(tournament.getStatus()).isEqualTo(TournamentStatus.END);
            assertThat(tournament.getWinner()).isNotNull();
        }
    }
}
