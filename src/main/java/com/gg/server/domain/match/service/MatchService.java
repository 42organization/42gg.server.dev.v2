package com.gg.server.domain.match.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.exception.GameAlreadyExistException;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.data.RedisMatchTime;
import com.gg.server.domain.match.data.RedisMatchTimeRepository;
import com.gg.server.domain.match.data.RedisMatchUser;
import com.gg.server.domain.match.data.RedisMatchUserRepository;
import com.gg.server.domain.match.dto.GameAddDto;
import com.gg.server.domain.match.exception.EnrolledSlotException;
import com.gg.server.domain.match.exception.PenaltyUserSlotException;
import com.gg.server.domain.match.exception.SlotCountException;
import com.gg.server.domain.match.exception.SlotNotFoundException;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.match.utils.MatchCalculator;
import com.gg.server.domain.penalty.service.PenaltyService;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.service.SeasonFindService;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.tournament.exception.TournamentConflictException;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final RedisMatchTimeRepository redisMatchTimeRepository;
    private final RedisMatchUserRepository redisMatchUserRepository;
    private final SeasonFindService seasonFindService;
    private final RankRedisRepository rankRedisRepository;
    private final GameRepository gameRepository;
    private final PenaltyService penaltyService;
    private final GameUpdateService gameUpdateService;
    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;

    /**
     * 1) 매칭 가능한 유저 있을 경우 : 게임 생성
     * 2) 매칭 가능한 유저 없을 경우 : 유저를 큐에 넣어줌
     */
    @Transactional
    public synchronized void makeMatch(UserDto userDto, Option option, LocalDateTime startTime) {
        checkValid(userDto, startTime);
        Season season = seasonFindService.findCurrentSeason(startTime);
        RankRedis rank = rankRedisRepository
                .findRankByUserId(RedisKeyManager.getHashKey(season.getId()), userDto.getId());
        RedisMatchUser player = new RedisMatchUser(userDto.getId(), rank.getPpp(), option);
        List<RedisMatchUser> allMatchUsers = redisMatchTimeRepository.getAllMatchUsers(startTime);
        MatchCalculator matchCalculator = new MatchCalculator(season.getPppGap(), player);
        Optional<RedisMatchUser> enemy = matchCalculator.findEnemy(allMatchUsers);
        if (enemy.isPresent()) {
            GameAddDto gameDto = new GameAddDto(startTime, season, player, enemy.get());
            gameUpdateService.make(gameDto, -1L);
            redisMatchTimeRepository.addMatchUser(startTime, player);
            cancelEnrolledSlots(List.of(enemy.get(), player), startTime);
        } else {
            addUserToQueue(startTime, player, option);
        }
    }

    /**
     * 1) 매칭되어 게임 생성된 후 : 게임 삭제하고 알림 전송, 취소한 유저 패널티 부과
     * 복귀 유저는 매칭 가능한 상대 존재하면 다시 매칭해주고 아니면 취소 알림 보내고 큐에 등록 시킴
     * 2) 매칭 전 : 큐에서 유저 삭제
     * */
    /**
     * game 매칭된 user 이외에 다른 user가 취소할 경우, 에러 발생
     */
    @Transactional
    public synchronized void cancelMatch(UserDto userDto, LocalDateTime startTime) {
        Optional<Game> game = gameRepository.findByStartTime(startTime);
        if (game.isPresent()) {
            List<User> enemyTeam = userRepository.findEnemyByGameAndUser(game.get().getId(), userDto.getId());
            if (enemyTeam.size() > 1) {
                throw new SlotNotFoundException();
            }
            if (game.get().getMode().equals(Mode.TOURNAMENT)) {
                throw new BusinessException(ErrorCode.TOURNAMENT_GAME_CAN_NOT_CANCELED);
            }
            cancelGame(userDto, startTime, game.get(), enemyTeam);
        } else {
            deleteUserFromQueue(userDto, startTime);
        };
    }

    private void cancelGame(UserDto userDto, LocalDateTime startTime, Game game, List<User> enemyTeam) {
        /**취소한 유저 큐에서 삭제 후 패널티 부과*/
        Long recoveredUserId = enemyTeam.get(0).getId();
        List<RedisMatchUser> allMatchUsers = redisMatchTimeRepository.getAllMatchUsers(startTime);
        RedisMatchUser penaltyUser = allMatchUsers.stream()
                .filter(ele -> ele.getUserId().equals(userDto.getId()))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
        RedisMatchUser recoveredUser = allMatchUsers.stream()
                .filter(ele -> ele.getUserId().equals(recoveredUserId))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
        redisMatchTimeRepository.deleteMatchUser(startTime, penaltyUser);
        penaltyService.givePenalty(userDto, 30);
        /**취소 당한 유저 매칭 상대 찾고 있으면 다시 게임 생성 아니면 취소 알림*/
        Season season = seasonFindService.findCurrentSeason(startTime);
        MatchCalculator matchCalculator = new MatchCalculator(season.getPppGap(), recoveredUser);
        List<RedisMatchUser> targetPlayers = allMatchUsers.stream()
                .filter(ele -> !ele.getUserId().equals(userDto.getId())
                        && !ele.getUserId().equals(recoveredUserId))
                .collect(Collectors.toList());
        Optional<RedisMatchUser> enemy = matchCalculator.findEnemy(targetPlayers);
        if (enemy.isPresent()) {
            gameUpdateService.delete(game);
            GameAddDto gameDto = new GameAddDto(startTime, season, recoveredUser, enemy.get());
            gameUpdateService.make(gameDto, recoveredUserId);
        } else {
            gameUpdateService.delete(game, enemyTeam);
            redisMatchUserRepository.addMatchTime(recoveredUserId, startTime, recoveredUser.getOption());
        }
    }

    /**
     * 매칭 요청 시 유효성 검사
     * @param userDto 매칭 요청한 유저
     * @param startTime 매칭 요청 시간
     * @throws PenaltyUserSlotException 패널티 유저일 경우
     * @throws TournamentConflictException 토너먼트가 존재할 경우
     * @throws GameAlreadyExistException 게임이 이미 존재할 경우
     * @throws EnrolledSlotException 매칭된 게임이 이미 있을 경우 || 유저 이미 큐에 등록할 경우
     * @throws SlotCountException 4번 이상 매치 넣을 경우
     *
     */
    private void checkValid(UserDto userDto, LocalDateTime startTime) {
        if (penaltyService.isPenaltyUser(userDto.getIntraId())) {
            throw new PenaltyUserSlotException();
        }
        if (isExistTournamentNotEnded(startTime)) {
            throw new TournamentConflictException();
        }
        if (gameRepository.findByStartTime(startTime).isPresent()) {
            throw new GameAlreadyExistException();
        }
        if (gameRepository.findByStatusTypeAndUserId(StatusType.BEFORE, userDto.getId()).isPresent()) {
            throw new EnrolledSlotException();
        }
        //유저 이미 큐에 등록 시 예외 처리
        if (redisMatchUserRepository.getUserTime(userDto.getId(), startTime).isPresent()) {
            throw new EnrolledSlotException();
        }
        //4번 이상 매치 넣을 시 예외 처리
        if (redisMatchUserRepository.countMatchTime(userDto.getId()) >= 3) {
            throw new SlotCountException();
        }
    }

    private void addUserToQueue(LocalDateTime startTime, RedisMatchUser matchUser, Option option) {
        redisMatchTimeRepository.addMatchUser(startTime, matchUser);
        redisMatchTimeRepository.setMatchTimeWithExpiry(startTime);
        redisMatchUserRepository.addMatchTime(matchUser.getUserId(), startTime, option);
    }

    private void cancelEnrolledSlots(List<RedisMatchUser> players, LocalDateTime targetTIme) {
        for (RedisMatchUser player : players) {
            Set<RedisMatchTime> matchTimes = redisMatchUserRepository
                    .getAllMatchTime(player.getUserId())
                    .stream()
                    .filter(ele -> !ele.getStartTime().equals(targetTIme))
                    .collect(Collectors.toSet());
            matchTimes.stream().forEach(ele -> redisMatchTimeRepository.deleteMatchUser(ele.getStartTime(), player));
            redisMatchUserRepository.deleteMatchUser(player.getUserId());
        }
    }

    private void deleteUserFromQueue(UserDto userDto, LocalDateTime startTime) {
        if (redisMatchUserRepository.getUserTime(userDto.getId(), startTime).isEmpty()) {
            throw new SlotNotFoundException();
        }
        redisMatchUserRepository.deleteMatchTime(userDto.getId(), startTime);
        List<RedisMatchUser> allMatchUsers = redisMatchTimeRepository.getAllMatchUsers(startTime);
        for (RedisMatchUser matchUser : allMatchUsers) {
            if (matchUser.getUserId().equals(userDto.getId())) {
                redisMatchTimeRepository.deleteMatchUser(startTime, matchUser);
                break;
            }
        }
    }

    /**
     * LIVE, BEFORE 상태인 토너먼트와 진행 시간이 겹치지 않으면 true, 겹치면 false
     * @param time 현재 시간
     * @return 종료되지 않은 토너먼트 있으면 true, 없으면 false
     */
    private boolean isExistTournamentNotEnded(LocalDateTime time) {
        List<Tournament> tournamentList = tournamentRepository.findAllByStatusIsNot(TournamentStatus.END);
        if (tournamentList.isEmpty()) {
            return false;
        }
        for (Tournament tournament : tournamentList) {
            if (time.isAfter(tournament.getStartTime()) &&
                time.isBefore(tournament.getEndTime())) {
                return false;
            }
            if (time.isEqual(tournament.getStartTime()) ||
                time.isEqual(tournament.getEndTime())) {
                return false;
            }
        }
        return true;
    }
}
