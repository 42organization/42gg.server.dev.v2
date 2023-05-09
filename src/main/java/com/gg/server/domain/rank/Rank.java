package com.gg.server.domain.rank;

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
    @Column(name = "season_id")
    private Long seasonId;

    @NotNull
    @Column(name = "ppp")
    private Integer ppp;

    @Column(name = "ranking")
    private Integer ranking;

    @NotNull
    @Column(name = "wins")
    private Integer wins;

    @NotNull
    @Column(name = "losses")
    private Integer losses;

    @Column(name = "status_message", length = 30)
    private String statusMessage;

    public static Rank from (User user, Long seasonId, Integer ppp){
        return Rank.builder()
                .user(user)
                .ppp(ppp)
                .seasonId(seasonId)
                .ranking(-1)
                .wins(0)
                .losses(0)
                .statusMessage("")
                .build();
    }
}
