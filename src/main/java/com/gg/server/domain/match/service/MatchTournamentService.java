package com.gg.server.domain.match.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.exception.EnrolledSlotException;
import com.gg.server.domain.match.exception.WinningTeamNotFoundException;
import com.gg.server.domain.match.type.TournamentMatchStatus;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.service.SeasonFindService;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentGameRepository;
import com.gg.server.domain.tournament.exception.TournamentGameNotFoundException;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.user.data.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.gg.server.domain.match.exception.SlotNotFoundException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.gg.server.domain.match.type.TournamentMatchStatus.*;


@Service
@RequiredArgsConstructor
public class MatchTournamentService {
    private final TournamentGameRepository tournamentGameRepository;
    private final GameRepository gameRepository;
    private final SlotManagementRepository slotManagementRepository;
    private final SeasonFindService seasonFindService;

    /**
     * 토너먼트 진행중 다음 라운드 게임 매칭이 필요한지 확인
     * <p> 결승전 점수 입력 후 토너먼트 END 상태로 업데이트 </p>
     * @param game 토너먼트 게임
     * @return TournamentMatchStatus - 매칭 가능 여부
     * @throws TournamentGameNotFoundException 토너먼트 게임이 존재하지 않을 경우
     */
    @Transactional
    public TournamentMatchStatus checkTournamentGame(Game game) {
        TournamentGame tournamentGame = tournamentGameRepository.findByGameId(game.getId())
            .orElseThrow(TournamentGameNotFoundException::new);

        // 토너먼트 결승전 게임일 경우, 토너먼트 상태 END로 변경
        if (TournamentRound.THE_FINAL.equals(tournamentGame.getTournamentRound())) {
            closeTournament(tournamentGame.getTournament(), game);
            return IMPOSSIBLE;
        }

        // 같은 round의 모든 게임이 END인 경우, 다음 round의 토너먼트 게임 매칭 가능
        TournamentRound round = tournamentGame.getTournamentRound();
        List<TournamentGame> tournamentGames = tournamentGameRepository.findAllByTournamentId(tournamentGame.getTournament().getId());
        List<TournamentGame> sameRoundGames = tournamentGames.stream()
            .filter(tg -> tg.getTournamentRound().getRoundNumber() == round.getRoundNumber())
            .collect(Collectors.toList());
        for (TournamentGame tg : sameRoundGames) {
            if (!StatusType.END.equals(tg.getGame().getStatus())) {
                return IMPOSSIBLE;
            }
        }
        if (isAlreadyExistMatchedGame(tournamentGame.getTournament(), round.getNextRound())) {
            return ALREADY_MATCHED;
        }
        return POSSIBLE;
    }


    /**
     * 토너먼트 게임 매칭
     * @param tournament 토너먼트
     * @param round 새로 매칭할 토너먼트 라운드
     * @throws EnrolledSlotException 이미 매칭된 게임이 존재할 경우
     * @throws SlotNotFoundException 슬롯이 존재하지 않을 경우
     */
    @Transactional
    public void matchGames(Tournament tournament, TournamentRound round) {
        if (isAlreadyExistMatchedGame(tournament, round)) {
            throw new EnrolledSlotException();
        }
        Season season = seasonFindService.findCurrentSeason(tournament.getStartTime());
        SlotManagement slotManagement = slotManagementRepository.findCurrent(tournament.getStartTime())
            .orElseThrow(SlotNotFoundException::new);
        int gameInterval = slotManagement.getGameInterval();
        List<TournamentGame> allTournamentGames = tournamentGameRepository.findAllByTournamentId(tournament.getId());
        List<TournamentGame> tournamentGames = findSameRoundGames(allTournamentGames, round.getRoundNumber());
        List<TournamentGame> previousRoundTournamentGames = findSameRoundGames(allTournamentGames, TournamentRound.getPreviousRoundNumber(round));
        LocalDateTime startTime = calculateStartTime(tournament, round, gameInterval);

        for (int i = 0; i < tournamentGames.size(); ++i) {
            startTime = startTime.plusMinutes((long) gameInterval * i);
            Game game = new Game(season, StatusType.BEFORE, Mode.TOURNAMENT, startTime, startTime.plusMinutes(gameInterval));
            Team team1 = new Team(game, -1, false);
            Team team2 = new Team(game, -1, false);
            User user1 = findMatchUser(previousRoundTournamentGames, i * 2, tournament);
            User user2 = findMatchUser(previousRoundTournamentGames, i * 2 + 1, tournament);
            new TeamUser(team1, user1);
            new TeamUser(team2, user2);
            gameRepository.save(game);
            tournamentGames.get(i).updateGame(game);
        }
    }

