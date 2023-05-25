package com.gg.server.admin.user.service;
import com.gg.server.admin.season.data.SeasonAdminRepository;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.admin.user.dto.UserDetailAdminResponseDto;
import com.gg.server.admin.user.dto.UserSearchAdminDto;
import com.gg.server.admin.user.dto.UserSearchAdminRequestDto;
import com.gg.server.admin.user.dto.UserSearchAdminResponseDto;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.user.User;
import lombok.AllArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
        return new UserSearchAdminResponseDto(userSearchAdminDtos, userPage.getTotalPages());
    }

    @Transactional(readOnly = true)
    public UserSearchAdminResponseDto searchByIntraId(Pageable pageable, String intraId) {
        Page<User> userPage = userAdminRepository.findByIntraId(pageable, intraId);
        List<UserSearchAdminDto> userSearchAdminDtos = new ArrayList<UserSearchAdminDto>();
        for (User user : userPage.getContent())
            userSearchAdminDtos.add(new UserSearchAdminDto(user, getUserStatusMessage(user)));
        return new UserSearchAdminResponseDto(userSearchAdminDtos, userPage.getTotalPages());
    }

    /* 문자열을 포함하는 intraId를 가진 유저 찾기 */
    @Transactional(readOnly = true)
    public UserSearchAdminResponseDto findByPartsOfIntraId(String intraId, Pageable pageable) {
        Page<User> userPage = userAdminRepository.findByIntraIdContains(pageable, intraId);
        List<UserSearchAdminDto> userSearchAdminDtos = new ArrayList<UserSearchAdminDto>();
        for (User user : userPage.getContent())
            userSearchAdminDtos.add(new UserSearchAdminDto(user, getUserStatusMessage(user)));
        return new UserSearchAdminResponseDto(userSearchAdminDtos, userPage.getTotalPages());
    }

    @Transactional(readOnly = true)
    public UserDetailAdminResponseDto getUserDetailByIntraId(String intraId) {
        User user = userAdminRepository.findByIntraId(intraId).orElseThrow(() -> new NotFoundException("못찾음"));//에러코드 수정 필요
        Season currSeason = seasonAdminRepository.findCurrentSeason(LocalDateTime.now()).orElseThrow(() -> new NotFoundException("못찾음"));// 에러코드 수정 필요
        RankRedis userCurrRank = rankRedisRepository.findRankByUserId(RedisKeyManager.getHashKey(currSeason.getId()),
                user.getId());
        return new UserDetailAdminResponseDto(user, userCurrRank);
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
