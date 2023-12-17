package com.gg.server.domain.tournament.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.exception.SlotNotFoundException;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.service.SeasonFindService;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.tournament.data.*;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.tournament.data.TournamentUser;
import com.gg.server.domain.tournament.data.TournamentUserRepository;
import com.gg.server.domain.tournament.dto.TournamentUserRegistrationResponseDto;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.tournament.dto.TournamentGameListResponseDto;
import com.gg.server.domain.tournament.dto.TournamentGameResDto;
import com.gg.server.domain.tournament.dto.TournamentListResponseDto;
import com.gg.server.domain.tournament.dto.TournamentResponseDto;
import com.gg.server.domain.tournament.exception.TournamentConflictException;
import com.gg.server.domain.tournament.exception.TournamentNotFoundException;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.domain.tournament.exception.TournamentNotFoundException;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.domain.tournament.type.TournamentUserStatus;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.dto.UserImageDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import static com.gg.server.domain.tournament.type.TournamentRound.*;
import static com.gg.server.domain.tournament.type.TournamentRound.QUARTER_FINAL_4;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final TournamentUserRepository tournamentUserRepository;
    private final UserRepository userRepository;
    private final TournamentGameRepository tournamentGameRepository;
    private final GameRepository gameRepository;
    private final SlotManagementRepository slotManagementRepository;
    private final SeasonFindService seasonFindService;

    /**
     * 토너먼트 리스트 조회
     * @param pageRequest 페이지 정보
     * @param type 토너먼트 타입
     * @param status 토너먼트 상태
     * @return 토너먼트 리스트
     */
    public TournamentListResponseDto getAllTournamentList(Pageable pageRequest, String type, String status) {

        Page<TournamentResponseDto> tournaments;

        TournamentType tournamentType = TournamentType.getEnumFromValue(type);
        TournamentStatus tournamentStatus = TournamentStatus.getEnumFromValue(status);

        if (type == null && status == null) {
            tournaments = tournamentRepository.findAll(pageRequest).
                    map(o-> new TournamentResponseDto(o, findTournamentWinner(o), findJoinedPlayerCnt(o)));
        } else if (type == null){
            tournaments = tournamentRepository.findAllByStatus(tournamentStatus, pageRequest).
                    map(o-> new TournamentResponseDto(o, findTournamentWinner(o), findJoinedPlayerCnt(o)));
        } else if (status == null) {
            tournaments = tournamentRepository.findAllByType(tournamentType, pageRequest).
                    map(o-> new TournamentResponseDto(o, findTournamentWinner(o), findJoinedPlayerCnt(o)));
        } else {
            tournaments = tournamentRepository.findAllByTypeAndStatus(tournamentType, tournamentStatus, pageRequest).
                    map(o-> new TournamentResponseDto(o, findTournamentWinner(o), findJoinedPlayerCnt(o)));
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
            .orElseThrow(() -> new TournamentNotFoundException(ErrorCode.TOURNAMENT_NOT_FOUND.getMessage(), ErrorCode.TOURNAMENT_NOT_FOUND));
        return (new TournamentResponseDto(tournament, findTournamentWinner(tournament), findJoinedPlayerCnt(tournament)));
    }

    /**
     * <p>유저 해당 토너먼트 참여 여부 확인 매서드</p>
     * @param tournamentId 타겟 토너먼트
     * @param user 해당 유저
     * @return TournamentUserRegistrationResponseDto [ BEFORE || WAIT || PLAYER ]
     * @throws TournamentNotFoundException 타겟 토너먼트 없음
     * @throws UserNotFoundException 유저 없음
     */
    public TournamentUserRegistrationResponseDto getUserStatusInTournament(Long tournamentId, UserDto user) {
        Tournament targetTournament = tournamentRepository.findById(tournamentId).orElseThrow(() ->
            new TournamentNotFoundException("target tournament not found", ErrorCode.TOURNAMENT_NOT_FOUND));

        TournamentUserStatus tournamentUserStatus = TournamentUserStatus.BEFORE;
        Optional<TournamentUser> tournamentUser = tournamentUserRepository.findByTournamentIdAndUserId(tournamentId, user.getId());
        if (tournamentUser.isPresent()) {
            tournamentUserStatus = tournamentUser.get().getIsJoined() ? TournamentUserStatus.PLAYER : TournamentUserStatus.WAIT;
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
        Tournament targetTournament = tournamentRepository.findById(tournamentId).orElseThrow(() ->
            new TournamentNotFoundException("target tournament not found", ErrorCode.TOURNAMENT_NOT_FOUND));
        User loginUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);

        List<TournamentUser> tournamentUserList = targetTournament.getTournamentUsers();
        tournamentUserRepository.findAllByUser(loginUser).stream()
            .filter(tu->tu.getTournament().getStatus().equals(TournamentStatus.BEFORE) || tu.getTournament().getStatus().equals(TournamentStatus.LIVE))
            .findAny()
            .ifPresent(a->{throw new TournamentConflictException("이미 신청한 토너먼트가 존재합니다.", ErrorCode.TOURNAMENT_CONFLICT);});
        TournamentUser tournamentUser = new TournamentUser(loginUser, targetTournament,
            tournamentUserList.size() < Tournament.ALLOWED_JOINED_NUMBER, LocalDateTime.now());
        TournamentUserStatus tournamentUserStatus = tournamentUser.getIsJoined() ? TournamentUserStatus.PLAYER : TournamentUserStatus.WAIT;
        return new TournamentUserRegistrationResponseDto(tournamentUserStatus);
    }

    /**
     * <p>유저 토너먼트 참가 신청 취소 매서드</p>
     * <p>참가자가 WAIT 이거나 PLAYER 로 해당 토너먼트에 신청을 한 상태일때만 취소해 준다.</p>
     * @param tournamentId 타겟 토너먼트
     * @param user 타겟 유저(사용자 본인)
     * @return
     */
    @Transactional
    public TournamentUserRegistrationResponseDto cancelTournamentUserRegistration(Long tournamentId, UserDto user) {
        Tournament targetTournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new TournamentNotFoundException("target tournament not found", ErrorCode.TOURNAMENT_NOT_FOUND));

        List<TournamentUser> tournamentUserList = targetTournament.getTournamentUsers();
        TournamentUser targetTournamentUser = tournamentUserList.stream()
            .filter(tu -> (tu.getUser().getId().equals(user.getId())))
            .findAny()
            .orElseThrow(()-> new TournamentNotFoundException("토너먼트 신청자가 아닙니다.", ErrorCode.TOURNAMENT_NOT_FOUND));
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
            if (time.isAfter(tournament.getStartTime()) &&
                time.isBefore(tournament.getEndTime())) {
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
                // TODO 취소 알림
                tournamentRepository.delete(imminentTournament);
                return;
            }
            imminentTournament.updateStatus(TournamentStatus.LIVE);
            matchTournamentGames(imminentTournament);
            // TODO 시작 알림?
        }
    }

    /**
     * 토너먼트 8강 경기 매칭 (game 생성)
     * @param tournament 게임 생성할 토너먼트
     */
    private void matchTournamentGames(Tournament tournament) {
        Season season = seasonFindService.findCurrentSeason(tournament.getStartTime());
        SlotManagement slotManagement = slotManagementRepository.findCurrent(tournament.getStartTime())
                .orElseThrow(SlotNotFoundException::new);
        int gameInterval = slotManagement.getGameInterval();
        // 8강 경기 매칭
        // QUARTER_FINAL_1, QUARTER_FINAL_2, QUARTER_FINAL_3, QUARTER_FINAL_4 순서대로 정렬
        List<TournamentGame> quarterFinalGames = tournament.getTournamentGames().stream()
            .filter(o -> o.getTournamentRound() == QUARTER_FINAL_1 ||
                o.getTournamentRound() == QUARTER_FINAL_2 ||
                o.getTournamentRound() == QUARTER_FINAL_3 ||
                o.getTournamentRound() == QUARTER_FINAL_4)
            .sorted(Comparator.comparing(TournamentGame::getTournamentRound))
            .collect(Collectors.toList());
        List<Game> games = new ArrayList<>();

        // game, team, teamUser 생성 후 저장
        for (int i = 0; i < Tournament.ALLOWED_JOINED_NUMBER / 2; ++i) {
            LocalDateTime startTime = tournament.getStartTime().plusMinutes((long) gameInterval * i);
            Game game = new Game(season, StatusType.BEFORE, Mode.TOURNAMENT, startTime, startTime.plusMinutes(gameInterval));
            Team team1 = new Team(game, -1, false);
            Team team2 = new Team(game, -1, false);
            TeamUser teamUser1 = new TeamUser(team1, tournament.getTournamentUsers().get(i * 2).getUser());
            TeamUser teamUser2 = new TeamUser(team2, tournament.getTournamentUsers().get(i * 2 + 1).getUser());
            team1.getTeamUsers().add(teamUser1);
            team2.getTeamUsers().add(teamUser2);
            game.getTeams().add(team1);
            game.getTeams().add(team2);
            gameRepository.save(game);

            games.add(game);
        }
        // TournamentGame Entity에 game 저장
        for (int i = 0; i < Tournament.ALLOWED_JOINED_NUMBER / 2; ++i) {
            quarterFinalGames.get(i).updateGame(games.get(i));
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
    private List<TournamentGameResDto> getTournamentGameResDtoList(Long tournamentId){
        List<TournamentGame> tournamentGames = tournamentGameRepository.findAllByTournamentId(tournamentId);
        List<TournamentGameResDto> tournamentGameResDtoList = new ArrayList<>();
        for (TournamentGame tournamentGame : tournamentGames) {
            TournamentGame nextTournamentGame = findNextTournamentGame(tournamentGames, tournamentGame);
            GameTeamUser gameTeamUser = null;
            if (tournamentGame.getGame() != null) {
                gameTeamUser = gameRepository.findTeamsByGameId(tournamentGame.getGame().getId());
            }
            tournamentGameResDtoList.add(new TournamentGameResDto(tournamentGame, gameTeamUser, tournamentGame.getTournamentRound(), nextTournamentGame));
        }
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