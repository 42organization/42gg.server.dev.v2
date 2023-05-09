package com.gg.server.domain.match.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.data.RedisMatchTime;
import com.gg.server.domain.match.data.RedisMatchUser;
import com.gg.server.domain.match.data.RedisMatchTimeRepository;
import com.gg.server.domain.match.data.RedisMatchUserRepository;
import com.gg.server.domain.match.dto.MatchStatusDto;
import com.gg.server.domain.match.dto.MatchStatusResponseListDto;
import com.gg.server.domain.match.type.SlotStatus;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamRepository;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.domain.user.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchRedisService {
    private final RedisMatchTimeRepository redisMatchTimeRepository;
    private final RedisMatchUserRepository redisMatchUserRepository;
    private final SeasonRepository seasonRepository;
    private final RankRedisRepository rankRedisRepository;
    private final GameRepository gameRepository;
    private final SlotManagementRepository slotManagementRepository;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final UserRepository userRepository;

    public Long countUserMatch(Long userId) {
        return redisMatchUserRepository.countMatchTime(userId);
    }
    public Boolean isUserMatch(Long userId, LocalDateTime startTime) {
        RedisMatchTime matchTime = new RedisMatchTime(startTime);
        if (redisMatchUserRepository.getMatchUserOrder(userId, matchTime) != null) {
            return true;
        }
        return false;
    }
    public synchronized void makeMatch(Long userId, Option option, LocalDateTime startTime) {
        Season currentSeason = seasonRepository.findCurrentSeason(LocalDateTime.now()).get();
        RankRedis rank = rankRedisRepository
                .findRankByUserId(RedisKeyManager.getHashKey(currentSeason.getId()), userId);
        RedisMatchTime matchTime = new RedisMatchTime(startTime);
        RedisMatchUser matchUser = new RedisMatchUser(userId, rank.getPpp(), option);
        //유저 이미 큐에 등록 시 예외 처리
        if (redisMatchUserRepository.getMatchUserOrder(userId, matchTime) != null) {
            return;
        }
        //3번 이상 매치 넣을 시 예외 처리
        if (redisMatchUserRepository.countMatchTime(userId) >= 3) {
            return;
        }
        //1) 매칭 가능한 유저 있을 시 게임 생성 2) 없으면 큐에 넣어주기
        Optional<RedisMatchUser> enemy = findEnemy(matchTime, matchUser, currentSeason.getPppGap());
        if (enemy.isEmpty()) {
            addUserToQueue(matchTime, matchUser);
            return;
        }
        //게임생성
        createGame(currentSeason, startTime, enemy.get(), matchUser);
        cancelEnrolledSlots(enemy.get(), matchUser);
    }

    //게임 생성 전 매칭 취소
    public void cancelMatch(Long userId, LocalDateTime startTime) {
        //취소 패널티는 게임이 만들어진 후 고려
        //이미 매칭이 성사되서 게임이 만들어졌다면
        Optional<Game> game = gameRepository.findByStartTime(startTime);
        if (game.isPresent()) {
            List<Team> teams = teamRepository.findAllBy(game.get().getId());
            TeamUser teamUser1 = teamUserRepository.findByTeam(teams.get(0).getId());
            TeamUser teamUser2 = teamUserRepository.findByTeam(teams.get(1).getId());
            teamRepository.deleteAll(teams);
            teamUserRepository.deleteAll(List.of(teamUser1, teamUser2));
            gameRepository.delete(game.get());
            //취소한 user에게 패널티 추가
            return;
        }
        RedisMatchTime matchTime = new RedisMatchTime(startTime);
        redisMatchUserRepository.deleteMatchTime(userId, matchTime);
        List<RedisMatchUser> allMatchUsers = redisMatchTimeRepository.getAllMatchUsers(startTime);
        for (RedisMatchUser matchUser : allMatchUsers) {
            if (matchUser.getUserId().equals(userId)) {
                redisMatchTimeRepository.deleteMatchUser(startTime, matchUser);
                break;
            }
        }
    }

    public MatchStatusResponseListDto getAllMatchStatus(Long userId, Option option) {
        SlotManagement slotManagement = slotManagementRepository.findFirstByOrderByCreatedAtDesc();
        LocalDateTime now = LocalDateTime.now();
        Season currentSeason = seasonRepository.findCurrentSeason(now).get();
        RankRedis user = rankRedisRepository.
                findRankByUserId(RedisKeyManager.getHashKey(currentSeason.getId()), userId);
        HashMap <LocalDateTime, MatchStatusDto> slots = new HashMap<LocalDateTime, MatchStatusDto>();
        groupPastSlots(slots, slotManagement);
        groupMatchedSlots(slots, slotManagement, user);
        groupMySlots(slots, slotManagement, user);
        groupEnrolledSlots(slots, slotManagement, option, currentSeason, user);
        return getResponseDto(slots, slotManagement);
    }

    private MatchStatusResponseListDto getResponseDto(HashMap<LocalDateTime, MatchStatusDto> slots,
                                                      SlotManagement slotManagement) {
        List<MatchStatusDto> matchBoards = new ArrayList<MatchStatusDto>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime standardTime = LocalDateTime.of(
                now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0);
        LocalDateTime minTime = standardTime.minusHours(slotManagement.getPastSlotTime());
        LocalDateTime maxTime = standardTime.plusHours(slotManagement.getFutureSlotTime());
        Integer interval = slotManagement.getGameInterval();
        for (LocalDateTime time = minTime ; time.isBefore(maxTime) ; time = time.plusMinutes(interval)) {
            MatchStatusDto dto = slots.getOrDefault(time, getMatchStatusDto(time, SlotStatus.OPEN, interval));
            matchBoards.add(dto);
        }
        return MatchStatusResponseListDto.builder()
                .matchBoards(matchBoards)
                .build();
    }
    private void groupPastSlots(HashMap<LocalDateTime, MatchStatusDto> slots, SlotManagement slotManagement) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime standardTime = LocalDateTime.of(
                now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0);
        LocalDateTime minTime = standardTime.minusHours(slotManagement.getPastSlotTime());
        Integer interval = slotManagement.getGameInterval();
        for (LocalDateTime time = minTime; time.isBefore(now); time = time.plusMinutes(interval)) {
            slots.put(time, getMatchStatusDto(time, SlotStatus.CLOSE, interval));
        }
    }

    private void groupMySlots(HashMap<LocalDateTime, MatchStatusDto> slots, SlotManagement slotManagement, RankRedis user) {
        Integer interval = slotManagement.getGameInterval();
        Set<RedisMatchTime> allMatchTime = redisMatchUserRepository.getAllMatchTime(user.getUserId());
        for (RedisMatchTime matchTime : allMatchTime) {
            slots.put(matchTime.getStartTime(),
                    getMatchStatusDto(matchTime.getStartTime(), SlotStatus.MYTABLE, interval));
        }
    }

    private void groupEnrolledSlots(HashMap<LocalDateTime, MatchStatusDto> slots, SlotManagement slotManagement,
                                    Option option, Season season, RankRedis user) {

        Optional<Set<LocalDateTime>> enrolledSlots = redisMatchTimeRepository.getAllEnrolledStartTimes();
        if (enrolledSlots.isPresent()) {
            Set<LocalDateTime> times = enrolledSlots.get().stream().filter(e -> !slots.containsKey(e))
                    .collect(Collectors.toSet());
            Integer interval = slotManagement.getGameInterval();
            times.stream().forEach(e -> getMatchStatusDto(e, getEnemyStatus(e, user, option, season), interval));
        }
    }

    private void groupMatchedSlots(HashMap<LocalDateTime, MatchStatusDto> slots,
                                   SlotManagement slotManagement, RankRedis user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime standardTime = LocalDateTime.of(
                now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0);
        LocalDateTime maxTime = standardTime.plusHours(slotManagement.getFutureSlotTime());
        Optional<List<Game>> games = gameRepository.findAllBetween(now, maxTime);
        if (games.isPresent()) {
            Integer interval = slotManagement.getGameInterval();
            games.get().stream().forEach(e -> slots.put(e.getStartTime(),
                    getMatchStatusDto(e.getStartTime(), SlotStatus.CLOSE, interval)));
            Optional<Game> myGame = gameRepository.findByUserInSlots(now, maxTime, user.getUserId());
            myGame.ifPresent(game -> slots.put(game.getStartTime(),
                    getMatchStatusDto(game.getStartTime(), SlotStatus.MYTABLE, interval)));
        }
    }

    private SlotStatus getEnemyStatus(LocalDateTime startTime, RankRedis user, Option option, Season season) {
        List<RedisMatchUser> allMatchUsers = redisMatchTimeRepository.getAllMatchUsers(startTime);
        if (option == Option.NORMAL) {
            if (allMatchUsers.stream().anyMatch(e -> e.getOption().equals(Option.NORMAL.getCode()))) {
                return SlotStatus.MATCH;
            }
        }
        if (option == Option.RANK) {
            if (allMatchUsers.stream().anyMatch(e -> e.getOption().equals(Option.RANK.getCode())
                    && (e.getPpp() - user.getPpp()) <= season.getPppGap())) {
                return SlotStatus.MATCH;
            }
        }
        if (allMatchUsers.stream().anyMatch(e ->
                e.getOption().equals(Option.NORMAL.getCode()) || e.getOption().equals(Option.BOTH.getCode())
                        || (e.getOption().equals(Option.RANK.getCode()) &&
                        e.getPpp() - user.getPpp() <= season.getPppGap()))) {
            return SlotStatus.MATCH;
        }
        return SlotStatus.OPEN;
    }


    private MatchStatusDto getMatchStatusDto(LocalDateTime startTime, SlotStatus status, Integer interval) {
        return MatchStatusDto.builder().status(status.getCode())
                .startTime(startTime)
                .endTime(startTime.plusMinutes(interval))
                .build();
    }


    //유저들이 입장할 때마다 Queue 매칭 검사
    //마지막으로 입장한 유저랑 그전 유저들만 비교하면 된다.
    //마지막 입장 유저가 both : normal 입장 유저와 경기 가능한 rank 입장 유저 매칭
    //마지막 입장 유저가 normal : normal user 탐색
    //마지막 입장 유저가 rank : 경기 가능한 rank 입장 유저 매칭
    //이때 탐색 우선 수위는 먼저 들어온 사람부터
    private Optional<RedisMatchUser> findEnemy(RedisMatchTime matchTime, RedisMatchUser targetUser, Integer pppGap) {
        List<RedisMatchUser> allMatchUsers = redisMatchTimeRepository.getAllMatchUsers(matchTime.getStartTime());
        if (allMatchUsers.size() == 0) {
            return Optional.empty();
        }
        if (targetUser.getOption().equals(Option.NORMAL.getCode())) {
            return allMatchUsers.stream()
                    .filter(matchUser -> matchUser.getOption().equals(Option.NORMAL.getCode()))
                    .findFirst();
        }
        if (targetUser.getOption().equals(Option.RANK.getCode())) {
            return allMatchUsers.stream()
                    .filter(matchUser -> matchUser.getOption().equals(Option.RANK.getCode())
                    && Math.abs(matchUser.getPpp() - targetUser.getPpp())<= pppGap)
                    .findFirst();
        }
        return allMatchUsers.stream()
                .filter(matchUser -> matchUser.getOption().equals(Option.NORMAL.getCode()) ||
                        matchUser.getOption().equals(Option.BOTH.getCode()) ||
                        (matchUser.getOption().equals(Option.RANK.getCode()) &&
                                Math.abs(matchUser.getPpp() - targetUser.getPpp())<= pppGap))
                .findFirst();
    }

    private void addUserToQueue(RedisMatchTime matchTime, RedisMatchUser matchUser) {
        redisMatchTimeRepository.addMatchUser(matchTime.getStartTime(), matchUser);
        redisMatchTimeRepository.setMatchTimeWithExpiry(matchTime.getStartTime());
        redisMatchUserRepository.addMatchTime(matchUser.getUserId(), matchTime);
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
        TeamUser myTeamUser = new TeamUser(myTeam, userRepository.findById(player.getUserId()).get());
        TeamUser enemyTeamUser = new TeamUser(enemyTeam, userRepository.findById(enemy.getUserId()).get());
        List<TeamUser> matchTeamUser = List.of(enemyTeamUser, myTeamUser);
        teamUserRepository.saveAllAndFlush(matchTeamUser);
        redisMatchTimeRepository.deleteMatchTime(startTime);
        redisMatchUserRepository.deleteMatchTime(enemy.getUserId(), new RedisMatchTime(startTime));
    }

    private Mode getMode(RedisMatchUser enemy, RedisMatchUser player, Integer pppGap) {
        if (enemy.getOption().equals(Option.BOTH.getCode()) && enemy.getOption().equals(Option.BOTH.getCode())) {
            if (Math.abs(player.getPpp() - enemy.getPpp()) <= pppGap) {
                return Mode.RANK;
            }
            return Mode.NORMAL;
        }
        if (!player.getOption().equals(Option.BOTH.getCode())) {
            return Mode.getEnumValue(player.getOption());
        }
        return Mode.getEnumValue(enemy.getOption());
    }
}
