package gg.pingpong.api.user.match.service;

import static com.gg.server.data.game.type.RoundNumber.*;
import static com.gg.server.data.match.type.TournamentMatchStatus.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.gg.server.admin.noti.dto.SendNotiAdminRequestDto;
import com.gg.server.admin.noti.service.NotiAdminService;
import com.gg.server.data.game.Game;
import com.gg.server.data.game.Season;
import com.gg.server.data.game.Team;
import com.gg.server.data.game.TeamUser;
import com.gg.server.data.game.Tournament;
import com.gg.server.data.game.TournamentGame;
import com.gg.server.data.game.TournamentUser;
import com.gg.server.data.game.type.Mode;
import com.gg.server.data.game.type.RoundNumber;
import com.gg.server.data.game.type.StatusType;
import com.gg.server.data.game.type.TournamentRound;
import com.gg.server.data.game.type.TournamentStatus;
import com.gg.server.data.match.type.TournamentMatchStatus;
import com.gg.server.data.noti.type.NotiType;
import com.gg.server.data.user.User;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.match.exception.EnrolledSlotException;
import com.gg.server.domain.match.exception.LosingTeamNotFoundException;
import com.gg.server.domain.match.exception.SlotNotFoundException;
import com.gg.server.domain.match.exception.WinningTeamNotFoundException;
import com.gg.server.domain.season.service.SeasonFindService;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.tournament.data.TournamentGameRepository;
import com.gg.server.domain.tournament.exception.TournamentGameNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchTournamentService {
	private final TournamentGameRepository tournamentGameRepository;
	private final GameRepository gameRepository;
	private final SlotManagementRepository slotManagementRepository;
	private final SeasonFindService seasonFindService;
	private final NotiAdminService notiAdminService;

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
			return NO_MORE_MATCHES;
		}

		// 같은 round의 모든 게임이 END인 경우, 다음 round의 토너먼트 게임 매칭 가능
		TournamentRound round = tournamentGame.getTournamentRound();
		List<TournamentGame> tournamentGames = tournamentGameRepository.findAllByTournamentId(
			tournamentGame.getTournament().getId());
		List<TournamentGame> sameRoundGames = tournamentGames.stream()
			.filter(tg -> tg.getTournamentRound().getRoundNumber() == round.getRoundNumber())
			.collect(Collectors.toList());
		for (TournamentGame tg : sameRoundGames) {
			if (!StatusType.END.equals(tg.getGame().getStatus())) {
				return UNNECESSARY;
			}
		}
		if (isAlreadyExistMatchedGame(tournamentGame.getTournament(), round.getNextRound().getRoundNumber())) {
			return ALREADY_MATCHED;
		}
		return REQUIRED;
	}

	/**
	 * 토너먼트 게임 매칭
	 * @param tournament 토너먼트
	 * @param roundNumber 새로 매칭할 토너먼트 라운드
	 * @throws EnrolledSlotException 이미 매칭된 게임이 존재할 경우
	 * @throws SlotNotFoundException 슬롯이 존재하지 않을 경우
	 */
	@Transactional
	public void matchGames(Tournament tournament, RoundNumber roundNumber) {
		if (isAlreadyExistMatchedGame(tournament, roundNumber)) {
			throw new EnrolledSlotException();
		}
		Season season = seasonFindService.findCurrentSeason(tournament.getStartTime());
		SlotManagement slotManagement = slotManagementRepository.findCurrent(tournament.getStartTime())
			.orElseThrow(SlotNotFoundException::new);
		int gameInterval = slotManagement.getGameInterval();
		List<TournamentGame> allTournamentGames = tournamentGameRepository.findAllByTournamentId(tournament.getId());
		List<TournamentGame> tournamentGames = findSameRoundGames(allTournamentGames, roundNumber);
		List<User> players = findSortedPlayers(tournament, roundNumber);
		LocalDateTime startTime = calculateStartTime(tournament, roundNumber, gameInterval);

		for (int i = 0; i < tournamentGames.size(); ++i) {
			Game game = new Game(season, StatusType.BEFORE, Mode.TOURNAMENT, startTime,
				startTime.plusMinutes(gameInterval));
			Team team1 = new Team(game, -1, false);
			Team team2 = new Team(game, -1, false);
			User user1 = players.get(i * 2);
			User user2 = players.get(i * 2 + 1);
			new TeamUser(team1, user1);
			new TeamUser(team2, user2);
			gameRepository.save(game);
			tournamentGames.get(i).updateGame(game);
			startTime = startTime.plusMinutes((long)gameInterval);
		}
		players.stream().distinct()
			.forEach(user -> notiAdminService.sendAnnounceNotiToUser(
				new SendNotiAdminRequestDto(user.getIntraId(), NotiType.TOURNAMENT_GAME_MATCHED.getMessage())));
	}

	/**
	 * 토너먼트 게임의 승자를 토너먼트 다음 라운드의 게임 플레이어로 업데이트
	 * @param modifiedGame 경기 결과가 수정된 토너먼트 게임
	 * @param nextMatchedGame 수정된 우승자로 수정할 다음 게임
	 * @throws WinningTeamNotFoundException 우승팀이 존재하지 않을 경우
	 * @throws LosingTeamNotFoundException 패자팀이 존재하지 않을 경우
	 */
	@Transactional
	public void updateMatchedGameUser(Game modifiedGame, Game nextMatchedGame) {
		User winner = getWinningTeam(modifiedGame).getTeamUsers().get(0).getUser();
		User loser = getLosingTeam(modifiedGame).getTeamUsers().get(0).getUser();
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
		notiAdminService.sendAnnounceNotiToUser(
			new SendNotiAdminRequestDto(winner.getIntraId(), NotiType.TOURNAMENT_GAME_MATCHED.getMessage()));
		notiAdminService.sendAnnounceNotiToUser(
			new SendNotiAdminRequestDto(loser.getIntraId(), NotiType.TOURNAMENT_GAME_CANCELED.getMessage()));
	}

	/**
	 * @param tournament 토너먼트
	 * @param roundNumber 토너먼트 라운드
	 * @param gameInterval 경기 간격
	 * @return 마지막 경기 종료 시간 + interval
	 * <p>8강의 경우 토너먼트 시작 시간</p>
	 * <p>4강, 결승일 경우 이전 라운드의 마지막 경기 종료 시간 + 15분</p>
	 */
	private LocalDateTime calculateStartTime(Tournament tournament, RoundNumber roundNumber, int gameInterval) {
		if (QUARTER_FINAL == roundNumber) {
			return tournament.getStartTime();
		}
		List<TournamentGame> previousRoundTournamentGames = findSameRoundGames(tournament.getTournamentGames(),
			TournamentRound.getPreviousRoundNumber(roundNumber));
		TournamentGame lastGame = previousRoundTournamentGames.get(previousRoundTournamentGames.size() - 1);
		return lastGame.getGame().getEndTime().plusMinutes(gameInterval);
	}

	/**
	 * 토너먼트 라운드에 매칭될 플레이어를 찾는다.
	 * @param tournament 토너먼트
	 * @param roundNumber 매칭할 토너먼트 라운드
	 * @return 토너먼트 라운드에 매칭될 플레이어 List (정렬된 상태)
	 */
	private List<User> findSortedPlayers(Tournament tournament, RoundNumber roundNumber) {
		List<User> players = new ArrayList<>();

		if (QUARTER_FINAL == roundNumber) {
			Map<Integer, Integer> randomNumbers = new LinkedHashMap<>();
			Random random = new Random();
			while (randomNumbers.size() < Tournament.ALLOWED_JOINED_NUMBER) {
				int randomNumber = random.nextInt(Tournament.ALLOWED_JOINED_NUMBER);
				if (!randomNumbers.containsValue(randomNumber)) {
					randomNumbers.put(randomNumbers.size(), randomNumber);
				}
			}
			for (Integer randomNumber : randomNumbers.values()) {
				List<TournamentUser> tournamentUsers = tournament.getTournamentUsers();
				User user = tournamentUsers.get(randomNumber).getUser();
				players.add(user);
			}
		} else {
			List<TournamentGame> previousRoundTournamentGames = findSameRoundGames(tournament.getTournamentGames(),
				TournamentRound.getPreviousRoundNumber(roundNumber));
			int roundNum = roundNumber.getRound();
			for (int i = 0; i < roundNum; ++i) {
				User user = getWinningTeam(previousRoundTournamentGames.get(i).getGame())
					.getTeamUsers().get(0).getUser();
				players.add(user);
			}
		}
		return players;
	}

	/**
	 * round에 매칭된 게임이 이미 존재하는지 확인
	 * @param tournament 토너먼트
	 * @param roundNumber 토너먼트 라운드
	 * @return true - 매칭된 게임이 존재, false - 아직 매칭된 게임이 존재하지 않음
	 * @throws TournamentGameNotFoundException 토너먼트 게임이 존재하지 않을 경우
	 */
	private boolean isAlreadyExistMatchedGame(Tournament tournament, RoundNumber roundNumber) {
		List<TournamentRound> sameRounds = TournamentRound.getSameRounds(roundNumber);
		List<TournamentGame> tournamentGames = tournamentGameRepository.findByTournamentIdAndTournamentRoundIn(
			tournament.getId(), sameRounds);
		for (TournamentGame tournamentGame : tournamentGames) {
			if (tournamentGame.getGame() != null) {
				return true;
			}
		}
		return false;
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
		tournament.updateEndTime(finalGame.getEndTime());
		tournament.updateWinner(winner);

	}

	/**
	 * 같은 round의 토너먼트 게임을 찾는다.
	 * @param tournamentGames 토너먼트 게임 List
	 * @param roundNum 토너먼트 라운드 number (2, 4, 8, ...) (잘못된 roundNum일 경우 Empty List 반환한다.)
	 * @return tournamentGames 중 roundNum과 동일한 roundNum을 가진 round 순으로 정렬된 tournamentGame List 반환
	 */
	private List<TournamentGame> findSameRoundGames(List<TournamentGame> tournamentGames, RoundNumber roundNum) {
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

	/**
	 * game의 패자를 찾는다.
	 * @param game
	 * @return
	 */
	private Team getLosingTeam(Game game) {
		return game.getTeams().stream()
			.filter(team -> Boolean.FALSE.equals(team.getWin()))
			.findAny()
			.orElseThrow(LosingTeamNotFoundException::new);
	}
}
