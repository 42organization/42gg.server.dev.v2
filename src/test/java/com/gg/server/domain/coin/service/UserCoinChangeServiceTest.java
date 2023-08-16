package com.gg.server.domain.coin.service;

import com.gg.server.domain.coin.data.CoinHistoryRepository;
import com.gg.server.domain.coin.data.CoinPolicy;
import com.gg.server.domain.coin.data.CoinPolicyRepository;
import com.gg.server.domain.coin.dto.UserGameCoinResultDto;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
@Transactional
class UserCoinChangeServiceTest {
    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    AuthTokenProvider tokenProvider;

    @Autowired
    CoinHistoryService coinHistoryService;

    @Autowired
    CoinHistoryRepository coinHistoryRepository;

    @Autowired
    CoinPolicyRepository coinPolicyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserCoinChangeService userCoinChangeService;

    @Test
    @DisplayName("노말 게임 재화 증가 서비스 테스트")
    void addNormalGameService() {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
        User user = userRepository.getById(userId);


        UserGameCoinResultDto userGameCoinResultDto = userCoinChangeService.addNormalGameCoin(userId);

        assertThat(user.getGgCoin()).isEqualTo(userGameCoinResultDto.getAfterCoin());
        assertThat(coinPolicyRepository.findTopByOrderByCreatedAtDesc().getNormal()).isEqualTo(userGameCoinResultDto.getCoinIncrement());
        System.out.println(coinHistoryRepository.findFirstByOrderByIdDesc().getHistory());
    }
}