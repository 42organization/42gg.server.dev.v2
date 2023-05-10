package com.gg.server.domain.rank.redis;

import com.gg.server.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;


@RedisHash("rank")
@Getter
@Builder
@AllArgsConstructor
public class RankRedis implements Serializable {

    private Long userId;
    private String intraId;
    private int ppp;
    private int wins;
    private int losses;
    private String statusMessage;

    public void updateRank(int ppp, int wins, int losses) {
        this.ppp = ppp;
        this.wins = wins;
        this.losses = losses;
    }

    public void setStatusMessage(String msg) {
        this.statusMessage = msg;
    }

    public static RankRedis from(UserDto user, Integer ppp) {
        RankRedis rankRedis = RankRedis.builder()
                .userId(user.getId())
                .intraId(user.getIntraId())
                .ppp(ppp)
                .wins(0)
                .losses(0)
                .statusMessage("")
                .build();
        return rankRedis;
    }

}
