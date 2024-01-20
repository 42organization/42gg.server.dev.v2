package com.gg.server.domain.tournament.service;

import static com.gg.server.domain.tournament.type.TournamentRound.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.admin.noti.dto.SendNotiAdminRequestDto;
import com.gg.server.admin.noti.service.NotiAdminService;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.match.service.MatchTournamentService;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentGameRepository;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.tournament.data.TournamentUser;
import com.gg.server.domain.tournament.data.TournamentUserRepository;
import com.gg.server.domain.tournament.dto.TournamentGameListResponseDto;
import com.gg.server.domain.tournament.dto.TournamentGameResDto;
import com.gg.server.domain.tournament.dto.TournamentListResponseDto;
import com.gg.server.domain.tournament.dto.TournamentResponseDto;
import com.gg.server.domain.tournament.dto.TournamentUserRegistrationResponseDto;
import com.gg.server.domain.tournament.exception.TournamentConflictException;
import com.gg.server.domain.tournament.exception.TournamentNotFoundException;
import com.gg.server.domain.tournament.exception.TournamentUpdateException;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.domain.tournament.type.TournamentUserStatus;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.dto.UserImageDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.BusinessException;

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

		Page<TournamentResponseDto> tournaments;
		if (type == null && status == null) {
			tournaments = tournamentRepository.findAll(pageRequest)
				.map(o -> new TournamentResponseDto(o, findTournamentWinner(o), findJoinedPlayerCnt(o)));
		} else if (type == null) {
			tournaments = tournamentRepository.findAllByStatus(status, pageRequest)
				.map(o -> new TournamentResponseDto(o, findTournamentWinner(o), findJoinedPlayerCnt(o)));
		} else if (status == null) {
			tournaments = tournamentRepository.findAllByType(type, pageRequest)
				.map(o -> new TournamentResponseDto(o, findTournamentWinner(o), findJoinedPlayerCnt(o)));
		} else {
			tournaments = tournamentRepository.findAllByTypeAndStatus(type, status, pageRequest)
				.map(o -> new TournamentResponseDto(o, findTournamentWinner(o), findJoinedPlayerCnt(o)));
		}
		return new TournamentListResponseDto(tournaments.getContent(), tournaments.getTotalPages());
	}

	/**
	 * 토너먼트 단일 조회
	 * @param tournamentId
	 * @return 토너먼트
	 */
	public TournamentResponseDto getTournament(long tournamentId) {
		Tournament tournament = tournamentRepository.findById(tournamentId)
			.orElseThrow(TournamentNotFoundException::new);
		return (new TournamentResponseDto(tournament, findTournamentWinner(tournament),
			findJoinedPlayerCnt(tournament)));
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
		Optional<TournamentUser> tournamentUser = tournamentUserRepository.findByTournamentIdAndUserId(tournamentId,
			user.getId());
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
	 * 진행중인 토너먼트 유무 확인
	 * @param time 현재 시간
	 * @return 종료되지 않은 토너먼트 있으면 true, 없으면 false
	 */
	public boolean isNotEndedTournament(LocalDateTime time) {
		List<Tournament> tournamentList = tournamentRepository.findAllByStatusIsNot(TournamentStatus.END);
		for (Tournament tournament : tournamentList) {
			if (time.isAfter(tournament.getStartTime())
				&& time.isBefore(tournament.getEndTime())) {
				return false;
			}
		}
		return true;
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
				for (TournamentUser tournamentUser : tournamentUsers) {
					if (tournamentUser.getIsJoined().equals(true)) {
						notiAdminService.sendAnnounceNotiToUser(
							new SendNotiAdminRequestDto(tournamentUser.getUser().getIntraId(),
								NotiType.TOURNAMENT_CANCELED.getMessage()));
					}
				}
				tournamentRepository.delete(imminentTournament);
				return;
			}
			imminentTournament.updateStatus(TournamentStatus.LIVE);
			matchTournamentService.matchGames(imminentTournament, QUARTER_FINAL_1);
		}
	}

	/**
	 * 시작 임박한(오늘 시작하는) 토너먼트 조회
	 * @param date 조회하려는 토너먼트의 시작 날짜
	 * @return date 날짜에 시작하는 토너먼트
	 */
	private List<Tournament> findImminentTournament(LocalDate date) {
		List<Tournament> tournaments = tournamentRepository.findAllByStatus(TournamentStatus.BEFORE);
		List<Tournament> imminentTournaments = new ArrayList<>();

		for (Tournament tournament : tournaments) {
			LocalDate startDate = tournament.getStartTime().toLocalDate();
			if (startDate.isEqual(date)) {
				imminentTournaments.add(tournament);
			}
		}
		return imminentTournaments;
	}

	/**
	 * 토너먼트 우승자 조회
	 * @param tournament 토너먼트
	 * @return 토너먼트 우승자 정보
	 */
	private UserImageDto findTournamentWinner(Tournament tournament) {
		User winner = tournament.getWinner();
		return new UserImageDto(winner);
	}

	/**
	 * 토너먼트 참가자 수 조회
	 * @param tournament 토너먼트
	 * @return 토너먼트 참가자 수
	 */
	private int findJoinedPlayerCnt(Tournament tournament) {
		return tournamentUserRepository.countByTournamentAndIsJoined(tournament, true);
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
		tournamentGameResDtoList.sort((o1, o2) -> {
			if (o1.getTournamentRound().getRoundNumber() < o2.getTournamentRound().getRoundNumber()) {
				return 1;
			}
			if (o1.getTournamentRound().getRoundOrder() > o2.getTournamentRound().getRoundOrder()) {
				return 1;
			}
			return -1;
		});
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
