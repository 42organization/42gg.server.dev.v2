package com.gg.server.domain.tournament.service;

import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.tournament.data.TournamentUser;
import com.gg.server.domain.tournament.dto.TournamentListResponseDto;
import com.gg.server.domain.tournament.dto.TournamentResponseDto;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.dto.UserImageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;

    /**
     * 토너먼트 리스트 조회
     * @param pageRequest 페이지 정보
     * @param type 토너먼트 타입
     * @param status 토너먼트 상태
     * @return 토너먼트 리스트
     */
    public TournamentListResponseDto getAllTournamentList(PageRequest pageRequest, String type, String status) {

        Page<TournamentResponseDto> tournaments;

        TournamentType tournamentType = TournamentType.getEnumFromValue(type);
        TournamentStatus tournamentStatus = TournamentStatus.getEnumFromValue(status);

        if (type == null && status == null) {
            tournaments = tournamentRepository.findAll(pageRequest).
                    map(o-> new TournamentResponseDto(o, findTournamentWinner(o), findPlayerCnt(o)));
        } else if (type == null){
            tournaments = tournamentRepository.findAllByStatus(tournamentStatus, pageRequest).
                    map(o-> new TournamentResponseDto(o, findTournamentWinner(o), findPlayerCnt(o)));
        } else if (status == null) {
            tournaments = tournamentRepository.findAllByType(tournamentType, pageRequest).
                    map(o-> new TournamentResponseDto(o, findTournamentWinner(o), findPlayerCnt(o)));
        } else {
            tournaments = tournamentRepository.findAllByTypeAndStatus(tournamentType, tournamentStatus, pageRequest).
                    map(o-> new TournamentResponseDto(o, findTournamentWinner(o), findPlayerCnt(o)));
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
    private int findPlayerCnt(Tournament tournament) {
        int player_cnt = 0;
        for (TournamentUser tournamentUser : tournament.getTournamentUsers()) {
            if (tournamentUser.isJoined()){
                player_cnt++;
            }
        }
        return player_cnt;
    }
}