package com.gg.server.domain.match.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchStatusResponseListDto {
    private List<MatchStatusResponseDto> matchBoards;
}
