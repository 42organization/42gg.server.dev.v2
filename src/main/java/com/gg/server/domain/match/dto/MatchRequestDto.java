package com.gg.server.domain.match.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gg.server.domain.match.type.Option;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchRequestDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @NotNull
    @JsonProperty("mode")
    private Option option;

    public MatchRequestDto(LocalDateTime startTime, Option option) {
        this.startTime = startTime;
        this.option = option;
    }
}
