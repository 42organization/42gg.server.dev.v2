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
    @Column(name = "status")
    private StatusType status;

    @NotNull
    @Column(name = "mode")
    private Mode mode;

    @NotNull
    @Column(name = "start_time", insertable = false, updatable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;
}
