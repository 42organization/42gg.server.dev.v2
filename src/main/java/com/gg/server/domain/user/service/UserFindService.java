package com.gg.server.domain.user.service;

import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFindService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
    }

    @Transactional(readOnly = true)
    public User findByIntraId(String intraId){
        return userRepository.findByIntraId(intraId).orElseThrow(() -> new UserNotFoundException());
    }
}
