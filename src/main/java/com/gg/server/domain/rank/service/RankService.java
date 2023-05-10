package com.gg.server.domain.rank.service;

import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.rank.dto.ExpRankDto;
import com.gg.server.domain.rank.dto.ExpRankPageResponseDto;
import com.gg.server.domain.rank.dto.RankDto;
import com.gg.server.domain.rank.dto.RankPageResponseDto;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankService {

    private final UserRepository userRepository;
    private final RankRedisRepository redisRepository;
    private final SeasonRepository seasonRepository;

    public ExpRankPageResponseDto getExpRankPage(int pageNum, int pageSize, UserDto curUser) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by("totalExp").descending());

        Long myRank = curUser.getTotalExp() == 0 ? -1 : userRepository.findExpRankingByIntraId(curUser.getIntraId());
        Page<User> users = userRepository.findAll(pageRequest);
        if(pageNum > users.getTotalPages())
            throw new RuntimeException("페이지가 존재하지 않습니다.");

        List<Long> userIds = users.getContent().stream().map(user -> user.getId()).collect(Collectors.toList());
        Season curSeason = seasonRepository.findCurrentSeason(LocalDateTime.now())
                .orElseThrow(() -> new NoSuchElementException("현재 시즌이 없습니다."));
        String hashKey = RedisKeyManager.getHashKey(curSeason.getId());
        List<RankRedis> ranks = redisRepository.findRanksByUserIds(hashKey, userIds);

        Integer startRank = pageRequest.getPageSize() * pageRequest.getPageNumber() + 1;
        List<ExpRankDto> expRankDtos = new ArrayList<>();
        for(int i = 0; i < ranks.size(); i++) {
            RankRedis rank = ranks.get(i);
            User user = users.getContent().get(i);
            expRankDtos.add(ExpRankDto.from(user, startRank + i, rank.getStatusMessage()));
        }

        return new ExpRankPageResponseDto(myRank.intValue(), pageNum, users.getTotalPages(), expRankDtos);
    }

    public RankPageResponseDto getRankPage(int pageNum, int pageSize, UserDto curUser, Long seasonId) {
        Season season;
        if (seasonId == null || seasonId == 0) {
            season = seasonRepository.findCurrentSeason(LocalDateTime.now())
                    .orElseThrow(() -> new NoSuchElementException("현재 시즌이 없습니다."));
        } else {
            season = seasonRepository.findById(seasonId)
                    .orElseThrow(() -> new NoSuchElementException("해당 시즌이 없습니다."));
        }

        int currentPage = pageNum;
        int totalPage = calcTotalPage(season, pageSize);
        if (pageNum > totalPage)
            throw new RuntimeException("페이지가 존재하지 않습니다.");
        int myRank = findMyRank(curUser, season);

        int startRank = (pageNum - 1) * pageSize;
        int endRank = startRank + pageSize - 1;
        List<RankDto> rankList = createRankList(startRank, endRank, season);
        return new RankPageResponseDto(myRank, currentPage, totalPage, rankList);
    }

    private int findMyRank(UserDto curUser, Season season) {
        String zSetKey = RedisKeyManager.getZSetKey(season.getId());
        String hashKey = RedisKeyManager.getHashKey(season.getId());

        RankRedis myRankRedis = redisRepository.findRankByUserId(hashKey, curUser.getId());
        Long myRank = redisRepository.getRankInZSet(zSetKey, curUser.getId());
        return (myRankRedis.getLosses() + myRankRedis.getWins() == 0)? -1 : myRank.intValue() + 1;
    }

    private int calcTotalPage(Season season, int pageSize) {
        String hashKey = RedisKeyManager.getHashKey(season.getId());
        Long totalUserCount = redisRepository.countTotalRank(hashKey);
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
