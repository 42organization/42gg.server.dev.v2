package com.gg.server.domain.rank;

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
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @NotNull
    @Column(name = "season_id")
    private Integer seasonId;

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
}
