package com.gg.server.domain.user.dto;

import com.gg.server.domain.coin.data.CoinHistory;
import com.gg.server.domain.coin.data.CoinHistoryRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CoinHistoryResponseDto {
    private  String history;
    private int amount;
    private LocalDateTime createdAt;

    public CoinHistoryResponseDto(CoinHistory coinHistory) {
        this.history = coinHistory.getHistory();
        this.amount = coinHistory.getAmount();
        this.createdAt = coinHistory.getCreatedAt();
    }
}
