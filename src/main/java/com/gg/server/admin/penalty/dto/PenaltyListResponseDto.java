package com.gg.server.admin.penalty.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PenaltyListResponseDto {
    List<PenaltyUserResponseDto> penaltyList;
    Integer currentPage;
    Integer totalPage;
}
