package com.gg.server.domain.match.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.data.RedisMatchTime;
import com.gg.server.domain.match.data.RedisMatchTimeRepository;
import com.gg.server.domain.match.data.RedisMatchUser;
import com.gg.server.domain.match.data.RedisMatchUserRepository;
import com.gg.server.domain.match.dto.MatchStatusDto;
import com.gg.server.domain.match.dto.MatchStatusResponseListDto;
import com.gg.server.domain.match.dto.MatchUserInfoDto;
import com.gg.server.domain.match.dto.SlotStatusDto;
import com.gg.server.domain.match.dto.SlotStatusResponseListDto;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.match.type.SlotStatus;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.service.SeasonFindService;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchFindService {
    private final SlotManagementRepository slotManagementRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final RedisMatchUserRepository redisMatchUserRepository;
    private final SeasonFindService seasonFindService;
    private final RankRedisRepository rankRedisRepository;
    private final RedisMatchTimeRepository redisMatchTimeRepository;

    @Transactional(readOnly = true)
    public MatchStatusResponseListDto getCurrentMatch(UserDto userDto) {
        SlotManagement slotManagement = slotManagementRepository.findFirstByOrderByCreatedAtDesc();
        Optional<Game> myGame = gameRepository.findByStatusTypeAndUserId(StatusType.BEFORE, userDto.getId());
        if (myGame.isPresent()) {
            List<User> enemyTeam = userRepository.findEnemyByGameAndUser(myGame.get().getId(), userDto.getId());
            return new MatchStatusResponseListDto(getMatchedListDto(myGame.get(), List.of(userDto.getIntraId()),
                    List.of(enemyTeam.get(0).getIntraId()), slotManagement));
        }
        Set<RedisMatchTime> enrolledSlots = redisMatchUserRepository.getAllMatchTime(userDto.getId());
        List<MatchStatusDto> dtos = enrolledSlots.stream()
                .map(e -> new MatchStatusDto(e, slotManagement.getGameInterval()))
                .sorted(Comparator.comparing(MatchStatusDto::getStartTime))
                .collect(Collectors.toList());
        return new MatchStatusResponseListDto(dtos);
    }

    @Transactional(readOnly = true)
    public SlotStatusResponseListDto getAllMatchStatus(Long userId, Option option) {
        SlotManagement slotManagement = slotManagementRepository.findFirstByOrderByCreatedAtDesc();
        Season currentSeason = seasonFindService.findCurrentSeason(LocalDateTime.now());
        RankRedis user = rankRedisRepository.
                findRankByUserId(RedisKeyManager.getHashKey(currentSeason.getId()), userId);
        MatchUserInfoDto matchUser = new MatchUserInfoDto(option, user, currentSeason, slotManagement);
        return getResponseDto(matchUser);
    }

    private List<MatchStatusDto> getMatchedListDto(Game game, List<String> myTeam, List<String> enemyTeam,
                                                   SlotManagement slotManagement) {
        List<MatchStatusDto> dtos = new ArrayList<MatchStatusDto>();
        dtos.add(new MatchStatusDto(game, myTeam, enemyTeam, slotManagement));
        return dtos;
    }

    private SlotStatusResponseListDto getResponseDto(MatchUserInfoDto matchUser) {
        HashMap <LocalDateTime, SlotStatusDto> slots = new HashMap<LocalDateTime, SlotStatusDto>();
        //slot 종류마다 grouping
        groupPastSlots(slots, matchUser);
        groupMatchedSlots(slots, matchUser);
        groupMySlots(slots, matchUser);
        groupEnrolledSlots(slots, matchUser);
        //HashMap -> List
        List<SlotStatusDto> matchBoards = new ArrayList<SlotStatusDto>();
        SlotManagement slotManagement = matchUser.getSlotManagement();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime standardTime = LocalDateTime.of(
                now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0);
        LocalDateTime minTime = standardTime.minusHours(slotManagement.getPastSlotTime());
        LocalDateTime maxTime = standardTime.plusHours(slotManagement.getFutureSlotTime());
        Integer interval = slotManagement.getGameInterval();
        for (LocalDateTime time = minTime ; time.isBefore(maxTime) ; time = time.plusMinutes(interval)) {
            SlotStatusDto dto = slots.getOrDefault(time, getMatchStatusDto(time, SlotStatus.OPEN, interval));
            matchBoards.add(dto);
        }
        return new SlotStatusResponseListDto(matchBoards);
    }


    private void groupPastSlots(HashMap<LocalDateTime, SlotStatusDto> slots, MatchUserInfoDto matchUser) {
        SlotManagement slotManagement = matchUser.getSlotManagement();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime standardTime = LocalDateTime.of(
                now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0);
        LocalDateTime minTime = standardTime.minusHours(slotManagement.getPastSlotTime());
        Integer interval = slotManagement.getGameInterval();
        for (LocalDateTime time = minTime; time.isBefore(now); time = time.plusMinutes(interval)) {
            slots.put(time, getMatchStatusDto(time, SlotStatus.CLOSE, interval));
        }
    }

    private void groupMatchedSlots(HashMap<LocalDateTime, SlotStatusDto> slots,
                                   MatchUserInfoDto matchUser) {
        SlotManagement slotManagement = matchUser.getSlotManagement();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime maxTime = LocalDateTime.of(
                now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0)
                .plusHours(slotManagement.getFutureSlotTime());
        List<Game> games = gameRepository.findAllBetween(now, maxTime);
        if (games.size() != 0) {
            Integer interval = slotManagement.getGameInterval();
            games.stream().forEach(e -> slots.put(e.getStartTime(),
                    getMatchStatusDto(e.getStartTime(), SlotStatus.CLOSE, interval)));
            Optional<Game> myGame = gameRepository.findByUserInSlots(now, maxTime, matchUser.getUserId());
            myGame.ifPresent(game -> slots.put(game.getStartTime(),
                    getMatchStatusDto(game.getStartTime(), SlotStatus.MYTABLE, interval)));
        }
    }

    private void groupMySlots(HashMap<LocalDateTime, SlotStatusDto> slots, MatchUserInfoDto matchUser) {
        Integer interval = matchUser.getSlotManagement().getGameInterval();
        Set<RedisMatchTime> allMatchTime = redisMatchUserRepository.getAllMatchTime(matchUser.getUserId());
        allMatchTime.stream().forEach(match -> slots.put(match.getStartTime(),
                getMatchStatusDto(match.getStartTime(), SlotStatus.MYTABLE, interval)));
    }

    private void groupEnrolledSlots(HashMap<LocalDateTime, SlotStatusDto> slots, MatchUserInfoDto matchUser) {

        Set<LocalDateTime> enrolledSlots = redisMatchTimeRepository.getAllEnrolledStartTimes();
            Set<LocalDateTime> notMyEnrolledTimes = enrolledSlots.stream().filter(e -> !slots.containsKey(e))
                    .collect(Collectors.toSet());
            Integer interval = matchUser.getSlotManagement().getGameInterval();
            notMyEnrolledTimes.stream().forEach(time ->
                    slots.put(time, getMatchStatusDto(time, getEnemyStatus(time, matchUser), interval)));
    }

    private SlotStatusDto getMatchStatusDto(LocalDateTime startTime, SlotStatus status, Integer interval) {
        return new SlotStatusDto(startTime, startTime.plusMinutes(interval), status);
    }

    private SlotStatus getEnemyStatus(LocalDateTime startTime, MatchUserInfoDto matchUser) {
        List<RedisMatchUser> allMatchUsers = redisMatchTimeRepository.getAllMatchUsers(startTime);
        if (matchUser.getOption().equals(Option.NORMAL)) {
            return getNormalSlotStatus(allMatchUsers);
        }
        if (matchUser.getOption().equals(Option.RANK)) {
            return getRankSlotStatus(allMatchUsers, matchUser);
        }
        return getBothSlotStatus(allMatchUsers, matchUser);
    }
    private SlotStatus getRankSlotStatus(List<RedisMatchUser> allMatchUsers, MatchUserInfoDto matchUser) {
        if (allMatchUsers.stream().anyMatch(e -> e.getOption().equals(Option.RANK)
                && (e.getPpp() - matchUser.getUserPpp()) <= matchUser.getPppGap())) {
            return SlotStatus.MATCH;
        }
        return SlotStatus.OPEN;
    }

    private SlotStatus getNormalSlotStatus(List<RedisMatchUser> allMatchUsers) {
        if (allMatchUsers.stream().anyMatch(e -> e.getOption().equals(Option.NORMAL))) {
            return SlotStatus.MATCH;
        }
        return SlotStatus.OPEN;
    }

    private SlotStatus getBothSlotStatus(List<RedisMatchUser> allMatchUsers, MatchUserInfoDto matchUser) {
        if (allMatchUsers.stream().anyMatch(e ->
                e.getOption().equals(Option.NORMAL) || e.getOption().equals(Option.BOTH)
                        || (e.getOption().equals(Option.RANK) &&
                        e.getPpp() - matchUser.getUserPpp() <= matchUser.getPppGap()))) {
            return SlotStatus.MATCH;
        }
        return SlotStatus.OPEN;
    }
}
