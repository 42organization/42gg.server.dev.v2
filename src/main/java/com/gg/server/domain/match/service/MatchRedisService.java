package com.gg.server.domain.match.service;

import com.gg.server.domain.match.data.RedisMatchTime;
import com.gg.server.domain.match.data.RedisMatchUser;
import com.gg.server.domain.match.data.RedisMatchTimeRepository;
import com.gg.server.domain.match.data.RedisMatchUserRepository;
import com.gg.server.domain.match.type.Option;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchRedisService {
    private final RedisMatchTimeRepository redisMatchTimeRepository;
    private final RedisMatchUserRepository redisMatchUserRepository;

    public Long countUserMatch(String nickName) {
        return redisMatchUserRepository.countMatchTime(nickName);
    }
    public Boolean isUserMatch(String nickName, LocalDateTime startTime) {
        RedisMatchTime matchTime = new RedisMatchTime(startTime);
        if (redisMatchUserRepository.getMatchUserOrder(nickName, matchTime) != null) {
            return true;
        }
        return false;
    }
    public void makeMatch(String nickname, Integer ppp, Option option, LocalDateTime startTime) {
        RedisMatchTime matchTime = new RedisMatchTime(startTime);
        RedisMatchUser matchUser = new RedisMatchUser(nickname, ppp, option);

        //유저 이미 큐에 등록 시 예외 처리
        if (redisMatchUserRepository.getMatchUserOrder(nickname, matchTime) != null) {
            return;
        }
        //3번 이상 매치 넣을 시 예외 처리
        if (redisMatchUserRepository.countMatchTime(nickname) >= 3) {
            return;
        }
        redisMatchTimeRepository.addMatchUser(startTime.toString(), matchUser);
        redisMatchTimeRepository.setMatchTimeWithExpiry(startTime);
        redisMatchUserRepository.addMatchTime(nickname, matchTime);
    }

    public void cancelMatch(String nickName, LocalDateTime startTime) {
        //취소 패널티는 게임이 만들어진 후 고려
        RedisMatchTime matchTime = new RedisMatchTime(startTime);
        redisMatchUserRepository.deleteMatchTime(nickName, matchTime);
        List<RedisMatchUser> allMatchUsers = redisMatchTimeRepository.getAllMatchUsers(startTime.toString());
        for (RedisMatchUser matchUser : allMatchUsers) {
            if (matchUser.getNickName().equals(nickName)) {
                redisMatchTimeRepository.deleteMatchUser(startTime.toString(), matchUser);
                break ;
            }
        }
    }
}
