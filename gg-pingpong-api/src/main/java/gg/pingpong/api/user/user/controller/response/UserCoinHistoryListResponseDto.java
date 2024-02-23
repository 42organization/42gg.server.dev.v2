package gg.pingpong.api.user.user.controller.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserCoinHistoryListResponseDto {
	private List<CoinHistoryResponseDto> useCoinList;
	private int totalPage;
}
