package com.gg.server.admin.season.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SeasonCreateRequestDto {
    @NotNull(message = "plz. seasonName")
    private String seasonName;

    @NotNull(message = "plz. startTime")
    @Future(message = "불가능한 예약시점입니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalDateTime startTime;

    @NotNull(message = "plz. startPpp")
    private Integer startPpp;

    @NotNull(message = "plz. pppGap")
    private Integer pppGap;

    public String toString() {
        return "SeasonCreateRequestAdminDto{" + '\'' +
                "seasonName=" + seasonName + '\'' +
                ", startTime=" + startTime +
                ", startPpp='" + startPpp + '\'' +
                ", pppGap='" + pppGap + '\'' +
                '}';
    }
}
