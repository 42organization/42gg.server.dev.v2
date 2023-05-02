package com.gg.server.domain.user.service;

import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.global.security.jwt.exception.TokenNotValidException;
import com.gg.server.global.security.jwt.repository.JwtRedisRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtRedisRepository jwtRedisRepository;
    private final AuthTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public String regenerate(String refreshToken) {
        Long userId = jwtRedisRepository.getUserIdByRefToken(refreshToken);
        if (tokenProvider.getTokenClaims(refreshToken) == null)
            throw new TokenNotValidException();
        return tokenProvider.createToken(userId);
    }

    /**
     * @param intraId
     * @return intraId가 포함된 유저들의 intraId를 페이징 관계없이 최대 5개까지 검색하여 List로 return
     */
    @Transactional(readOnly = true)
    public List<String> findByPartofIntraId(String intraId) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("intraId").ascending());
        Page<User> pageUsers = userRepository.findByIntraIdContains(pageable, intraId);
        return pageUsers.getContent().stream().map(user -> user.getIntraId())
                .collect(Collectors.toList());
    }
}
