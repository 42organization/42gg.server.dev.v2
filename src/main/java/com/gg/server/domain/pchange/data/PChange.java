package com.gg.server.domain.pchange.data;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.user.User;
import com.gg.server.global.utils.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class PChange extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "ppp_result")
    private  Integer pppResult;

    @NotNull
    @Column(name = "exp")
    private Integer exp;

    public PChange(Game game, User user, Integer pppResult) {
        this.game = game;
        this.user = user;
        this.pppResult = pppResult;
        this.exp = user.getTotalExp();
    }

    public void updatePPP(Integer ppp) {
        this.pppResult = ppp;
    }
}
