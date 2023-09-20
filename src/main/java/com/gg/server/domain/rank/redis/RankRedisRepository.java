package com.gg.server.domain.rank.redis;

import com.gg.server.domain.rank.exception.RedisDataNotFoundException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class RankRedisRepository {
    private final ZSetOperations<String, Object> zSetOps;
    private final HashOperations<String, String, Object> hashOps;
    private final RedisTemplate<String, Object> redisTemplate;

    public RankRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.zSetOps = redisTemplate.opsForZSet();
        this.hashOps = redisTemplate.opsForHash();
        this.redisTemplate = redisTemplate;
    }

    /**
     *
     * @param key
     * @param userId
     * @param ppp
     *
     * ZSET에 user를 추가하는 메서드
     */
    public void addToZSet(String key, Long userId, int ppp) {
        zSetOps.add(key, userId.toString(), ppp);
    }

    /**
     *
     * @param key
     * @param userId
     * @param ppp
     *
     * ZSET에서 user의 ppp 증가시키는 메서드
     */
    public void incrementScoreInZSet(String key, Long userId, int ppp) {
        zSetOps.incrementScore(key, userId.toString(), ppp);

    }


    /**
     *
     * @param key
     * @param userId
     * @param ppp
     *
     * ZSET에서 user의 ppp를 감소시키는 메서드
     */
    public void decrementScoreInZSet(String key, Long userId, int ppp) {
        zSetOps.incrementScore(key, userId.toString(), -ppp);
    }

    /**
     *
     * @param key
     * @param userId
     *
     * ZSET에서 user를 삭제하는 메서드
     */
    public void deleteFromZSet(String key, Long userId) {
        zSetOps.remove(key, userId.toString());
    }

    /**
     *
     * @param key
     * @param userId
     * @return 0 -> 1등
     *
     *  ZSET에서 user의 순위(rank)를 조회하는 메서드 ppp가 높은순
     */
    public Long getRankInZSet(String key, Long userId) {
        Long result = zSetOps.reverseRank(key, userId.toString());
        if(result == null)
            throw new RedisDataNotFoundException();
        return result;
    }


    /**
     *
     * @param key
     * @param userId
     *
     *   ZSET에서 user의 ppp를 가져오는 메서드
     */
    public Long getScoreInZSet(String key, Long userId) {
        Double result = zSetOps.score(key, userId.toString());
        if(result == null)
            throw new RedisDataNotFoundException();
        return result.longValue();
    }

    /**
     *
     * @param key
     * @param startRank
     * @param endRank
     *
     *  Zset에서 user의 랭킹 범위를 지정하여 그 범위 내의 userId들을 가져오는 메소드
     *  startRank -> 0부터 시작
     */
    public List<Long> getUserIdsByRangeFromZSet(String key, long startRank, long endRank) {
        Set<Object> result = zSetOps.reverseRange(key, startRank, endRank);
        List<String> stringList = result.stream().map(Object::toString).collect(Collectors.toList());
        if (result == null)
            throw new RedisDataNotFoundException();
        return stringList.stream()
                .map(Long::parseLong).collect(Collectors.toList());
    }



    /**
     *
     * @param key
     * @param userId
     * @param userRank
     * redis hash에 user rank데이터를 추가하는 메소드
     */
    public void addRankData(String key, Long userId, RankRedis userRank){
        hashOps.put(key, userId.toString(), userRank);
    }

    /**
     *
     * @param key
     * @param userId
     *
     * 해당 유저의 rank데이터를 조회하는 메소드
     */
    public RankRedis findRankByUserId(String key, Long userId) {
        Object result = hashOps.get(key, userId.toString());
        if (result == null)
            throw new RedisDataNotFoundException();
        return RankRedis.class.cast(result);
    }

    /**
     *
     * @param key
     * @param userId
     * @param userRank
     *
     * 해당 유저의 rank데이터를 업데이트하는 메소드
     */
    public void updateRankData(String key, Long userId, RankRedis userRank) {
        hashOps.put(key, userId.toString(), userRank);
    }

    /**
     *
     * @param key
     * @param userId
     *
     * 해당 유저의 rank데이터를 삭제하는 메소드
     */
    public void deleteRankData(String key, Long userId) {
        hashOps.delete(key, userId.toString());
    }


    /**
     *
     * @param key
     * @param userIds
     *
     * 해당 유저들의 rank데이터를 조회하는 메소드
     */
    public List<RankRedis> findRanksByUserIds(String key, List<Long> userIds) {
        List<String> userIdsStr = userIds.stream().map(String::valueOf).collect(Collectors.toList());
        List<Object> objects = hashOps.multiGet(key, userIdsStr);
        if(objects == null)
            throw new RedisDataNotFoundException();
        return objects.stream().map(RankRedis.class::cast).collect(Collectors.toList());
    }

    public List<RankRedis> findAllRanksOrderByPppDesc(String key) {
        List<Object> objects = hashOps.values(key);
        if(objects == null)
            throw new RedisDataNotFoundException();
        return objects.stream().map(RankRedis.class::cast).sorted(Comparator.comparing(RankRedis::getPpp).reversed()).collect(Collectors.toList());
    }

    public Long countTotalRank(String zSetKey) {
        Long result = zSetOps.size(zSetKey);
        if(result == null)
            throw new RedisDataNotFoundException();
        return result;
    }

    public void deleteAll(){
        redisTemplate.delete(redisTemplate.keys("*"));
    }
    public void deleteZSetKey(String key) {
        redisTemplate.delete(key);
    }

    public void deleteHashKey(String key) {
        redisTemplate.delete(key);
    }

    public Boolean isEmpty(String hashKey) {
        return hashOps.size(hashKey) == 0;
    }
}
