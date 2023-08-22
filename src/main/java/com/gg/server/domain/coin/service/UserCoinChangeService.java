package com.gg.server.domain.coin.service;

import com.gg.server.domain.coin.data.CoinPolicyRepository;
import com.gg.server.domain.coin.dto.UserGameCoinResultDto;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCoinChangeService {
    private final CoinPolicyRepository coinPolicyRepository;
    private final CoinHistoryService coinHistoryService;
    private final UserRepository userRepository;

    @Transactional
    public UserGameCoinResultDto addNormalGameCoin(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
        int coinIncrement = coinPolicyRepository.findTopByOrderByCreatedAtDesc().getNormal();

        user.addGgCoin(coinIncrement);
        coinHistoryService.addNormalCoin(user);
        return new UserGameCoinResultDto(user.getGgCoin(), coinIncrement);
    }
}
