package com.gg.server.domain.match.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.exception.GameAlreadyExistException;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.data.RedisMatchTime;
import com.gg.server.domain.match.data.RedisMatchUser;
import com.gg.server.domain.match.data.RedisMatchTimeRepository;
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
import com.gg.server.domain.user.dto.UserDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
            gameUpdateService.make(gameDto);
            cancelEnrolledSlots(List.of(enemy.get(), player), startTime);
        } else {
            addUserToQueue(startTime, player, option);
        }
    }

    /**
     * 1) 매칭되어 게임 생성된 후 : 게임 삭제하고 알림 전송, 취소한 유저 패널티 부과
     * 2) 매칭 전 : 큐에서 유저 삭제
     * */
    @Transactional
    public synchronized void cancelMatch(UserDto userDto, LocalDateTime startTime) {
        Optional<Game> game = gameRepository.findByStartTime(startTime);
        if (game.isPresent()) {
            gameUpdateService.delete(game.get(), userDto);//cascade 테스트
            penaltyService.givePenalty(userDto, 30);
            return;
        }
        deleteUserFromQueue(userDto, startTime);
    }

    private void checkValid(UserDto userDto, LocalDateTime startTime) {
        if (penaltyService.isPenaltyUser(userDto.getIntraId())) {
            throw new PenaltyUserSlotException();
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

    private void cancelEnrolledSlots(List<RedisMatchUser> players, LocalDateTime startTime) {
        redisMatchTimeRepository.deleteMatchTime(startTime);
        for (RedisMatchUser player : players) {
            Set<RedisMatchTime> matchTimes = redisMatchUserRepository.getAllMatchTime(player.getUserId());
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

}
