package com.gg.server.domain.rank.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RankPageResponseDto {
	private int myRank;
	private int currentPage;
	private int totalPage;
	private List<RankDto> rankList;
}
