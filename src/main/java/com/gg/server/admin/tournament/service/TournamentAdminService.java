package com.gg.server.admin.tournament.service;

import com.gg.server.admin.tournament.dto.TournamentAdminAddUserRequestDto;
import com.gg.server.admin.tournament.dto.TournamentAdminAddUserResponseDto;
import com.gg.server.admin.tournament.dto.TournamentAdminUpdateRequestDto;
import com.gg.server.admin.tournament.dto.TournamentAdminCreateRequestDto;
import com.gg.server.admin.tournament.exception.TournamentTitleConflictException;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentGameRepository;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.tournament.data.TournamentUser;
import com.gg.server.domain.tournament.data.TournamentUserRepository;
import com.gg.server.domain.tournament.exception.TournamentConflictException;
import com.gg.server.domain.tournament.exception.TournamentNotFoundException;
import com.gg.server.domain.tournament.exception.TournamentUpdateException;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TournamentAdminService {
    private final TournamentRepository tournamentRepository;
    private final TournamentGameRepository tournamentGameRepository;
    private final TournamentUserRepository tournamentUserRepository;
    private final UserRepository userRepository;

    // 토너먼트 참가자 수 => 현재는 8강 고정
    private static final long ALLOWED_JOINED_NUMBER = 8;
    // 토너먼트 최소 시작 날짜 (n일 후)
    private static final long ALLOWED_MINIMAL_START_DAYS = 2;
    // 토너먼트 최소 진행 시간 (n시간)
    private static final long MINIMUM_TOURNAMENT_DURATION = 2;

    /***
     * 토너먼트 생성 Method
     * @param tournamentAdminCreateRequestDto 토너먼트 생성에 필요한 데이터
     * @return 새로 생성된 tournament
     */
    @Transactional
    public Tournament createTournament(TournamentAdminCreateRequestDto tournamentAdminCreateRequestDto) {
        checkTournamentTitle(tournamentAdminCreateRequestDto.getTitle());
        checkValidTournamentTime(tournamentAdminCreateRequestDto.getStartTime(), tournamentAdminCreateRequestDto.getEndTime());
        checkConflictedTournament(-1L, tournamentAdminCreateRequestDto.getStartTime(), tournamentAdminCreateRequestDto.getEndTime());

        Tournament tournament = Tournament.builder()
                .title(tournamentAdminCreateRequestDto.getTitle())
                .contents(tournamentAdminCreateRequestDto.getContents())
                .startTime(tournamentAdminCreateRequestDto.getStartTime())
                .endTime(tournamentAdminCreateRequestDto.getEndTime())
                .type(tournamentAdminCreateRequestDto.getType())
                .status(TournamentStatus.BEFORE).build();
        createTournamentGameList(tournament, 7);
        return tournamentRepository.save(tournament);
    }

    /**
     * <p>토너먼트 업데이트 Method</p>
     * @param tournamentId 업데이트할 토너먼트 id
     * @param requestDto   요청한 Dto
     * @throws TournamentNotFoundException 찾을 수 없는 토너먼트 일 때
     * @throws TournamentUpdateException   업데이트 할 수 없는 토너먼트 일 때
     */
    public Tournament updateTournamentInfo(Long tournamentId, TournamentAdminUpdateRequestDto requestDto) {
        Tournament targetTournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException("target tournament not found", ErrorCode.TOURNAMENT_NOT_FOUND));
        if (targetTournament.getStatus() != TournamentStatus.BEFORE && !targetTournament.getStatus().equals(TournamentStatus.READY)) {
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

    /***
     * 토너먼트 게임 테이블 생성 Method
     * @param tournament 토너먼트 게임에 매칭될 토너먼트
     * @param cnt 토너먼트 전체 라운드 수
     */
    private void createTournamentGameList(Tournament tournament, int cnt) {
        TournamentRound[] rounds = TournamentRound.values();
        while (--cnt >= 0) {
            TournamentGame tournamentGame = new TournamentGame(null, tournament, rounds[cnt]);
            tournament.addTournamentGame(tournamentGame);
        }
    }

    /**
     * <p>토너먼트 삭제 매서드</p>
     * <p>토너먼트는 BEFORE 인 경우에만 삭제 가능하다.</p>
     * @param tournamentId 타겟 토너먼트 id
     * @throws TournamentNotFoundException 찾을 수 없는 토너먼트 일 때
     * @throws TournamentUpdateException   업데이트 할 수 없는 토너먼트 일 때
     */
    public void deleteTournament(Long tournamentId) {
        Tournament targetTournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException("target tournament not found", ErrorCode.TOURNAMENT_NOT_FOUND));
        if (targetTournament.getStatus() != TournamentStatus.BEFORE && !targetTournament.getStatus().equals(TournamentStatus.READY)) {
            throw new TournamentUpdateException("already started or ended", ErrorCode.TOURNAMENT_NOT_BEFORE);
        }
        List<TournamentGame> tournamentGameList = tournamentGameRepository.findAllByTournamentId(targetTournament.getId());
        tournamentGameRepository.deleteAll(tournamentGameList);
        tournamentRepository.deleteById(tournamentId);
    }

    /**
     * <p>관리자 토너먼트 참가 유저 추가 매서드</p>
     * <p>해당 토너먼트에 이미 신청 되어 있으면 추가 불가</p>
     * @param tournamentId 타겟 토너먼트
     * @param requestDto   요청 dto
     * @return TournamentAdminAddUserResponseDto dto 반환
     * @throws TournamentNotFoundException 타겟 토너먼트 없음
     * @throws TournamentUpdateException   이미 시작했거나 종료된 토너먼트
     * @throws UserNotFoundException       유저 없음
     * @throws TournamentConflictException 이미 참가자인 토너먼트가 존재
     */
    public TournamentAdminAddUserResponseDto addTournamentUser(Long tournamentId, TournamentAdminAddUserRequestDto requestDto) {
        Tournament targetTournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException("target tournament not found", ErrorCode.TOURNAMENT_NOT_FOUND));
        if (!targetTournament.getStatus().equals(TournamentStatus.BEFORE) && !targetTournament.getStatus().equals(TournamentStatus.READY)) {
            throw new TournamentUpdateException("already started or ended", ErrorCode.TOURNAMENT_NOT_BEFORE);
        }

        User targetUser = userRepository.findByIntraId(requestDto.getIntraId()).orElseThrow(UserNotFoundException::new);
        List<Tournament> tournamentList = tournamentRepository.findAllByStatusIsNot(TournamentStatus.END);
        List<TournamentUser> tournamentsContainingTargetUser = getTournamentsContainingUser(targetUser.getIntraId(), tournamentList);

        for (TournamentUser tu : tournamentsContainingTargetUser) {
            if (tu.getTournament().getId().equals(tournamentId)) {
                throw new TournamentConflictException("user is already participant", ErrorCode.TOURNAMENT_CONFLICT);
            }
        }

        TournamentUser tournamentUser = new TournamentUser(targetUser, targetTournament,
                targetTournament.getTournamentUsers().size() < ALLOWED_JOINED_NUMBER, LocalDateTime.now());
        targetTournament.addTournamentUser(tournamentUser);
        tournamentUserRepository.save(tournamentUser);

        return new TournamentAdminAddUserResponseDto(
                targetUser.getId(),
                targetUser.getIntraId(),
                tournamentUser.isJoined()
        );
    }

    /**
     * 토너먼트 시간 체크 :
     * [ 현재 시간 + 최소 2일 ],
     * [ 현재시간 보다 미래 ],
     * [ 시작 시간이 종료시간보다 현재시에 가까움 ]
     * [ 진행 시간 최소 2시간 ]
     *
     * @param startTime 업데이트할 토너먼트 시작 시간
     * @param endTime   업데이트할 토너먼트 종료 시간
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
     * <p>tournamentList 에서 targetTournament을 제외한 토너먼트 중 겹치는 시간대 존재 유무 확인</p>
     * @param targetTournamentId 업데이트할 토너먼트 id
     * @param startTime          업데이트할 토너먼트 시작 시간
     * @param endTime            업데이트할 토너먼트 종료 시간
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

    /**
     * <p>tournamentList 에서 intraId 가 타겟 intraID인 TournamentUser 리스트 반환 매서드</p>
     * @param intraId        타겟 유저 intraId
     * @param tournamentList 토너먼트 리스트
     * @return intraId 가 타겟 intraID인 TournamentUser 리스트
     */
    private List<TournamentUser> getTournamentsContainingUser(String intraId, List<Tournament> tournamentList) {
        List<TournamentUser> result = new ArrayList<>();
        for (Tournament t : tournamentList) {
            for (TournamentUser tu : t.getTournamentUsers()) {
                if (tu.getUser().getIntraId().equals(intraId)) {
                    result.add(tu);
                }
            }
        }
        return result;
    }

    /***
     * 토너먼트 제목 중복 체크
     * @param tournamentTitle 요청 데이터에서 받아온 토너먼트 제목
     * @throws TournamentTitleConflictException 토너먼트의 제목이 겹칠 때
     */
    private void checkTournamentTitle(String tournamentTitle) {
        tournamentRepository.findByTitle(tournamentTitle).ifPresent(
                a -> {
                    throw new TournamentTitleConflictException();
                });
    }
}