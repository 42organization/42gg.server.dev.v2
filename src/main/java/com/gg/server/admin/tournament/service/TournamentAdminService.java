package com.gg.server.admin.tournament.service;

import com.gg.server.admin.tournament.dto.TournamentAdminUpdateRequestDto;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentGameRepository;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.tournament.exception.TournamentConflictException;
import com.gg.server.domain.tournament.exception.TournamentNotFoundException;
import com.gg.server.domain.tournament.exception.TournamentUpdateException;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TournamentAdminService {
    private final TournamentRepository tournamentRepository;
    private final TournamentGameRepository tournamentGameRepository;
    // 토너먼트 최소 시작 날짜 (n일 후)
    private static final long ALLOWED_MINIMAL_START_DAYS = 2;
    // 토너먼트 최소 진행 시간 (n시간)
    private static final long MINIMUM_TOURNAMENT_DURATION = 2;

    /**
     * 토너먼트 업데이트 Method
     * @param tournamentId  업데이트할 토너먼트 id
     * @param requestDto 요청한 Dto
     * @throws TournamentNotFoundException 찾을 수 없는 토너먼트 일 때
     * @throws TournamentUpdateException 업데이트 할 수 없는 토너먼트 일 때
     */
    public Tournament updateTournamentInfo(Long tournamentId, TournamentAdminUpdateRequestDto requestDto) {
        Tournament targetTournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new TournamentNotFoundException("target tournament not found", ErrorCode.TOURNAMENT_NOT_FOUND));
        if (targetTournament.getStatus() != TournamentStatus.BEFORE) {
            throw new TournamentUpdateException("already started or ended", ErrorCode.TOURNAMENT_NOT_BEFORE);
        }
        checkValidTournamentTime(requestDto.getStartTime(), requestDto.getEndTime());
        checkConflictedTournament(targetTournament.getId(), requestDto.getStartTime(), requestDto.getEndTime());
        targetTournament.update(
            requestDto.getTitle(),
            requestDto.getContents(),
            requestDto.getStartTime(),
            requestDto.getEndTime(),
            requestDto.getType(),
            TournamentStatus.BEFORE);
        return tournamentRepository.save(targetTournament);
    }

    /**
     * 토너먼트 삭제 매서드:
     * 토너먼트는 BEFORE 인 경우에만 삭제 가능하다.
     * @param tournamentId 타겟 토너먼트 id
     * @throws TournamentNotFoundException 찾을 수 없는 토너먼트 일 때
     * @throws TournamentUpdateException 업데이트 할 수 없는 토너먼트 일 때
     */
    public void deleteTournamentInfo(Long tournamentId) {
        Tournament targetTournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new TournamentNotFoundException("target tournament not found", ErrorCode.TOURNAMENT_NOT_FOUND));
        if (targetTournament.getStatus() != TournamentStatus.BEFORE) {
            throw new TournamentUpdateException("already started or ended", ErrorCode.TOURNAMENT_NOT_BEFORE);
        }
        List<TournamentGame> tournamentGameList = tournamentGameRepository.findAllByTournamentId(targetTournament.getId());
        tournamentGameRepository.deleteAll(tournamentGameList);
        tournamentRepository.deleteById(tournamentId);
    }

    /**
     * 토너먼트 시간 체크 :
     * [ 현재 시간 + 최소 2일 ],
     * [ 현재시간 보다 미래 ],
     * [ 시작 시간이 종료시간보다 현재시에 가까움 ]
     * [ 진행 시간 최소 2시간 ]
     * @param startTime 업데이트할 토너먼트 시작 시간
     * @param endTime 업데이트할 토너먼트 종료 시간
     * @throws InvalidParameterException 토너먼트 시간으로 부적합 할 때
     */
    private void checkValidTournamentTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime) ||
            startTime.isBefore(LocalDateTime.now().plusDays(ALLOWED_MINIMAL_START_DAYS)) ||
            startTime.plusHours(MINIMUM_TOURNAMENT_DURATION).isAfter(endTime)) {
            throw new InvalidParameterException("invalid tournament time", ErrorCode.VALID_FAILED);
        }
    }

    /**
     * tournamentList 에서 targetTournament을 제외한 토너먼트 중 겹치는 시간대 존재 유무 확인
     * @param targetTournamentId 업데이트할 토너먼트 id
     * @param startTime 업데이트할 토너먼트 시작 시간
     * @param endTime 업데이트할 토너먼트 종료 시간
     * @throws TournamentConflictException 업데이트 하고자 하는 토너먼트의 시간이 겹칠 때
     */
    private void checkConflictedTournament(Long targetTournamentId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Tournament> tournamentList = tournamentRepository.findAllByStatusIsNot(TournamentStatus.END);
        for (Tournament tournament : tournamentList) {
            if (targetTournamentId.equals(tournament.getId())) {
                continue;
            }
            if ((startTime.isAfter(tournament.getStartTime()) && startTime.isBefore(tournament.getEndTime())) ||
                (endTime.isAfter(tournament.getStartTime()) && endTime.isBefore(tournament.getEndTime())) ||
                (startTime.isBefore(tournament.getStartTime()) && endTime.isAfter(tournament.getEndTime())) ||
                startTime.isEqual(tournament.getStartTime()) || startTime.isEqual(tournament.getEndTime()) ||
                endTime.isEqual(tournament.getEndTime()) || endTime.isEqual(tournament.getStartTime())) {
                throw new TournamentConflictException("tournament conflicted", ErrorCode.TOURNAMENT_CONFLICT);
            }
        }
    }
}
