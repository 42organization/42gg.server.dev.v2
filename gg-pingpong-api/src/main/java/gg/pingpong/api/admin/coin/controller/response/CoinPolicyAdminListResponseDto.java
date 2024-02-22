package gg.pingpong.api.admin.coin.controller.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CoinPolicyAdminListResponseDto {
	private List<CoinPolicyAdminResponseDto> coinPolicyList;
	private int totalPage;
}
