package com.gg.server.admin.user.service;
import com.gg.server.admin.season.data.SeasonAdminRepository;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.admin.user.dto.UserSearchAdminDto;
import com.gg.server.admin.user.dto.UserSearchAdminResponseDto;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.user.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserAdminService {

    private final UserAdminRepository userAdminRepository;
    private final SeasonAdminRepository seasonAdminRepository;
    private final RankRedisRepository rankRedisRepository;

    @Transactional(readOnly = true)
    public UserSearchAdminResponseDto searchAll(Pageable pageable) {
        Page<User> userPage = userAdminRepository.findAll(pageable);
        List<UserSearchAdminDto> userSearchAdminDtos = new ArrayList<UserSearchAdminDto>();
        for (User user : userPage.getContent())
            userSearchAdminDtos.add(new UserSearchAdminDto(user, getUserStatusMessage(user)));
        return new UserSearchAdminResponseDto(userSearchAdminDtos, userPage.getTotalPages(), userPage.getNumber() + 1);
    }

    /* 문자열을 포함하는 intraId를 가진 유저 찾기 */
    @Transactional
    public UserSearchAdminResponseDto findByPartsOfIntraId(String intraId, Pageable pageable) {
        Page<User> userPage = userAdminRepository.findByIntraIdContains(pageable, intraId);
        List<UserSearchAdminDto> userSearchAdminDtos = new ArrayList<UserSearchAdminDto>();
        for (User user : userPage.getContent())
            userSearchAdminDtos.add(new UserSearchAdminDto(user, getUserStatusMessage(user)));
        return new UserSearchAdminResponseDto(userSearchAdminDtos, userPage.getTotalPages(), userPage.getNumber() + 1);
    }

    private String getUserStatusMessage(User targetUser) {
        Season currentSeason = seasonAdminRepository.findCurrentSeason(LocalDateTime.now())
                .orElseThrow(() -> new NoSuchElementException("현재 시즌이 없습니다."));
        String hashKey = RedisKeyManager.getHashKey(currentSeason.getId());
        RankRedis userRank = rankRedisRepository.findRankByUserId(hashKey, targetUser.getId());
        if (userRank == null)
            return "";
        else
            return userRank.getStatusMessage();
    }
}
