package com.gg.server.utils;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.exception.WinningTeamNotFoundException;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.exception.TeamNotFoundException;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchTestUtils {
    private final UserRepository userRepository;
    private final SeasonRepository seasonRepository;
    private final RankRedisRepository rankRedisRepository;
    private final SlotManagementRepository slotManagementRepository;
    private final GameRepository gameRepository;

    public User createUser() {
        String randomId = UUID.randomUUID().toString().substring(0, 30);
        User user = User.builder()
                .eMail("email")
                .intraId(randomId)
                .racketType(RacketType.PENHOLDER)
                .snsNotiOpt(SnsType.NONE)
                .roleType(RoleType.USER)
                .totalExp(1000)
                .build();
        userRepository.save(user);
        return user;
    }

    public User createGuestUser() {
        String randomId = UUID.randomUUID().toString().substring(0, 30);
        User user = User.builder()
                .eMail("email")
                .intraId(randomId)
                .racketType(RacketType.PENHOLDER)
                .snsNotiOpt(SnsType.NONE)
                .roleType(RoleType.GUEST)
                .totalExp(1000)
                .build();
        userRepository.save(user);
        return user;
    }

    public RankRedis addUsertoRankRedis(Long userId, Integer ppp, Long seasonId) {
        String randomId = UUID.randomUUID().toString();
        RankRedis rankRedis = new RankRedis(userId,  randomId, ppp, 0, 0,"test", "https://42gg-public-image.s3.ap-northeast-2.amazonaws.com/images/nheo.jpeg", "#000000");
        rankRedisRepository.addRankData(RedisKeyManager.getHashKey(seasonId), userId, rankRedis);
        rankRedisRepository.addToZSet(RedisKeyManager.getZSetKey(seasonId), userId, ppp);
        return rankRedis;
    }

    public List<LocalDateTime> getTestSlotTimes(Integer interval) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime standard = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth()
        , now.getHour(), 0);
        List<LocalDateTime> sampleSlots = new ArrayList<LocalDateTime>();
        for (int i = 0; i < 15; i++) {
            if (standard.plusMinutes(interval * i).isAfter(now)) {
                sampleSlots.add(standard.plusMinutes(interval * i));
            }
        }
        return sampleSlots;
    }
    public Season makeTestSeason(Integer pppGap) {
        Optional<Season> currentSeason = seasonRepository.findCurrentSeason(LocalDateTime.now());
        if (currentSeason.isPresent()) {
            return currentSeason.get();
        }
        Season season = new Season(
                "test",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.of(9999, 12, 31, 23, 59, 59),
                1000,
                pppGap
        );
        seasonRepository.save(season);
        return season;
    }

    public SlotManagement makeTestSlotManagement(Integer interval) {
        SlotManagement slotManagement = SlotManagement.builder()
                .futureSlotTime(10)
                .pastSlotTime(0)
                .gameInterval(interval)
                .openMinute(5)
                .startTime(LocalDateTime.now().minusHours(2))
                .build();
        slotManagementRepository.save(slotManagement);
        return slotManagement;
    }

    /**
     * 토너먼트에서 동일한 라운드의 경기들을 매칭 (생성)
     * @param tournament 토너먼트
     * @param round 해당 라운드와 동일한 라운드의 모든 경기를 매칭
     *              ex ) 8강의 경우 8강의 4경기를 매칭
     * @return 매칭된 토너먼트 게임
     */
    public List<TournamentGame> matchTournamentGames(Tournament tournament, TournamentRound round) {
        Season season = seasonRepository.findCurrentSeason(LocalDateTime.now())
            .orElseThrow(() -> new IllegalArgumentException("현재 시즌이 존재하지 않습니다."));
        List<TournamentRound> sameRounds = TournamentRound.getSameRounds(round);
        List<TournamentGame> sameRoundGames = tournament.getTournamentGames().stream()
            .filter(o -> sameRounds.contains(o.getTournamentRound()))
            .sorted(Comparator.comparing(TournamentGame::getTournamentRound))
            .collect(Collectors.toList());
        List<TournamentGame> previousRoundTournamentGames = findSameRoundGames(tournament.getTournamentGames(), TournamentRound.getPreviousRoundNumber(round));

        for (int i = 0; i < round.getRoundNumber() / 2; ++i) {
            Game game = new Game(season, StatusType.BEFORE, Mode.TOURNAMENT, LocalDateTime.now(), LocalDateTime.now());
            Team team1 = new Team(game, -1, false);
            Team team2 = new Team(game, -1, false);
            User user1 = findMatchUser(previousRoundTournamentGames, i * 2, tournament);
            User user2 = findMatchUser(previousRoundTournamentGames, i * 2 + 1, tournament);
            new TeamUser(team1, user1);
            new TeamUser(team2, user2);
            gameRepository.save(game);
            sameRoundGames.get(i).updateGame(game);
        }
        return sameRoundGames;
    }

    /**
     * 여러 경기에 대한 결과 수정
     * @param tournamentGames
     * @param scores
     */
    public void updateTournamentGamesResult(List<TournamentGame> tournamentGames, List<Integer> scores) {
        int sum = scores.stream().mapToInt(Integer::intValue).sum();
        if (sum > 3 || sum < 0) {
            throw new IllegalArgumentException("게임 점수는 0 ~ 3 사이여야 합니다.");
        }
        List<Game> games = tournamentGames.stream().map(TournamentGame::getGame).collect(Collectors.toList());
        for (Game game : games) {
            updateTournamentGameResult(game, scores);
        }
    }

    /**
     * 하나의 경기에 대한 결과 업데이트
     * @param game
     * @param scores
     */
    public void updateTournamentGameResult(Game game, List<Integer> scores) {
        int sum = scores.stream().mapToInt(Integer::intValue).sum();
        if (sum > 3 || sum < 0) {
            throw new IllegalArgumentException("게임 점수는 0 ~ 3 사이여야 합니다.");
        }
        List<Team> teams = game.getTeams();
        teams.get(0).updateScore(scores.get(0), scores.get(0) > scores.get(1));
        teams.get(1).updateScore(scores.get(1), scores.get(0) < scores.get(1));
        // BEFORE -> LIVE -> WAIT -> END
        game.updateStatus();
        game.updateStatus();
        game.updateStatus();

    }

    public Team getWinningTeam(Game game) {
        return game.getTeams().stream()
            .filter(team -> Boolean.TRUE.equals(team.getWin()))
            .findAny()
            .orElseThrow(WinningTeamNotFoundException::new);
    }

    private User findMatchUser(List<TournamentGame> previousTournamentGames, int index, Tournament tournament) {
        if (previousTournamentGames.isEmpty()) {
            return tournament.getTournamentUsers().get(index).getUser();
        }
        Game game = previousTournamentGames.get(index).getGame();
        return getWinningTeam(game)
            .getTeamUsers().get(0).getUser();
    }

    private List<TournamentGame> findSameRoundGames(List<TournamentGame> tournamentGames, int roundNum) {
        return tournamentGames.stream()
            .filter(tournamentGame -> roundNum == tournamentGame.getTournamentRound().getRoundNumber())
            .sorted(Comparator.comparing(TournamentGame::getTournamentRound))
            .collect(Collectors.toList());
    }
}
