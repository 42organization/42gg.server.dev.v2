package com.gg.server.domain.user.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.UserDetailResponseDto;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.dto.UserLiveResponseDto;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.global.security.jwt.exception.TokenNotValidException;
import com.gg.server.global.security.jwt.repository.JwtRedisRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.global.utils.ExpLevelCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<String> findByPartOfIntraId(String intraId) {
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

    @Transactional(readOnly = true)
    public UserDetailResponseDto getUserDetail(String targetUserIntraId) {
        User targetUser = userRepository.findByIntraId(targetUserIntraId).orElseThrow();
        int currentExp = ExpLevelCalculator.getCurrentLevelMyExp(targetUser.getTotalExp());
        int maxExp = ExpLevelCalculator.getLevelMaxExp(ExpLevelCalculator.getLevel(targetUser.getTotalExp()));
        String statusMessage = getUserStatusMessage(targetUserIntraId);
        UserDetailResponseDto responseDto = UserDetailResponseDto.builder()
                .intraId(targetUser.getIntraId())
                .userImageUri(targetUser.getImageUri())
                .racketType(targetUser.getRacketType().getCode())
                .statusMessage(statusMessage)
                .level(ExpLevelCalculator.getLevel(targetUser.getTotalExp()))
                .currentExp(currentExp)
                .maxExp(maxExp)
                .snsNotiOpt(targetUser.getSnsNotiOpt())
                .build();
        return responseDto;
    }

    @Transactional
    public void updateUser(String racketType, String statusMessage, String snsNotiOpt, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.update(RacketType.valueOf(racketType), SnsType.valueOf(snsNotiOpt));
        // TODO: 2023/05/03 redis에서 statusMessage update
    }

    private String getUserStatusMessage(String targetUserIntraId) {
        // TODO: 2023/05/03 redis에서 해당 유저의 현재 시즌의 rank data에서 statusMessage 가져오기
        return null;
    }
}
