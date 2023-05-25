package com.gg.server.domain.rank.data;

import com.gg.server.admin.user.dto.UserUpdateAdminRequestDto;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.user.User;
import com.gg.server.global.utils.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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


    public static Rank from (User user, Season season, Integer ppp) {
        return Rank.builder()
                .user(user)
                .ppp(ppp)
                .season(season)
                .wins(0)
                .losses(0)
                .statusMessage("")
                .build();
    }

    @Builder
    public Rank(User user, Season season, Integer ppp, Integer wins,
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

    public void updatePpp(Integer changePpp) {
        this.ppp += changePpp;
    }

    public void modifyUserRank(UserUpdateAdminRequestDto userUpdateAdminRequestDto) {
        this.ppp = userUpdateAdminRequestDto.getPpp();
        this.wins = userUpdateAdminRequestDto.getWins();
        this.losses = userUpdateAdminRequestDto.getLosses();
    }
}
