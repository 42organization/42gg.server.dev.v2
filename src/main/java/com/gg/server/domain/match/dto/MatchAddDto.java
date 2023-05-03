package com.gg.server.domain.match.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@AllArgsConstructor
public class MatchAddDto {
//    @JsonProperty("mode")
//    private String option;
    String mode;
    String temp;
//    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")//협의 필요
//    private LocalDateTime startTime;
}
