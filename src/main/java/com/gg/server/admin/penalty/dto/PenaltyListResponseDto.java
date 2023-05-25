package com.gg.server.admin.penalty.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyListResponseDto {
    List<PenaltyUserResponseDto> penaltyList;
    Integer totalPage;
}
