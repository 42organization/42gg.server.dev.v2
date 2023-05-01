package com.gg.server.domain.game;

import com.gg.server.domain.season.Season;
import com.gg.server.global.types.Mode;
import com.gg.server.global.types.StatusType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", insertable = false, updatable = false)
    private Season season;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private StatusType status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "mode")
    private Mode mode;

    @NotNull
    @Column(name = "start_time", insertable = false, updatable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;
}
