package com.gg.server.admin.tournament.dto;

import com.gg.server.domain.tournament.type.TournamentType;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Getter
@AllArgsConstructor
public class TournamentAdminUpdateRequestDto {
    @NotNull(message = "제목이 필요합니다.")
    private String title;

    @NotNull(message = "내용이 필요합니다.")
    private String contents;

    @NotNull(message = "시작 시간이 필요합니다.")
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private LocalDateTime startTime;

    @NotNull(message = "종료 시간이 필요합니다.")
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private LocalDateTime endTime;

    @NotNull(message = "토너먼트 종류가 필요합니다.")
    @Enumerated(EnumType.STRING)
    private TournamentType type;
}
