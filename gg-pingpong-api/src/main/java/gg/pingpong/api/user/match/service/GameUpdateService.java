package gg.pingpong.api.user.match.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import gg.data.noti.Noti;
import gg.data.pingpong.game.Game;
import gg.data.pingpong.game.Team;
import gg.data.pingpong.game.TeamUser;
import gg.data.pingpong.manage.SlotManagement;
import gg.data.user.User;
import gg.pingpong.api.user.match.dto.GameAddDto;
import gg.pingpong.api.user.noti.service.NotiService;
import gg.pingpong.api.user.noti.service.SnsNotiService;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.repo.game.GameRepository;
import gg.repo.game.TeamRepository;
import gg.repo.game.TeamUserRepository;
import gg.repo.manage.SlotManagementRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.match.SlotNotFoundException;
import gg.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameUpdateService {
	private final GameRepository gameRepository;
	private final TeamRepository teamRepository;
	private final UserRepository userRepository;
	private final TeamUserRepository teamUserRepository;
	private final SlotManagementRepository slotManagementRepository;
	private final NotiService notiService;
	private final SnsNotiService snsNotiService;

	/**
	 * 게임 생성 메서드
	 * 1) 게임 취소했을 경우, 2) 게임 매칭됐을 경우, 3) 토너먼트 게임 생성
	 * @param addDto 게임 생성에 필요한 정보
	 * @param recoveredUserId 게임 취소 당한 유저의 id, -1이면 무의미함
	 */
	public void make(GameAddDto addDto, Long recoveredUserId) {
		SlotManagement slotManagement = slotManagementRepository.findCurrent(LocalDateTime.now())
			.orElseThrow(SlotNotFoundException::new);
		Game game = new Game(addDto.getSeason(), addDto.getMode(), addDto.getStartTime(),
			slotManagement.getGameInterval());
		gameRepository.save(game);
		Team enemyTeam = new Team(game, -1, false);
		Team myTeam = new Team(game, -1, false);
		List<Team> matchPair = List.of(enemyTeam, myTeam);
		teamRepository.saveAll(matchPair);
		User playerUser = userRepository.findById(addDto.getPlayerId()).orElseThrow(UserNotFoundException::new);
		User enemyUser = userRepository.findById(addDto.getEnemyId()).orElseThrow(UserNotFoundException::new);
		TeamUser myTeamUser = new TeamUser(myTeam, playerUser);
		TeamUser enemyTeamUser = new TeamUser(enemyTeam, enemyUser);
		List<TeamUser> matchTeamUser = List.of(enemyTeamUser, myTeamUser);
		teamUserRepository.saveAll(matchTeamUser);
		if (!playerUser.getId().equals(recoveredUserId)) {
			Noti playerNoti = notiService.createMatched(playerUser, addDto.getStartTime());
			snsNotiService.sendSnsNotification(playerNoti, UserDto.from(playerUser));
		}
		if (!enemyUser.getId().equals(recoveredUserId)) {
			Noti enemyNoti = notiService.createMatched(enemyUser, addDto.getStartTime());
			snsNotiService.sendSnsNotification(enemyNoti, UserDto.from(enemyUser));
		}
	}

	public void delete(Game game, List<User> enemyTeam) {
		enemyTeam.forEach(enemy -> {
			Noti noti = notiService.createMatchCancel(enemy, game.getStartTime());
			snsNotiService.sendSnsNotification(noti, UserDto.from(enemy));
		});
		gameRepository.delete(game);
	}

	public void delete(Game game) {
		gameRepository.delete(game);
	}
}
