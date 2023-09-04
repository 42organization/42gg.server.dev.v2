package com.gg.server.domain.rank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RankPageResponseDto {
    private int myRank;
    private int currentPage;
    private int totalPage;
    private List<RankDto> rankList;
}
