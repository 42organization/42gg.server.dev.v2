package gg.pingpong.api.user.rank.controller.response;

import java.util.List;

import gg.pingpong.api.user.rank.dto.ExpRankDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
