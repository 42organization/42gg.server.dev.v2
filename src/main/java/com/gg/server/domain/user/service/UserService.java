package com.gg.server.domain.user.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.dto.UserLiveResponseDto;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtRedisRepository jwtRedisRepository;
    private final AuthTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final NotiRepository notiRepository;
    private final GameRepository gameRepository;

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

    /**
     *
     * @param user
     * - event:
     *     - null → 로그인 유저가 잡힌 매칭이 하나도 없을 때
     *     - match → 매칭은 되었으나 게임시작 전일 때
     *     - game → 유저가 게임이 잡혔고 현재 게임중인 경우
     *
     * - currentMatchMode
     *     - normal
     *     - rank
     *     - null -> 매칭이 안잡혔을 때
     */
    @Transactional(readOnly = true)
    public UserLiveResponseDto getUserLiveDetail(UserDto user) {
        int notiCnt = notiRepository.countNotCheckedNotiByUser(user.getId());
        String event = null;
        String currentMatchMode = null;
        Optional<Game> optionalGame = gameRepository.getLatestGameByUser(user.getId());
        if (!optionalGame.isEmpty()) {
            Game latestGame = optionalGame.get();
            if (latestGame.getStatus() == StatusType.END)
                return new UserLiveResponseDto(notiCnt, null, null);
            event = (latestGame.getStatus() == StatusType.BEFORE) ? "match" : "game";
            currentMatchMode = latestGame.getMode().getCode();
        }
        return new UserLiveResponseDto(notiCnt, event, currentMatchMode);
    }
}
