package com.gg.server.domain.rank.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpRankPageResponseDto {
    private int myRank;
    private int currentPage;
    private int totalPage;
    private List<ExpRankDto> rankList;
}
