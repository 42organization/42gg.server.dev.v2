package com.gg.server.domain.rank.redis;

import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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
    private String tierImageUrl;
    private String textColor;

    public void updateRank(int changePpp, int wins, int losses) {
        this.ppp += changePpp;
        this.wins = wins;
        this.losses = losses;
    }

    public void updateTierImage(String tierImageUrl) {
        this.tierImageUrl = tierImageUrl;
    }

    public void changedRank(int ppp, int wins, int losses) {
        this.ppp = ppp;
        this.wins = wins;
        this.losses = losses;
    }

    public void setStatusMessage(String msg) {
        this.statusMessage = msg;
    }

    public static RankRedis from(UserDto user, Integer ppp, String tierImageUrl) {
        RankRedis rankRedis = RankRedis.builder()
                .userId(user.getId())
                .intraId(user.getIntraId())
                .ppp(ppp)
                .wins(0)
                .losses(0)
                .statusMessage("")
                .tierImageUrl(tierImageUrl)
                .textColor(user.getTextColor())
                .build();
        return rankRedis;
    }

    public static RankRedis from(Rank rank) {
        RankRedis rankRedis = RankRedis.builder()
                .userId(rank.getUser().getId())
                .intraId(rank.getUser().getIntraId())
                .ppp(rank.getPpp())
                .wins(rank.getWins())
                .losses(rank.getLosses())
                .statusMessage(rank.getStatusMessage())
                .tierImageUrl(rank.getTier().getImageUri())
                .textColor(rank.getUser().getTextColor())
                .build();
        return rankRedis;
    }

    @Override
    public String toString() {
        return "RankRedis{" +
                "userId=" + userId +
                ", intraId='" + intraId + '\'' +
                ", ppp=" + ppp +
                ", wins=" + wins +
                ", losses=" + losses +
                ", statusMessage='" + statusMessage + '\'' +
                ", tierImageUrl='" + tierImageUrl + '\'' +
                ", textColor='" + textColor + '\'' +
                '}';
    }
}
