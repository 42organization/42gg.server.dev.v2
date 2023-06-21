package com.gg.server.domain.match.utils;

import com.gg.server.domain.match.data.RedisMatchUser;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.match.type.SlotStatus;
import java.util.List;
import java.util.Optional;

public class MatchCalculator {
    private final Integer pppGap;
    private final RedisMatchUser matchUser;


    public MatchCalculator(Integer pppGap, RedisMatchUser matchUser) {
        this.pppGap = pppGap;
        this.matchUser = matchUser;
    }

    /**유저들이 입장할 때마다 Queue 매칭 검사
     * 마지막으로 입장한 유저랑 그전 유저들만 비교하면 된다.
     * 마지막 입장 유저가 both : normal 입장 유저와 경기 가능한 rank 입장 유저 매칭
     * 마지막 입장 유저가 normal : normal user 탐색
     * 마지막 입장 유저가 rank : 경기 가능한 rank 입장 유저 매칭
     * 이때 탐색 우선  위는 먼저 들어온 사람부터**/
    public Optional<RedisMatchUser> findEnemy(List<RedisMatchUser> allMatchUsers) {
        if (allMatchUsers.size() == 0) {
            return Optional.empty();
        }
        if (matchUser.getOption().equals(Option.NORMAL)) {
            return allMatchUsers.stream()
                    .filter(player -> player.getOption().equals(Option.NORMAL) ||
                            player.getOption().equals(Option.BOTH))
                    .findFirst();
        }
        if (matchUser.getOption().equals(Option.RANK)) {
            return allMatchUsers.stream()
                    .filter(player -> (player.getOption().equals(Option.RANK) ||
                            player.getOption().equals(Option.BOTH))
                            && Math.abs(player.getPpp() - matchUser.getPpp()) <= pppGap)
                    .findFirst();
        }
        return allMatchUsers.stream()
                .filter(player -> player.getOption().equals(Option.NORMAL) ||
                        player.getOption().equals(Option.BOTH) ||
                        (player.getOption().equals(Option.RANK) &&
                                Math.abs(player.getPpp() - matchUser.getPpp())<= pppGap))
                .findFirst();
    }

    /**
     *
     * @param allMatchUsers : 큐에 들어있는 플레이어들
     * @return 1) 매칭가능한 상대가 있는 경우: SlotStatus.MATCH
     * 2) 매칭가능한 상대가 없는 경우: SlotStatus.OPEN
     */
    public SlotStatus findEnemyStatus(List<RedisMatchUser> allMatchUsers)
    {
        if (matchUser.getOption().equals(Option.NORMAL)) {
            return getNormalSlotStatus(allMatchUsers);
        }
        if (matchUser.getOption().equals(Option.RANK)) {
            return getRankSlotStatus(allMatchUsers);
        }
        return getBothSlotStatus(allMatchUsers);
    }

    private SlotStatus getRankSlotStatus(List<RedisMatchUser> allMatchUsers) {
        if (allMatchUsers.stream().anyMatch(e -> (e.getOption().equals(Option.RANK)
        || e.getOption().equals(Option.BOTH)) && (e.getPpp() - matchUser.getPpp()) <= pppGap)) {
            return SlotStatus.MATCH;
        }
        return SlotStatus.OPEN;
    }

    private SlotStatus getNormalSlotStatus(List<RedisMatchUser> allMatchUsers) {
        if (allMatchUsers.stream().anyMatch(e -> e.getOption().equals(Option.NORMAL) ||
                e.getOption().equals(Option.BOTH))) {
            return SlotStatus.MATCH;
        }
        return SlotStatus.OPEN;
    }

    private SlotStatus getBothSlotStatus(List<RedisMatchUser> allMatchUsers) {
        if (allMatchUsers.stream().anyMatch(e ->
                e.getOption().equals(Option.NORMAL) || e.getOption().equals(Option.BOTH)
                        || (e.getOption().equals(Option.RANK) &&
                        e.getPpp() - matchUser.getPpp() <= pppGap))) {
            return SlotStatus.MATCH;
        }
        return SlotStatus.OPEN;
    }
}
