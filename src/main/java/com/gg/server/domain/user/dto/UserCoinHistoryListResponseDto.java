package com.gg.server.domain.user.dto;

import com.gg.server.admin.coin.dto.CoinPolicyAdminResponseDto;
import com.gg.server.domain.coin.data.CoinHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserCoinHistoryListResponseDto {
    private List<CoinHistoryResponseDto> coinPolicyList;
    private int totalPage;
}
