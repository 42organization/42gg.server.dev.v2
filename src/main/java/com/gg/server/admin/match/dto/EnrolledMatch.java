package com.gg.server.admin.match.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrolledMatch {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isMatched;
    private List<MatchUser> players;

    public EnrolledMatch(LocalDateTime startTime, LocalDateTime endTime, Boolean isMatched, List<MatchUser> players) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.isMatched = isMatched;
        this.players = players;
    }
}
