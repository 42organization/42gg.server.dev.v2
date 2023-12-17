package com.gg.server.domain.tournament.data;

import static com.gg.server.global.exception.ErrorCode.*;
import static com.gg.server.global.exception.ErrorCode.TOURNAMENT_GAME_EXCEED;
import static com.gg.server.global.utils.BusinessChecker.mustContains;
import static com.gg.server.global.utils.BusinessChecker.mustNotContains;
import static com.gg.server.global.utils.BusinessChecker.mustNotExceed;
import static com.gg.server.global.utils.BusinessChecker.mustNotNull;

import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.domain.user.data.User;
import com.gg.server.global.utils.BaseTimeEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@ToString
public class Tournament extends BaseTimeEntity {
    // 토너먼트 참가자 수 => 현재는 8강 고정
    public static final int ALLOWED_JOINED_NUMBER = 8;
    // 토너먼트 최소 시작 날짜 (n일 후)
    public static final int ALLOWED_MINIMAL_START_DAYS = 2;
    // 토너먼트 최소 진행 시간 (n시간)
    public static final int MINIMUM_TOURNAMENT_DURATION = 2;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "title")
    private String title;

    @NotNull
    @Column(name = "contents")
    private String contents;

    @NotNull
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TournamentType type;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<TournamentGame> tournamentGames = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<TournamentUser> tournamentUsers = new ArrayList<>();

    /**
     * winner는 생성시점에 존재하지 않음.
     */
    @Builder
    public Tournament(String title, String contents, LocalDateTime startTime, LocalDateTime endTime,
        TournamentType type, TournamentStatus status) {
        this.title = title;
        this.contents = contents;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.status = status;
        this.winner = null;
    }

    public void update(String title, String contents, LocalDateTime startTime,
        LocalDateTime endTime, TournamentType type, TournamentStatus status) {
        this.title = title;
        this.contents = contents;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.status = status;
    }

    /**
     * TournamentGame 에서 호출하는 연관관계 편의 메서드, 기타 호출 금지.
     */
    protected void addTournamentGame(TournamentGame tournamentGame) {
        mustNotNull(tournamentGame, NULL_POINT);
        mustNotExceed(ALLOWED_JOINED_NUMBER - 2, tournamentGames, TOURNAMENT_GAME_EXCEED);
        mustNotContains(tournamentGame, tournamentGames, TOURNAMENT_GAME_DUPLICATION);
        this.tournamentGames.add(tournamentGame);
    }

    /**
     * TournamentUser 에서 호출하는 연관관계 편의 메서드, 기타 호출 금지.
     */
    protected void addTournamentUser(@NotNull TournamentUser tournamentUser) {
        mustNotNull(tournamentUser, NULL_POINT);
        mustNotContains(tournamentUser, tournamentUsers, TOURNAMENT_USER_DUPLICATION);
        this.tournamentUsers.add(tournamentUser);
    }

    /**
     * TournamentGame 에서 호출하는 연관관계 편의 메서드, 기타 호출 금지.
     */
    protected void deleteTournamentUser(TournamentUser tournamentUser) {
        mustNotNull(tournamentUser, NULL_POINT);
        mustContains(tournamentUser, tournamentUsers, TOURNAMENT_USER_NOT_FOUND);
        this.tournamentUsers.remove(tournamentUser);
    }

    public void updateWinner(User winner) {
        mustNotNull(winner, NULL_POINT);
        this.winner = winner;
    }

    public void updateStatus(TournamentStatus status) {
        mustNotNull(status, NULL_POINT);
        this.status = status;
    }
}
