package com.gg.server.domain.tournament.service;

import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.tournament.data.TournamentUser;
import com.gg.server.domain.tournament.data.TournamentUserRepository;
import com.gg.server.domain.tournament.dto.TournamentUserRegistrationResponseDto;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.tournament.data.*;
import com.gg.server.domain.tournament.dto.TournamentGameListResponseDto;
import com.gg.server.domain.tournament.dto.TournamentGameResDto;
import com.gg.server.domain.tournament.dto.TournamentListResponseDto;
import com.gg.server.domain.tournament.dto.TournamentResponseDto;
import com.gg.server.domain.tournament.exception.TournamentNotFoundException;
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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final TournamentUserRepository tournamentUserRepository;
    private final UserRepository userRepository;
    private final TournamentGameRepository tournamentGameRepository;
    private final GameRepository gameRepository;

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
     * @return
     */
    public TournamentUserRegistrationResponseDto getUserStatusInTournament(Long tournamentId, UserDto user) {
        Tournament targetTournament = tournamentRepository.findById(tournamentId).orElseThrow(() ->
            new TournamentNotFoundException("target tournament not found", ErrorCode.TOURNAMENT_NOT_FOUND));
        User loginUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
        TournamentUserStatus tournamentUserStatus = TournamentUserStatus.BEFORE;
        Optional<TournamentUser> tournamentUser = tournamentUserRepository.findByTournamentIdAndUserId(tournamentId, user.getId());
        if (tournamentUser.isPresent()) {
            tournamentUserStatus = tournamentUser.get().getIsJoined() ? TournamentUserStatus.PLAYER : TournamentUserStatus.WAIT;
        }
        return new TournamentUserRegistrationResponseDto(tournamentUserStatus);
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