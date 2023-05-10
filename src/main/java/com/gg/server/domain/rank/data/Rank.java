package com.gg.server.domain.rank.data;

import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.user.User;
import com.gg.server.global.utils.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
@Table(name="ranks")
public class Rank extends BaseTimeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    private Season season;

    @NotNull
    @Column(name = "ppp")
    private Integer ppp;

    @NotNull
    @Column(name = "wins")
    private Integer wins;

    @NotNull
    @Column(name = "losses")
    private Integer losses;

    @Column(name = "status_message", length = 30)
    private String statusMessage;

    @Builder
    public Rank(User user, Season season, Integer ppp, Integer ranking, Integer wins,
                Integer losses, String statusMessage) {
        this.user = user;
        this.season = season;
        this.ppp = ppp;
        this.wins = wins;
        this.losses = losses;
        this.statusMessage = statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
