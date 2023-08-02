package com.gg.server.admin.coin.service;

import com.gg.server.admin.coin.data.CoinPolicyAdminRepository;
import com.gg.server.admin.coin.dto.CoinPolicyAdminAddDto;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.domain.coin.data.CoinPolicy;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CoinPolicyAdminService {
    private final CoinPolicyAdminRepository coinPolicyAdminRepository;
    private final UserAdminRepository userAdminRepository;
    @Transactional
    public void addCoinPolicy(UserDto userDto, CoinPolicyAdminAddDto addDto){
        User user = userAdminRepository.findByIntraId(userDto.getIntraId()).orElseThrow(() -> new UserNotFoundException());

        CoinPolicy coinPolicy = CoinPolicy.from(user, addDto);

        coinPolicyAdminRepository.save(coinPolicy);
    }
}
