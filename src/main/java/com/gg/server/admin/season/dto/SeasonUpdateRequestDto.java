package com.gg.server.admin.season.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Getter
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

    @Override
    public String toString() {
        return "SeasonCreateRequestAdminDto{" + '\'' +
                "seasonName=" + seasonName + '\'' +
                ", startTime=" + startTime +
                ", startPpp='" + startPpp + '\'' +
                ", pppGap='" + pppGap + '\'' +
                '}';
    }
}
