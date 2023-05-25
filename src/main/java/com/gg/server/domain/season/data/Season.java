package com.gg.server.domain.season.data;

import com.gg.server.admin.season.dto.SeasonCreateRequestDto;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotNull
    @Column(name = "season_name", length = 20)
    private String seasonName;

    @Setter
    @NotNull
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Setter
    @NotNull
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Setter
    @NotNull
    @Column(name = "start_ppp")
    private Integer startPpp;

    @Setter
    @NotNull
    @Column(name = "ppp_gap")
    private Integer pppGap;

    @Builder
    public Season(String seasonName, LocalDateTime startTime, LocalDateTime endTime, Integer startPpp, Integer pppGap) {
        this.seasonName = seasonName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startPpp = startPpp;
        this.pppGap = pppGap;
    }

    @Builder
    public Season(SeasonCreateRequestDto createDto) {
        this.seasonName = createDto.getSeasonName();
        this.startTime = createDto.getStartTime();
        this.startPpp = createDto.getStartPpp();
        this.pppGap = createDto.getPppGap();
    }
}
