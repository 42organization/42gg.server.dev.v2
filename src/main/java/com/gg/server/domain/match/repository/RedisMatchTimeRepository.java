package com.gg.server.domain.match.repository;

import com.gg.server.domain.match.RedisMatchUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisMatchTimeRepository {
    private final RedisTemplate<String, RedisMatchUser> redisTemplate;

    public void addMatchUser(String key, RedisMatchUser redisMatchUser) {
        redisTemplate.opsForList().rightPush(key, redisMatchUser);
    }
    public List<RedisMatchUser> getAllMatchUsers(String key) {

        ListOperations<String, RedisMatchUser> listOperations = redisTemplate.opsForList();
        Long size = listOperations.size(key);
        return listOperations.range(key, 0, size - 1);
        //0, -1 idx 확인
    }

    public void deleteMatchTime(String matchTime) {//매칭이 되거나 시간이 지나면 key를 지워준다.
        redisTemplate.delete(matchTime);
    }
}
