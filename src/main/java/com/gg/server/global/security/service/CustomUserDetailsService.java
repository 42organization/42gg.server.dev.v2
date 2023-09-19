package com.gg.server.global.security.service;

import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.domain.user.service.UserFindService;
import com.gg.server.global.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final UserFindService userFindService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        User user = userFindService.findByIntraId(username);
        return UserPrincipal.create(user);
    }

    public UserDetails loadUserById(Long id) {
        User user = userFindService.findUserById(id);
        return UserPrincipal.create(user);
    }
}
