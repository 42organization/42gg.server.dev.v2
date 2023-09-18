package com.gg.server.admin.coin.service;

import com.gg.server.admin.coin.dto.CoinUpdateRequestDto;
import com.gg.server.domain.coin.data.CoinHistory;
import com.gg.server.domain.coin.service.CoinHistoryService;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoinAdminService {
    private final UserRepository userRepository;
    private final CoinHistoryService coinHistoryService;

    @Transactional
    public void updateUserCoin(CoinUpdateRequestDto coinUpdateRequestDto) {
        User user = userRepository.findByIntraId(coinUpdateRequestDto.getIntraId()).orElseThrow(() -> new UserNotFoundException());
        user.addGgCoin(coinUpdateRequestDto.getChange());
        coinHistoryService.addCoinHistory(new CoinHistory(user, coinUpdateRequestDto.getContent(), coinUpdateRequestDto.getChange()));
    }
}
