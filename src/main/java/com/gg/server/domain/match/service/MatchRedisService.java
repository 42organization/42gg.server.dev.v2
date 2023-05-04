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
@Transactional
@RequiredArgsConstructor
public class MatchRedisService {
    private final RedisMatchTimeRepository redisMatchTimeRepository;
    private final RedisMatchUserRepository redisMatchUserRepository;

    public void makeMatch(String nickname, Integer ppp, Option option, LocalDateTime startTime) {
        //key에 due date를 넣어야 할 필요가 있음
        //now 에서 slot time을 빼는 식으로 해야하나.., data race 발생할 수도..
        //3번 이상 매치 넣을 시 예외 처리
        //user가 같은 시간대 슬롯 등록 방지 필요
        //시간 같은 거 있는지 확인

        RedisMatchTime matchTime = new RedisMatchTime(startTime);
        RedisMatchUser matchUser = new RedisMatchUser(nickname, ppp, option);

        //유저 이미 큐에 등록 시 예외 처리
        if (redisMatchUserRepository.getMatchUserOrder(nickname, matchTime) != null) {
            return;
//            throw new IllegalArgumentException("[Forbidden] User already enrolled in  queue");
        }
        //3번 이상 매치 넣을 시 예외 처리
        if (redisMatchUserRepository.countMatchTime(nickname) >= 3) {
            return;
//            throw new IllegalArgumentException("[Forbidden] User already enrolled three queues");
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
