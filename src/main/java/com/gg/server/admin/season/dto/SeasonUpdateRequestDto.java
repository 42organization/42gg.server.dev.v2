package com.gg.server.admin.season.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeasonUpdateRequestDto {
    @NotNull
    private String seasonName;
    @NotNull
    @Future
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalDateTime startTime;
    @NotNull
    private Integer startPpp;
    @NotNull
    private Integer pppGap;

}