    /**
     * 토너먼트 게임의 승자를 토너먼트 다음 라운드의 게임 플레이어로 업데이트
     * @param modifiedGame 경기 결과가 수정된 토너먼트 게임
     * @param nextMatchedGame 수정된 우승자로 수정할 다음 게임
     * @throws WinningTeamNotFoundException 우승팀이 존재하지 않을 경우
     */
    @Transactional
    public void updateMatchedGameUser(Game modifiedGame, Game nextMatchedGame) {
        User winner = getWinningTeam(modifiedGame).getTeamUsers().get(0).getUser();
        List<User> players = modifiedGame.getTeams().stream()
            .map(team -> team.getTeamUsers().get(0).getUser())
            .collect(Collectors.toList());
        List<TeamUser> nextMatchedGameTeamUsers = nextMatchedGame.getTeams().stream()
            .map(team -> team.getTeamUsers().get(0))
            .collect(Collectors.toList());
        for (TeamUser nextGameTeamUser : nextMatchedGameTeamUsers) {
            if (players.contains(nextGameTeamUser.getUser())) {
                nextGameTeamUser.updateUser(winner);
                break;
            }
        }
    }

    /**
     * @param tournament 토너먼트
     * @param round 토너먼트 라운드
     * @param gameInterval 경기 간격
     * @return 마지막 경기 종료 시간 + interval
     * <p>8강의 경우 토너먼트 시작 시간</p>
     * <p>4강, 결승일 경우 이전 라운드의 마지막 경기 종료 시간 + 15분</p>
     */
    private LocalDateTime calculateStartTime(Tournament tournament, TournamentRound round, int gameInterval) {
        if (TournamentRound.QUARTER_FINAL_1.getRoundNumber() == round.getRoundNumber()) {
            return tournament.getStartTime();
        }
        List<TournamentGame> previousRoundTournamentGames = findSameRoundGames(tournament.getTournamentGames(), TournamentRound.getPreviousRoundNumber(round));
        TournamentGame lastGame = previousRoundTournamentGames.get(previousRoundTournamentGames.size() - 1);
        return lastGame.getGame().getEndTime().plusMinutes(gameInterval);
    }

    private User findMatchUser(List<TournamentGame> previousTournamentGames, int index, Tournament tournament) {
        if (previousTournamentGames.isEmpty()) {
            return tournament.getTournamentUsers().get(index).getUser();
        }
        return getWinningTeam(previousTournamentGames.get(index).getGame())
            .getTeamUsers().get(0).getUser();
    }

    /**
     * round에 매칭된 게임이 이미 존재하는지 확인
     * @param tournament 토너먼트
     * @param round 토너먼트 라운드
     * @return true - 매칭된 게임이 존재, false - 아직 매칭된 게임이 존재하지 않음
     * @throws TournamentGameNotFoundException 토너먼트 게임이 존재하지 않을 경우
     */
    private boolean isAlreadyExistMatchedGame(Tournament tournament, TournamentRound round) {
        TournamentGame tournamentGame = tournamentGameRepository.findByTournamentIdAndTournamentRound(tournament.getId(), round)
            .orElseThrow(TournamentGameNotFoundException::new);
        return tournamentGame.getGame() != null;
    }

    /**
     * 토너먼트 종료시키는 함수
     * <p> 토너먼트 상태 END로 업데이트 </p>
     * <p> 토너먼트 winner 업데이트 </p>
     * @param tournament 종료할 토너먼트
     * @param finalGame 토너먼트의 마지막 게임
     * @throws WinningTeamNotFoundException 우승팀이 존재하지 않을 경우
     */
    private void closeTournament(Tournament tournament, Game finalGame) {
        User winner = getWinningTeam(finalGame)
            .getTeamUsers().get(0).getUser();
        tournament.updateStatus(TournamentStatus.END);
        tournament.updateWinner(winner);

    }

    /**
     * 같은 round의 토너먼트 게임을 찾는다.
     * @param tournamentGames - 토너먼트 게임 List
     * @param roundNum - 토너먼트 라운드 number
     * @return - 같은 roundNum의 tournamentGame List
     */
    private List<TournamentGame> findSameRoundGames(List<TournamentGame> tournamentGames, int roundNum) {
        return tournamentGames.stream()
            .filter(tournamentGame -> roundNum == tournamentGame.getTournamentRound().getRoundNumber())
            .sorted(Comparator.comparing(TournamentGame::getTournamentRound))
            .collect(Collectors.toList());
    }

    /**
     * game의 승자를 찾는다.
     * @param game
     * @return
     */
    private Team getWinningTeam(Game game) {
        return game.getTeams().stream()
            .filter(team -> Boolean.TRUE.equals(team.getWin()))
            .findAny()
            .orElseThrow(WinningTeamNotFoundException::new);
    }
}
