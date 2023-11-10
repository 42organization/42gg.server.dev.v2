package com.gg.server.admin.tournament.dto;

import com.gg.server.domain.tournament.type.TournamentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TournamentCreateRequestDto {
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
    @NotNull
    private String title;
    @NotNull
    private String contents;
    @NotNull
    private TournamentType type;
}
