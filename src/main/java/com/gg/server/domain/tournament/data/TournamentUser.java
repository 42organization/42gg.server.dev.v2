package com.gg.server.domain.tournament.data;

import com.gg.server.domain.user.data.User;
import com.gg.server.global.utils.BaseTimeEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
public class TournamentUser extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @NotNull
    @Column(name = "is_joined")
    private Boolean isJoined;

    @NotNull
    @Column(name = "register_time")
    private LocalDateTime registerTime;

    public TournamentUser(User user, Tournament tournament, boolean isJoined, LocalDateTime registerTime) {
        this.user = user;
        this.tournament = tournament;
        this.isJoined = isJoined;
        this.registerTime = registerTime;
    }

    public void updateIsJoined(boolean isJoined) {
        this.isJoined = isJoined;
    }
}
