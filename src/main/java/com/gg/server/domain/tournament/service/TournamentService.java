package com.gg.server.domain.tournament.service;

import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.tournament.data.TournamentUser;
import com.gg.server.domain.tournament.data.TournamentUserRepository;
import com.gg.server.domain.tournament.dto.TournamentListResponseDto;
import com.gg.server.domain.tournament.dto.TournamentResponseDto;
import com.gg.server.domain.tournament.exception.TournamentNotFoundException;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.dto.UserImageDto;
import com.gg.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final TournamentUserRepository tournamentUserRepository;

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
     * 토너먼트 단일 조회
     * @param tournamentId
     * @return 토너먼트
     */
    public TournamentResponseDto getTournament(long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException("Tournament no found with ID: " + tournamentId, ErrorCode.TOURNAMENT_NOT_FOUND));
        return (new TournamentResponseDto(tournament, findTournamentWinner(tournament), findJoinedPlayerCnt(tournament)));
    }
}