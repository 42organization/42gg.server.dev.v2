package com.gg.server.domain.match.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SlotStatusResponseListDto {
    private List<SlotStatusDto> matchBoards;

    @Override
    public String toString() {
        return "MatchStatusResponseListDto{" +
                "matchBoards=" + matchBoards +
                '}';
    }
}
