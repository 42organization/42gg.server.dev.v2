package com.gg.server.domain.coin.service;

import com.gg.server.domain.coin.data.CoinHistory;
import com.gg.server.domain.coin.data.CoinHistoryRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
@Transactional
class CoinHistoryServiceTest {
    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    AuthTokenProvider tokenProvider;

    @Autowired
    CoinHistoryService coinHistoryService;

    @Autowired
    CoinHistoryRepository coinHistoryRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("출석 재화이력 등록")
    void addAttendanceCoinHistory() {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
        User user = userRepository.getById(userId);

        int beforeCoinHistoryCount = coinHistoryRepository.findAllByUserOrderByIdDesc(user).size();

        coinHistoryService.addAttendanceCoinHistory(user);

        List<CoinHistory> afterCoinHistory = coinHistoryRepository.findAllByUserOrderByIdDesc(user);

        assertThat(beforeCoinHistoryCount).isEqualTo(afterCoinHistory.size() - 1);

        System.out.println(beforeCoinHistoryCount + " < " + afterCoinHistory.size());
        for(CoinHistory ch : afterCoinHistory){
            System.out.println("이력 : " + ch.getHistory());
            System.out.println("사용값 : " + ch.getAmount());
            System.out.println("사용자 : " + ch.getUser().getIntraId());
            System.out.println("생성날짜 : " + ch.getCreatedAt());
        }
    }

    @Test
    @DisplayName("일반게임 재화이력 등록")
    void addNormalCoin() {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
        User user = userRepository.getById(userId);

        int beforeCoinHistoryCount = coinHistoryRepository.findAllByUserOrderByIdDesc(user).size();

        coinHistoryService.addNormalCoin(user);

        List<CoinHistory> afterCoinHistory = coinHistoryRepository.findAllByUserOrderByIdDesc(user);

        assertThat(beforeCoinHistoryCount).isEqualTo(afterCoinHistory.size() - 1);

        System.out.println(beforeCoinHistoryCount + " < " + afterCoinHistory.size());
        for(CoinHistory ch : afterCoinHistory){
            System.out.println("이력 : " + ch.getHistory());
            System.out.println("사용값 : " + ch.getAmount());
            System.out.println("사용자 : " + ch.getUser().getIntraId());
            System.out.println("생성날짜 : " + ch.getCreatedAt());
        }


    }

    @Test
    @DisplayName("랭크게임 win 재화이력 등록")
    void addRankWinCoin() {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
        User user = userRepository.getById(userId);

        int beforeCoinHistoryCount = coinHistoryRepository.findAllByUserOrderByIdDesc(user).size();

        coinHistoryService.addRankWinCoin(user);

        List<CoinHistory> afterCoinHistory = coinHistoryRepository.findAllByUserOrderByIdDesc(user);

        assertThat(beforeCoinHistoryCount).isEqualTo(afterCoinHistory.size() - 1);

        System.out.println(beforeCoinHistoryCount + " < " + afterCoinHistory.size());
        for(CoinHistory ch : afterCoinHistory){
            System.out.println("이력 : " + ch.getHistory());
            System.out.println("사용값 : " + ch.getAmount());
            System.out.println("사용자 : " + ch.getUser().getIntraId());
            System.out.println("생성날짜 : " + ch.getCreatedAt());
        }
    }

    @Test
    @DisplayName("랭크게임 lose 재화이력 등록")
    void addRankLoseCoin() {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
        User user = userRepository.getById(userId);

        int beforeCoinHistoryCount = coinHistoryRepository.findAllByUserOrderByIdDesc(user).size();

        coinHistoryService.addRankLoseCoin(user);
        coinHistoryService.addRankLoseCoin(user);

        List<CoinHistory> afterCoinHistory = coinHistoryRepository.findAllByUserOrderByIdDesc(user);

        assertThat(beforeCoinHistoryCount).isEqualTo(afterCoinHistory.size() - 2);

        System.out.println(beforeCoinHistoryCount + " < " + afterCoinHistory.size());
        for(CoinHistory ch : afterCoinHistory){
            System.out.println("이력 : " + ch.getHistory());
            System.out.println("사용값 : " + ch.getAmount());
            System.out.println("사용자 : " + ch.getUser().getIntraId());
            System.out.println("생성날짜 : " + ch.getCreatedAt());
        }
    }
}