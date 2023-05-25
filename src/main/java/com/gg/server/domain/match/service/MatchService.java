package com.gg.server.domain.match.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.data.RedisMatchTime;
import com.gg.server.domain.match.data.RedisMatchUser;
import com.gg.server.domain.match.data.RedisMatchTimeRepository;
import com.gg.server.domain.match.data.RedisMatchUserRepository;
import com.gg.server.domain.match.exception.EnrolledSlotException;
import com.gg.server.domain.match.exception.SlotCountException;
import com.gg.server.domain.match.exception.SlotNotFoundException;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.noti.service.NotiService;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.service.SeasonFindService;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamRepository;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchService {
    private final RedisMatchTimeRepository redisMatchTimeRepository;
    private final RedisMatchUserRepository redisMatchUserRepository;
    private final SeasonFindService seasonFindService;
    private final RankRedisRepository rankRedisRepository;
    private final GameRepository gameRepository;
    private final SlotManagementRepository slotManagementRepository;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final UserRepository userRepository;
    private final NotiService notiService;

    @Transactional
    public void makeMatch(UserDto userDto, Option option, LocalDateTime startTime) {
        Season currentSeason = seasonFindService.findCurrentSeason(LocalDateTime.now());
        RankRedis rank = rankRedisRepository
                .findRankByUserId(RedisKeyManager.getHashKey(currentSeason.getId()), userDto.getId());
        //유저 이미 큐에 등록 시 예외 처리
        if (redisMatchUserRepository.getUserTime(userDto.getId(), startTime).isPresent()) {
            throw new EnrolledSlotException();
        }
        //3번 이상 매치 넣을 시 예외 처리
        if (redisMatchUserRepository.countMatchTime(userDto.getId()) >= 3) {
            throw new SlotCountException();
        }
        RedisMatchUser matchUser = new RedisMatchUser(userDto.getId(), rank.getPpp(), option);
        Optional<RedisMatchUser> enemy = findEnemy(startTime, matchUser, currentSeason.getPppGap());
        //1) 매칭 가능한 유저 있을 시 게임 생성 2) 없으면 큐에 넣어주기
        if (enemy.isPresent()) {
            createGame(currentSeason, startTime, enemy.get(), matchUser);
            cancelEnrolledSlots(enemy.get(), matchUser);
        } else {
            addUserToQueue(startTime, matchUser);
        }
    }

    @Transactional
    //게임 생성 전 매칭 취소
    public void cancelMatch(UserDto userDto, LocalDateTime startTime) {
        //취소 패널티는 게임이 만들어진 후 고려
        //이미 매칭이 성사되서 게임이 만들어졌다
        Optional<Game> game = gameRepository.findByStartTime(startTime);
        if (game.isPresent()) {
            //취소한 유저의 상대방에게 알람 보내기
            List<User> enemyTeam = userRepository.findEnemyByGameAndUser(game.get().getId(), userDto.getId());
            enemyTeam.forEach(enemy -> notiService.createMatchCancel(enemy, startTime));
            gameRepository.delete(game.get());//cascade 테스트
            //취소한 user에게 패널티 추가
            return;
        }
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





    //유저들이 입장할 때마다 Queue 매칭 검사
    //마지막으로 입장한 유저랑 그전 유저들만 비교하면 된다.
    //마지막 입장 유저가 both : normal 입장 유저와 경기 가능한 rank 입장 유저 매칭
    //마지막 입장 유저가 normal : normal user 탐색
    //마지막 입장 유저가 rank : 경기 가능한 rank 입장 유저 매칭
    //이때 탐색 우선 수위는 먼저 들어온 사람부터
    private Optional<RedisMatchUser> findEnemy(LocalDateTime startTime, RedisMatchUser targetUser, Integer pppGap) {
        List<RedisMatchUser> allMatchUsers = redisMatchTimeRepository.getAllMatchUsers(startTime);
        if (allMatchUsers.size() == 0) {
            return Optional.empty();
        }
        if (targetUser.getOption().equals(Option.NORMAL)) {
            return allMatchUsers.stream()
                    .filter(matchUser -> matchUser.getOption().equals(Option.NORMAL))
                    .findFirst();
        }
        if (targetUser.getOption().equals(Option.RANK)) {
            return allMatchUsers.stream()
                    .filter(matchUser -> matchUser.getOption().equals(Option.RANK)
                            && Math.abs(matchUser.getPpp() - targetUser.getPpp()) <= pppGap)
                    .findFirst();
        }
        return allMatchUsers.stream()
                .filter(matchUser -> matchUser.getOption().equals(Option.NORMAL) ||
                        matchUser.getOption().equals(Option.BOTH) ||
                        (matchUser.getOption().equals(Option.RANK) &&
                                Math.abs(matchUser.getPpp() - targetUser.getPpp())<= pppGap))
                .findFirst();
    }

    private void addUserToQueue(LocalDateTime startTime, RedisMatchUser matchUser) {
        redisMatchTimeRepository.addMatchUser(startTime, matchUser);
        redisMatchTimeRepository.setMatchTimeWithExpiry(startTime);
        redisMatchUserRepository.addMatchTime(matchUser.getUserId(), startTime);
    }

    private void cancelEnrolledSlots(RedisMatchUser enemy, RedisMatchUser player) {
        Set<RedisMatchTime> enemyMatchTimes = redisMatchUserRepository.getAllMatchTime(enemy.getUserId());
        Set<RedisMatchTime> playerMatchTimes = redisMatchUserRepository.getAllMatchTime(player.getUserId());
        for (RedisMatchTime matchTime : enemyMatchTimes) {
            redisMatchTimeRepository.deleteMatchUser(matchTime.getStartTime(), enemy);
        }
        for (RedisMatchTime matchTime : playerMatchTimes) {
            redisMatchTimeRepository.deleteMatchUser(matchTime.getStartTime(), player);
        }
        redisMatchUserRepository.deleteMatchUser(enemy.getUserId());
        redisMatchUserRepository.deleteMatchUser(player.getUserId());

    }
    private void createGame(Season currentSeason, LocalDateTime startTime, RedisMatchUser enemy, RedisMatchUser player) {
        SlotManagement slotManagement = slotManagementRepository.findFirstByOrderByCreatedAtDesc();
        Integer interval = slotManagement.getGameInterval();
        Mode mode = getMode(enemy, player, currentSeason.getPppGap());
        Game game = new Game(currentSeason, StatusType.BEFORE, mode, startTime, startTime.plusMinutes(interval));
        gameRepository.saveAndFlush(game);
        Team enemyTeam =  new Team(game, -1, false);
        Team myTeam = new Team(game, -1, false);
        List<Team> match = List.of(enemyTeam, myTeam);
        teamRepository.saveAllAndFlush(match);
        User playerUser = userRepository.findById(player.getUserId()).orElseThrow(UserNotFoundException::new);
        User enemyUser = userRepository.findById(enemy.getUserId()).orElseThrow(UserNotFoundException::new);
        TeamUser myTeamUser = new TeamUser(myTeam, playerUser);
        TeamUser enemyTeamUser = new TeamUser(enemyTeam, enemyUser);
        List<TeamUser> matchTeamUser = List.of(enemyTeamUser, myTeamUser);
        teamUserRepository.saveAllAndFlush(matchTeamUser);
        redisMatchTimeRepository.deleteMatchTime(startTime);
        redisMatchUserRepository.deleteMatchTime(enemy.getUserId(), startTime);
        notiService.createMatched(playerUser, startTime);
        notiService.createMatched(enemyUser, startTime);
    }

    private Mode getMode(RedisMatchUser enemy, RedisMatchUser player, Integer pppGap) {
        if (enemy.getOption().equals(Option.BOTH) && player.getOption().equals(Option.BOTH)) {
            if (Math.abs(player.getPpp() - enemy.getPpp()) <= pppGap) {
                return Mode.RANK;
            }
            return Mode.NORMAL;
        }
        if (!player.getOption().equals(Option.BOTH)) {
            return Mode.getEnumValue(player.getOption().getCode());
        }
        return Mode.getEnumValue(enemy.getOption().getCode());
    }
}
