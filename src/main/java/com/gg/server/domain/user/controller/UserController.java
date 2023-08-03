package com.gg.server.domain.user.controller;

import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.*;
import com.gg.server.domain.user.exception.KakaoOauth2AlreadyExistException;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.domain.user.exception.UserTextColorException;
import com.gg.server.domain.user.service.UserAuthenticationService;
import com.gg.server.domain.user.service.UserService;
import com.gg.server.domain.user.service.UserTextColorCheckService;
import com.gg.server.domain.user.type.OauthType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.global.security.config.properties.AppProperties;
import com.gg.server.global.security.cookie.CookieUtil;
import com.gg.server.global.security.jwt.utils.TokenHeaders;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/users")

public class UserController {
    private final UserService userService;
    private final UserTextColorCheckService userTextColorCheck;
    private final UserAuthenticationService userAuthenticationService;
    private final AppProperties appProperties;
    private final CookieUtil cookieUtil;

    @PostMapping("/accesstoken")
    public ResponseEntity<UserAccessTokenDto> generateAccessToken(@RequestParam String refreshToken, HttpServletResponse response) {
        UserJwtTokenDto result = userAuthenticationService.regenerate(refreshToken);
        cookieUtil.addCookie(response, TokenHeaders.REFRESH_TOKEN, result.getRefreshToken(),
                (int)(appProperties.getAuth().getRefreshTokenExpiry() / 1000));
        return new ResponseEntity<>(new UserAccessTokenDto(result.getAccessToken()), HttpStatus.CREATED);
    }

    @GetMapping
    UserNormalDetailResponseDto getUserNormalDetail(@Parameter(hidden = true) @Login UserDto user){
        return userService.getUserNormalDetail(user);
    }

    @GetMapping("/live")
    UserLiveResponseDto getUserLiveDetail(@Parameter(hidden = true) @Login UserDto user) {
        return userService.getUserLiveDetail(user);
    }

    @GetMapping("/searches")
    UserSearchResponseDto searchUsers(@RequestParam String intraId){
        List<String> intraIds = userService.findByPartOfIntraId(intraId);
        return new UserSearchResponseDto(intraIds);
    }

    @GetMapping("/{intraId}")
    public UserDetailResponseDto getUserDetail(@PathVariable String intraId){
        return userService.getUserDetail(intraId);
    }

    @GetMapping("/{intraId}/rank")
    public UserRankResponseDto getUserRank(@PathVariable String intraId, @RequestParam Long season){
        return userService.getUserRankDetail(intraId, season);
    }

    @GetMapping("/{intraId}/historics")
    public UserHistoryResponseDto getUserHistory(@PathVariable String intraId, @RequestParam Long season) {
        return userService.getUserHistory(intraId, season);
    }

    @PutMapping("{intraId}")
    public ResponseEntity doModifyUser (@Valid @RequestBody UserModifyRequestDto userModifyRequestDto,
                                        @PathVariable String intraId, @Parameter(hidden = true) @Login UserDto loginUser) {
        if (!loginUser.getIntraId().equals(intraId)) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        userService.updateUser(userModifyRequestDto.getRacketType(), userModifyRequestDto.getStatusMessage(),
                userModifyRequestDto.getSnsNotiOpt(), intraId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        cookieUtil.deleteCookie(response, TokenHeaders.REFRESH_TOKEN);
    }

    /**
     * 42user 카카오 로그인 연동 해제
     */
    @DeleteMapping("/oauth/kakao")
    public void deleteOauthKakao(@Parameter(hidden = true) @Login UserDto user) {
        if (user.getRoleType().equals(RoleType.GUEST) || user.getKakaoId() == null) {
            throw new KakaoOauth2AlreadyExistException();
        }
        userService.deleteKakaoId(user.getId());
    }

    @GetMapping("/oauth")
    public UserOauthDto getUserOauth2Information(@Parameter(hidden = true) @Login UserDto user) {
        return new UserOauthDto(OauthType.of(user.getRoleType(), user.getKakaoId()).getCode());
    }

    @GetMapping("/images")
    public UserImageResponseDto getUserImage(@RequestParam(required = false) Long seasonId, Mode mode) {
        if (mode == Mode.RANK)
            return userService.getRankedUserImagesByPPP(seasonId);
        else{
            PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "totalExp"));
            return userService.getRankedUserImagesByExp(pageRequest);
        }
    }

    @PatchMapping  ("/text-color")
    public void updateTextColor(@RequestBody @Valid UserTextColorDto textColorDto, @Parameter(hidden = true) @Login UserDto user) {
        userService.updateTextColor(user.getId() ,textColorDto);
    }
    @PostMapping("/attendance")
    public UserAttendanceResponseDto attendUser(@Parameter(hidden = true) @Login UserDto user) {
        return userService.attendUser(user.getId());
    }
}
