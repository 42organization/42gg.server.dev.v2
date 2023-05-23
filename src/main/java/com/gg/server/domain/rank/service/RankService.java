package com.gg.server.domain.rank.service;

import com.gg.server.domain.rank.dto.ExpRankDto;
import com.gg.server.domain.rank.dto.ExpRankPageResponseDto;
import com.gg.server.domain.rank.dto.RankDto;
import com.gg.server.domain.rank.dto.RankPageResponseDto;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.service.SeasonFindService;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.dto.PageRequestDto;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.PageNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Configuration
@RequiredArgsConstructor
public class RankService {
    private final UserRepository userRepository;
    private final RankRedisRepository redisRepository;
    private final SeasonFindService seasonFindService;

    @Transactional(readOnly = true)
    public ExpRankPageResponseDto getExpRankPage(PageRequest pageRequest, UserDto curUser) {

        Long myRank = curUser.getTotalExp() == 0 ? -1 : userRepository.findExpRankingByIntraId(curUser.getIntraId());
        Page<User> users = userRepository.findAll(pageRequest);
        if(pageRequest.getPageNumber() + 1 > users.getTotalPages())
            throw new PageNotFoundException("페이지가 존재하지 않습니다.", ErrorCode.PAGE_NOT_FOUND);

        List<Long> userIds = users.getContent().stream().map(user -> user.getId()).collect(Collectors.toList());
        Season curSeason = seasonFindService.findCurrentSeason(LocalDateTime.now());

        String hashKey = RedisKeyManager.getHashKey(curSeason.getId());
        List<RankRedis> ranks = redisRepository.findRanksByUserIds(hashKey, userIds);

        Integer startRank = pageRequest.getPageSize() * pageRequest.getPageNumber() + 1;
        List<ExpRankDto> expRankDtos = new ArrayList<>();
        for(int i = 0; i < ranks.size(); i++) {
            RankRedis rank = ranks.get(i);
            User user = users.getContent().get(i);
            expRankDtos.add(ExpRankDto.from(user, startRank + i, rank.getStatusMessage()));
        }

        return new ExpRankPageResponseDto(myRank.intValue(), pageRequest.getPageNumber() + 1, users.getTotalPages(), expRankDtos);
    }

    @Transactional(readOnly = true)
    public RankPageResponseDto getRankPage(PageRequest pageRequest, UserDto curUser, Long seasonId) {
        Season season;
        if (seasonId == null || seasonId == 0) {
            season = seasonFindService.findCurrentSeason(LocalDateTime.now());
        } else {
            season = seasonFindService.findSeasonById(seasonId);
        }
        int totalPage = calcTotalPage(season, pageRequest.getPageSize());
        if (pageRequest.getPageNumber() + 1 > totalPage)
            throw new PageNotFoundException("페이지가 존재하지 않습니다.", ErrorCode.PAGE_NOT_FOUND);
        int myRank = findMyRank(curUser, season);

        int startRank = pageRequest.getPageNumber() * pageRequest.getPageSize();
        int endRank = startRank + pageRequest.getPageSize() - 1;
        List<RankDto> rankList = createRankList(startRank, endRank, season);
        return new RankPageResponseDto(myRank, pageRequest.getPageNumber() + 1, totalPage, rankList);
    }

    private int findMyRank(UserDto curUser, Season season) {
        String zSetKey = RedisKeyManager.getZSetKey(season.getId());
        String hashKey = RedisKeyManager.getHashKey(season.getId());

        RankRedis myRankRedis = redisRepository.findRankByUserId(hashKey, curUser.getId());
        Long myRank = redisRepository.getRankInZSet(zSetKey, curUser.getId());
        return (myRankRedis.getLosses() + myRankRedis.getWins() == 0)? -1 : myRank.intValue() + 1;
    }

    private int calcTotalPage(Season season, int pageSize) {
        String zSetKey = RedisKeyManager.getZSetKey(season.getId());
        Long totalUserCount = redisRepository.countTotalRank(zSetKey);
        return (int) Math.ceil((double) totalUserCount / pageSize);
    }

    private List<RankDto> createRankList(int startRank, int endRank, Season season) {
        String zSetKey = RedisKeyManager.getZSetKey(season.getId());
        String hashKey = RedisKeyManager.getHashKey(season.getId());

        List<Long> userIds = redisRepository.getUserIdsByRangeFromZSet(zSetKey, startRank, endRank);
        List<RankRedis> userRanks = redisRepository.findRanksByUserIds(hashKey, userIds);
        List<RankDto> rankList = new ArrayList<>();

        for (RankRedis userRank : userRanks) {
            rankList.add(RankDto.from(userRank, ++startRank));
        }
        return rankList;
    }
}
