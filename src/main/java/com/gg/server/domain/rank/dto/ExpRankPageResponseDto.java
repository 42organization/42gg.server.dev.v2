package com.gg.server.domain.rank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@AllArgsConstructor
@Getter
public class ExpRankPageResponseDto {
    private int myRank;
    private int currentPage;
    private int totalPage;
    private List<ExpRankDto> rankList;
}
