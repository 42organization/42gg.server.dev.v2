package com.gg.server.domain.tournament.data;

import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.domain.user.data.User;
import com.gg.server.global.utils.BaseTimeEntity;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
public class Tournament extends BaseTimeEntity {
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

    @OneToMany(mappedBy = "tournament")
    private List<TournamentGame> tournamentGames;

    @OneToMany(mappedBy = "tournament")
    private List<TournamentUser> tournamentUsers;

    @Builder
    public Tournament(String title, String contents, LocalDateTime startTime, LocalDateTime endTime, TournamentType type, TournamentStatus status) {
        this.title = title;
        this.contents = contents;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.status = status;
    }

    static public Tournament of(String title, String contents, LocalDateTime startTime, LocalDateTime endTime, TournamentType type, TournamentStatus status) {
        return Tournament.builder()
                .title(title)
                .contents(contents)
                .startTime(startTime)
                .endTime(endTime)
                .type(type)
                .status(status)
                .build();
    }

    // TODO TournamentDto 사용할 건지 고민해보기
//    static public Tournament from(TournamentDto tournamentDto) {
//        return Tournament.builder()
//                .title(tournamentDto.getTitle())
//                .contents(tournamentDto.getContents())
//                .startTime(tournamentDto.getStartTime())
//                .endTime(tournamentDto.getEndTime())
//                .type(tournamentDto.getType())
//                .status(tournamentDto.getStatus())
//                .build();
//    }
}
