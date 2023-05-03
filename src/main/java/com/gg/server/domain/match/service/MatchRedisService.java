package com.gg.server.domain.match.service;

import com.gg.server.domain.match.RedisMatchTime;
import com.gg.server.domain.match.RedisMatchUser;
import com.gg.server.domain.match.repository.RedisMatchTimeRepository;
import com.gg.server.domain.match.repository.RedisMatchUserRepository;
import com.gg.server.domain.match.type.MatchKey;
import com.gg.server.domain.match.type.Option;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchRedisService {
    private final RedisMatchTimeRepository redisMatchTimeRepository;
    private final RedisMatchUserRepository redisMatchUserRepository;

    public void makeMatch(String nickname, Integer ppp, Option option, LocalDateTime startTime) {
        //key에 duedate를 넣어야 할 필요가 있음
        //now 에서 slot time을 빼는 식으로 해야하나.., data race 발생할 수도..

        //3번 이상 매치 넣을 시 예외 처리
        //
        RedisMatchTime matchTime = new RedisMatchTime(startTime);
        RedisMatchUser matchUser = new RedisMatchUser(nickname, ppp, option);
        redisMatchTimeRepository.addMatchUser(MatchKey.MATCH_TIME.getCode() + startTime.toString(), matchUser);
        redisMatchUserRepository.addMatchTime(MatchKey.MATCH_USER.getCode() + nickname, matchTime);
    }



    private boolean isFull () {
        return true;
    }
}
