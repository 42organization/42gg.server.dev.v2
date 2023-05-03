package com.gg.server.domain.season.data;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Builder
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "season_name", length = 20)
    private String seasonName;

    @NotNull
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @NotNull
    @Column(name = "start_ppp")
    private Integer startPpp;

    @NotNull
    @Column(name = "ppp_gap")
    private Integer pppGap;

    public Season(String seasonName, LocalDateTime startTime, LocalDateTime endTime, Integer startPpp, Integer pppGap) {
        this.seasonName = seasonName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startPpp = startPpp;
        this.pppGap = pppGap;
    }
}
