package gg.pingpong.api.user.rank.controller.response;

import java.util.List;

import gg.pingpong.api.user.rank.dto.RankDto;
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
