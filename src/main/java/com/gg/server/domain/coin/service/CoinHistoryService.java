package com.gg.server.domain.coin.service;

import com.gg.server.domain.coin.data.CoinHistory;
import com.gg.server.domain.coin.data.CoinHistoryRepository;
import com.gg.server.domain.coin.data.CoinPolicyRepository;
import com.gg.server.domain.coin.type.HistoryType;
import com.gg.server.domain.item.data.Item;
import com.gg.server.domain.user.data.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CoinHistoryService {
    private final CoinHistoryRepository coinHistoryRepository;
    private final CoinPolicyRepository coinPolicyRepository;

    @Transactional
    public void addAttendanceCoinHistory(User user) {
        int amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc().getAttendance();
        addCoinHistory(new CoinHistory(user, HistoryType.ATTENDANCECOIN.getHistory(), amount));
    }

    @Transactional
    public void addPurchaseItemCoinHistory(User user, Item item, Integer price) {
        addCoinHistory(new CoinHistory(user, item.getName()+ " 구매", price*(-1)));
    }

    @Transactional
    public void addGiftItemCoinHistory(User user, User giftTarget, Item item, Integer price) {
        addCoinHistory(new CoinHistory(user, giftTarget.getIntraId() + "에게 " + item.getName()+ " 선물", price*(-1)));
    }

    @Transactional
    public void addNormalCoin(User user){
        int amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc().getNormal();
        addCoinHistory(new CoinHistory(user, HistoryType.NORMAL.getHistory(), amount));
    }

    @Transactional
    public int addRankWinCoin(User user){
        int amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc().getRankWin();
        addCoinHistory(new CoinHistory(user, HistoryType.RANKWIN.getHistory(), amount));
        return amount;
    }

    @Transactional
    public int addRankLoseCoin(User user){
        int amount = coinPolicyRepository.findTopByOrderByCreatedAtDesc().getRankLose();
        if (amount == 0)
            return amount;
        addCoinHistory(new CoinHistory(user, HistoryType.RANKLOSE.getHistory(), amount));
        return amount;
    }

    @Transactional(readOnly = true)
    public boolean hasAttendedToday(User user) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return coinHistoryRepository.existsUserAttendedCheckToday(
                user, HistoryType.ATTENDANCECOIN.getHistory(), startOfDay, endOfDay);
    }

    private void addCoinHistory(CoinHistory coinHistory){
        coinHistoryRepository.save(coinHistory);
    }

}
