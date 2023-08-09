package com.gg.server.domain.user.service;

import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserCoinResponseDto;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserCoinService {
    private final UserRepository userRepository;

    public UserCoinResponseDto getUserCoin(String intraId) {
        int userCoin = userRepository.findByIntraId(intraId).orElseThrow(() -> new UserNotFoundException()).getGgCoin();

        return new UserCoinResponseDto(userCoin);
    }
}
