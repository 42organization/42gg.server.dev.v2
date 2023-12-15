package com.gg.server.domain.match.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.type.TournamentMatch;
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
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.CustomRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.gg.server.domain.match.exception.SlotNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.gg.server.domain.match.type.TournamentMatch.*;


// TODO transactional 고민해보기
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
     * @throws IllegalArgumentException 토너먼트 게임이 아닐 경우
     */
    public TournamentMatch checkTournamentGame(Game game) {
        TournamentGame tournamentGame = tournamentGameRepository.findByGameId(game.getId())
            .orElseThrow(() -> new CustomRuntimeException("토너먼트 게임이 아닙니다.", ErrorCode.TOURNAMENT_NOT_FOUND));     // TODO : custom exception

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
     * @throws NullPointerException round의 이전 라운드가 존재하지 않을 경우
     */
    public void matchGames(Tournament tournament, TournamentRound round) {
        if (isAlreadyExistMatchedGame(tournament, round)) {
            throw new CustomRuntimeException("이미 매칭된 게임이 존재합니다.", ErrorCode.TOURNAMENT_NOT_FOUND);      // TODO 수정
        }
        Season season = seasonFindService.findCurrentSeason(tournament.getStartTime());
        SlotManagement slotManagement = slotManagementRepository.findCurrent(tournament.getStartTime())
            .orElseThrow(SlotNotFoundException::new);
        int gameInterval = slotManagement.getGameInterval();
        int previousRoundNumber = TournamentRound.getPreviousRoundNumber(round);
        List<TournamentGame> tournamentGames = tournament.findSameRoundNumTournamentGames(round.getRoundNumber());
        List<TournamentGame> previousRoundTournamentGames = tournament.findSameRoundNumTournamentGames(previousRoundNumber);

        for (int i = 0; i < tournamentGames.size(); ++i) {
            LocalDateTime startTime = tournament.getStartTime().plusMinutes((long) gameInterval * i);
            Game game = new Game(season, StatusType.BEFORE, Mode.TOURNAMENT, startTime, startTime.plusMinutes(gameInterval));
            Team team1 = new Team(game, -1, false);
            Team team2 = new Team(game, -1, false);
            User user1 = getTeamUser(previousRoundTournamentGames, i * 2, tournament);
            User user2 = getTeamUser(previousRoundTournamentGames, i * 2 + 1, tournament);
            new TeamUser(team1, user1);
            new TeamUser(team2, user2);
            gameRepository.save(game);
            tournamentGames.get(i).updateGame(game);
        }
    }

    /**
     * 토너먼트 게임의 승자를 토너먼트 다음 라운드의 게임 플레이어로 업데이트
     * @param game 경기 결과가 수정된 토너먼트 게임
     * @param nextMatchedGame 수정된 우승자로 수정할 다음 게임
     */
    public void updateMatchedGameUser(Game game, Game nextMatchedGame) {
        List<Long> teamIds = game.getTeams().stream().map(Team::getId).collect(Collectors.toList());
        Team winningTeam = game.getWinningTeam().orElseThrow();     // TODO : throw exception

        List<Team> nextMatchedGameTeams = nextMatchedGame.getTeams();
        for (Team team : nextMatchedGameTeams) {
            User teamUser = team.getTeamUsers().get(0).getUser();
            if (teamIds.contains(teamUser.getId())) {
                team.getTeamUsers().get(0).updateUser(winningTeam.getTeamUsers().get(0).getUser());
            }
        }
    }

    private User getTeamUser(List<TournamentGame> previousTournamentGames, int index, Tournament tournament) {
        if (previousTournamentGames.isEmpty()) {
            return tournament.getTournamentUsers().get(index).getUser();
        }
        return previousTournamentGames.get(index).getGame().getWinningTeam()
            .orElseThrow(() -> new IllegalArgumentException("이전 라운드의 승자가 존재하지 않습니다."))
            .getTeamUsers().get(0).getUser();
    }

    /**
     * round에 매칭된 게임이 이미 존재하는지 확인
     * @param tournament 토너먼트
     * @param round 토너먼트 라운드
     * @return true - 매칭된 게임이 존재, false - 아직 매칭된 게임이 존재하지 않음
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
     */
    private void closeTournament(Tournament tournament, Game finalGame) {
        User winner = finalGame.getWinningTeam()
            .orElseThrow(() -> new CustomRuntimeException("승자가 존재하지 않습니다.", ErrorCode.TOURNAMENT_NOT_FOUND))
            .getTeamUsers().get(0).getUser();
        tournament.updateStatus(TournamentStatus.END);
        tournament.updateWinner(winner);

    }
}
