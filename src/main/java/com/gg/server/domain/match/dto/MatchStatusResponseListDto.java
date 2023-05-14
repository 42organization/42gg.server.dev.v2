package com.gg.server.domain.match.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class MatchStatusResponseListDto {
    private List<MatchStatusDto> match;
}
