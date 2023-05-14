package com.gg.server.domain.match.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MatchStatusResponseListDto {
    private List<MatchStatusDto> match;

    @Override
    public String toString() {
        return "MatchStatusResponseListDto{" +
                "match=" + match +
                '}';
    }
}
