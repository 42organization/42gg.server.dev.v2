package com.gg.server.domain.coin.service;

import com.gg.server.domain.coin.data.CoinHistoryRepository;
import com.gg.server.domain.coin.data.CoinPolicyRepository;
import com.gg.server.domain.coin.dto.UserGameCoinResultDto;
import com.gg.server.domain.coin.type.HistoryType;
import com.gg.server.domain.game.service.GameFindService;
import com.gg.server.domain.item.data.Item;
import com.gg.server.domain.item.exception.InsufficientGgcoinException;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.exception.UserAlreadyAttendanceException;
import com.gg.server.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCoinChangeService {
    private final CoinPolicyRepository coinPolicyRepository;
    private final CoinHistoryService coinHistoryService;
    private final UserRepository userRepository;
    private final GameFindService gameFindService;

    @Transactional
    public int addAttendanceCoin(User user){
        if (coinHistoryService.hasAttendedToday(user))
            throw new UserAlreadyAttendanceException();
        int coinIncrement = coinPolicyRepository.findTopByOrderByCreatedAtDesc().getAttendance();
        user.addGgCoin(coinIncrement);
        coinHistoryService.addAttendanceCoinHistory(user);
        return coinIncrement;
    }

    @Transactional
    public void purchaseItemCoin(Item item, Integer price, Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        user.payGgCoin(price);

        coinHistoryService.addPurchaseItemCoinHistory(user, item, price);
    }


    @Transactional
    public UserGameCoinResultDto addNormalGameCoin(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
        int coinIncrement = coinPolicyRepository.findTopByOrderByCreatedAtDesc().getNormal();

        user.addGgCoin(coinIncrement);
        coinHistoryService.addNormalCoin(user);
        return new UserGameCoinResultDto(user.getGgCoin(), coinIncrement);
    }

    @Transactional
    public UserGameCoinResultDto addRankGameCoin(Long gameId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
        int coinIncrement;

        if (userIsWinner(gameId, user))
            coinIncrement = coinHistoryService.addRankWinCoin(user);
        else
            coinIncrement = coinHistoryService.addRankLoseCoin(user);

        user.addGgCoin(coinIncrement);
        return new UserGameCoinResultDto(user.getGgCoin(), coinIncrement);
    }

    private boolean userIsWinner(Long gameId, User user) {
        List<Team> teams = gameFindService.findByGameId(gameId).getTeams();

        for(Team team: teams) {
            for (TeamUser teamUser : team.getTeamUsers()){
                if (teamUser.getUser().getId() == user.getId() && team.getWin() == true)
                    return true;
                else if (teamUser.getUser().getId() == user.getId() && team.getWin() == false)
                    return false;
            }
        }

        return false;
    }
}
