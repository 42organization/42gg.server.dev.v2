package com.gg.server.domain.rank.service;

import com.gg.server.domain.rank.dto.ExpRankDto;
import com.gg.server.domain.rank.dto.ExpRankPageResponseDto;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankService {

    private final UserRepository userRepository;
    private final RankRedisRepository redisRepository;
    private final SeasonRepository seasonRepository;
    public ExpRankPageResponseDto getExpRankPage(int pageNum, int pageSize, UserDto curUser) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by("totalExp").descending());

        Long myRank = curUser.getTotalExp() == 0 ? -1 : userRepository.findExpRankingByIntraId(curUser.getIntraId());
        Page<User> users = userRepository.findAll(pageRequest);

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
}
