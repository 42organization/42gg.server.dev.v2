package gg.pingpong.api.user.tournament.service;

import static java.util.Comparator.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.api.admin.noti.controller.request.SendNotiAdminRequestDto;
import gg.pingpong.api.admin.noti.service.NotiAdminService;
import gg.pingpong.api.user.match.service.MatchTournamentService;
import gg.pingpong.api.user.tournament.dto.TournamentGameListResponseDto;
import gg.pingpong.api.user.tournament.dto.TournamentGameResDto;
import gg.pingpong.api.user.tournament.dto.TournamentListResponseDto;
import gg.pingpong.api.user.tournament.dto.TournamentResponseDto;
import gg.pingpong.api.user.tournament.dto.TournamentUserRegistrationResponseDto;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.pingpong.data.game.Tournament;
import gg.pingpong.data.game.TournamentGame;
import gg.pingpong.data.game.TournamentUser;
import gg.pingpong.data.game.type.RoundNumber;
import gg.pingpong.data.game.type.TournamentRound;
import gg.pingpong.data.game.type.TournamentStatus;
import gg.pingpong.data.game.type.TournamentType;
import gg.pingpong.data.game.type.TournamentUserStatus;
import gg.pingpong.data.noti.type.NotiType;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.game.GameRepository;
import gg.pingpong.repo.game.GameTeamUser;
import gg.pingpong.repo.tournarment.TournamentGameRepository;
import gg.pingpong.repo.tournarment.TournamentRepository;
import gg.pingpong.repo.tournarment.TournamentUserRepository;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.BusinessException;
import gg.pingpong.utils.exception.tournament.TournamentConflictException;
import gg.pingpong.utils.exception.tournament.TournamentNotFoundException;
import gg.pingpong.utils.exception.tournament.TournamentUpdateException;
import gg.pingpong.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TournamentService {
	private final TournamentRepository tournamentRepository;
	private final TournamentUserRepository tournamentUserRepository;
	private final UserRepository userRepository;
	private final TournamentGameRepository tournamentGameRepository;
	private final GameRepository gameRepository;
	private final MatchTournamentService matchTournamentService;
	private final NotiAdminService notiAdminService;

	/**
	 * 토너먼트 리스트 조회
	 * @param pageRequest 페이지 정보
	 * @param type 토너먼트 타입
	 * @param status 토너먼트 상태
	 * @return 토너먼트 리스트
	 */
	public TournamentListResponseDto getAllTournamentList(Pageable pageRequest, TournamentType type,
		TournamentStatus status) {
		Page<Tournament> tournaments;
		if (type == null && status == null) {
			tournaments = tournamentRepository.findAll(pageRequest);
		} else if (type == null) {
			tournaments = tournamentRepository.findAllByStatus(status, pageRequest);
		} else if (status == null) {
			tournaments = tournamentRepository.findAllByType(type, pageRequest);
		} else {
			tournaments = tournamentRepository.findAllByTypeAndStatus(type, status, pageRequest);
		}
		Page<TournamentResponseDto> tournamentsDto = tournaments.map(TournamentResponseDto::new);
		return new TournamentListResponseDto(tournamentsDto);
	}

	/**
	 * 토너먼트 단일 조회
	 * @param tournamentId
	 * @return 토너먼트
	 */
	public TournamentResponseDto getTournament(long tournamentId) {
		Tournament tournament = tournamentRepository.findById(tournamentId)
			.orElseThrow(TournamentNotFoundException::new);
		return (new TournamentResponseDto(tournament));
	}

	/**
	 * <p>유저 해당 토너먼트 참여 여부 확인 매서드</p>
	 * @param tournamentId 타겟 토너먼트
	 * @param user 해당 유저
	 * @return TournamentUserRegistrationResponseDto [ BEFORE || WAIT || PLAYER ]
	 * @throws TournamentNotFoundException 타겟 토너먼트 없음
	 */
	public TournamentUserRegistrationResponseDto getUserStatusInTournament(Long tournamentId, UserDto user) {
		Tournament targetTournament = tournamentRepository.findById(tournamentId)
			.orElseThrow(TournamentNotFoundException::new);

		TournamentUserStatus tournamentUserStatus = TournamentUserStatus.BEFORE;
		Optional<TournamentUser> tournamentUser = targetTournament.findTournamentUserByUserId(user.getId());

		if (tournamentUser.isPresent()) {
			tournamentUserStatus =
				tournamentUser.get().getIsJoined() ? TournamentUserStatus.PLAYER : TournamentUserStatus.WAIT;
		}
		return new TournamentUserRegistrationResponseDto(tournamentUserStatus);
	}

	/**
	 * <p>토너먼트 참가 신청 매서드</p>
	 * <p>이미 신청한 토너먼트 중  BEFORE || LIVE인 경우가 존재한다면 신청 불가능 하다.</p>
	 * @param tournamentId 타겟 토너먼트 Id
	 * @param user 신청 유저(로그인한 본인)
	 * @return TournamentUserRegistrationResponseDto [ WAIT || PLAYER ]
	 * @throws TournamentNotFoundException 타겟 토너먼트 없음
	 * @throws UserNotFoundException 유저 없음
	 * @throws TournamentConflictException 이미 신청한 토너먼트 존재(BEFORE || LIVE인 토너먼트)
	 */
	@Transactional
	public TournamentUserRegistrationResponseDto registerTournamentUser(Long tournamentId, UserDto user) {
		Tournament targetTournament = tournamentRepository.findById(tournamentId)
			.orElseThrow(TournamentNotFoundException::new);
		if (!targetTournament.getStatus().equals(TournamentStatus.BEFORE)) {
			throw new TournamentUpdateException();
		}
		User loginUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);

		List<TournamentUser> tournamentUserList = targetTournament.getTournamentUsers();
		tournamentUserRepository.findAllByUser(loginUser).stream()
			.filter(tu -> tu.getTournament().getStatus().equals(TournamentStatus.BEFORE) || tu.getTournament()
				.getStatus()
				.equals(TournamentStatus.LIVE))
			.findAny()
			.ifPresent(a -> {
				throw new TournamentConflictException(ErrorCode.TOURNAMENT_ALREADY_PARTICIPANT);
			});
		TournamentUser tournamentUser = new TournamentUser(loginUser, targetTournament,
			tournamentUserList.size() < Tournament.ALLOWED_JOINED_NUMBER, LocalDateTime.now());
		TournamentUserStatus tournamentUserStatus =
			tournamentUser.getIsJoined() ? TournamentUserStatus.PLAYER : TournamentUserStatus.WAIT;
		return new TournamentUserRegistrationResponseDto(tournamentUserStatus);
	}

	/**
	 * <p>유저 토너먼트 참가 신청 취소 매서드</p>
	 * <p>참가자가 WAIT 이거나 PLAYER 로 해당 토너먼트에 신청을 한 상태일때만 취소해 준다.</p>
	 * @param tournamentId 타겟 토너먼트
	 * @param user 타겟 유저(사용자 본인)
	 * @throws TournamentNotFoundException 타겟 토너먼트 없음 || 타겟 토너먼트 신청자가 아님
	 * @return
	 */
	@Transactional
	public TournamentUserRegistrationResponseDto cancelTournamentUserRegistration(Long tournamentId, UserDto user) {
		Tournament targetTournament = tournamentRepository.findById(tournamentId)
			.orElseThrow(TournamentNotFoundException::new);
		if (!targetTournament.getStatus().equals(TournamentStatus.BEFORE)) {
			throw new TournamentUpdateException();
		}

		List<TournamentUser> tournamentUserList = targetTournament.getTournamentUsers();
		TournamentUser targetTournamentUser = tournamentUserList.stream()
			.filter(tu -> (tu.getUser().getId().equals(user.getId())))
			.findAny()
			.orElseThrow(() -> new TournamentNotFoundException(ErrorCode.TOURNAMENT_NOT_PARTICIPANT));
		tournamentUserList.remove(targetTournamentUser);
		if (targetTournamentUser.getIsJoined() && tournamentUserList.size() >= Tournament.ALLOWED_JOINED_NUMBER) {
			tournamentUserList.get(Tournament.ALLOWED_JOINED_NUMBER - 1).updateIsJoined(true);
		}
		tournamentUserRepository.delete(targetTournamentUser);
		return new TournamentUserRegistrationResponseDto(TournamentUserStatus.BEFORE);
	}

	/**
	 * 오늘 시작하는 토너먼트가 있으면 해당 토너먼트 status를 LIVE로 변경하고 8강 경기 매칭
	 * 참가자가 ALLOWED_JOINED_NUMBER보다 적으면 토너먼트 취소
	 */
	@Transactional
	public void startTournament() {
		LocalDate date = LocalDate.now();
		List<Tournament> imminentTournaments = findImminentTournament(date);

		for (Tournament imminentTournament : imminentTournaments) {
			List<TournamentUser> tournamentUsers = imminentTournament.getTournamentUsers();
			if (tournamentUsers.size() < Tournament.ALLOWED_JOINED_NUMBER) {
				sendNotification(tournamentUsers, NotiType.TOURNAMENT_CANCELED);
				tournamentRepository.delete(imminentTournament);
				continue;
			}
			imminentTournament.updateStatus(TournamentStatus.LIVE);
			matchTournamentService.matchGames(imminentTournament, RoundNumber.QUARTER_FINAL);
		}
	}

	/**
	 * 토너먼트 참가중인 유저에게 알림 전송.
	 */
	private void sendNotification(List<TournamentUser> tournamentUsers, NotiType type) {
		for (TournamentUser tournamentUser : tournamentUsers) {
			String message = type.getMessage();

			if (tournamentUser.getIsJoined().equals(true)) {
				String intraId = tournamentUser.getUser().getIntraId();
				SendNotiAdminRequestDto dto = new SendNotiAdminRequestDto(intraId, message);
				notiAdminService.sendAnnounceNotiToUser(dto);
			}
		}
	}

	/**
	 * 시작 임박한(오늘 시작하는) 토너먼트 조회
	 * @param date 조회하려는 토너먼트의 시작 날짜
	 * @return date 날짜에 시작하는 토너먼트
	 */
	private List<Tournament> findImminentTournament(LocalDate date) {
		List<Tournament> tournaments = tournamentRepository.findAllByStatus(TournamentStatus.BEFORE);
		return tournaments.stream()
			.filter(tournament -> tournament.getStartTime().toLocalDate().isEqual(date))
			.collect(Collectors.toList());
	}

	/**
	 * 토너먼트 게임 목록 조회
	 * @param tournamentId 토너먼트 id
	 * @return 토너먼트 게임 목록
	 */
	public TournamentGameListResponseDto getTournamentGames(Long tournamentId) {
		List<TournamentGameResDto> tournamentGameResDtoList = getTournamentGameResDtoList(tournamentId);
		return new TournamentGameListResponseDto(tournamentId, tournamentGameResDtoList);
	}

	/**
	 * TournamentGameResDto list 반환
	 * @param tournamentId 토너먼트 id
	 * @return List<TournamentGameResDto>
	 *     - tournamentGameId: 토너먼트 게임 id
	 *     - NextTournamentGameId: 다음 토너먼트 게임 id
	 *     - tournamentRound: 토너먼트 라운드
	 *     - game: 게임 정보
	 */
	private List<TournamentGameResDto> getTournamentGameResDtoList(Long tournamentId) {
		List<TournamentGame> tournamentGames = tournamentGameRepository.findAllByTournamentId(tournamentId);
		List<TournamentGameResDto> tournamentGameResDtoList = new ArrayList<>();
		for (TournamentGame tournamentGame : tournamentGames) {
			TournamentGame nextTournamentGame = findNextTournamentGame(tournamentGames, tournamentGame);
			GameTeamUser gameTeamUser = null;
			if (tournamentGame.getGame() != null) {
				gameTeamUser = gameRepository.findTeamsByGameId(tournamentGame.getGame().getId())
					.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_USER_NOT_FOUND));
			}
			tournamentGameResDtoList.add(
				new TournamentGameResDto(tournamentGame, gameTeamUser, tournamentGame.getTournamentRound(),
					nextTournamentGame));
		}
		tournamentGameResDtoList.sort(comparing(TournamentGameResDto::getTournamentRound,
			comparing(TournamentRound::getRoundNumber).reversed().thenComparing(TournamentRound::getRoundOrder)));

		return tournamentGameResDtoList;
	}

	/**
	 * 다음 토너먼트 게임 조회
	 * @param tournamentGames tournamentGames 토너먼트 게임 리스트
	 * @param tournamentGame 현재 토너먼트 게임
	 * @return 다음 토너먼트 게임
	 */
	private TournamentGame findNextTournamentGame(List<TournamentGame> tournamentGames, TournamentGame tournamentGame) {
		TournamentRound tournamentRound = tournamentGame.getTournamentRound();
		return tournamentGames.stream()
			.filter(tournamentGame1 -> tournamentGame1.getTournamentRound().equals(tournamentRound.getNextRound()))
			.findFirst()
			.orElse(null);
	}
}
