package com.gg.server.domain.rank.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("rank")
@Getter
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
}
