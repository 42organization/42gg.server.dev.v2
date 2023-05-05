package com.gg.server.domain.user.controller;

import com.gg.server.domain.user.dto.*;
import com.gg.server.domain.user.service.UserService;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.global.security.jwt.exception.TokenNotValidException;
import com.gg.server.global.security.jwt.utils.TokenHeaders;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/users/")
public class UserController {

    private final UserService userService;

    @PostMapping("accesstoken")
    public ResponseEntity generateAccessToken(@RequestParam String refreshToken) {
        try {
            String accessToken = userService.regenerate(refreshToken);
            Map<String, String> result = new HashMap<>();
            result.put(TokenHeaders.ACCESS_TOKEN, accessToken);
            return new ResponseEntity(result, HttpStatus.OK);
        } catch (TokenNotValidException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping
    UserNormalDetailResponseDto getUserNormalDetail(@Parameter(hidden = true) @Login UserDto user){
        Boolean isAdmin = user.getRoleType() == RoleType.ADMIN;
        return new UserNormalDetailResponseDto(user.getIntraId(), user.getImageUri(), isAdmin);
    }

    @GetMapping("live")
    UserLiveResponseDto getUserLiveDetail(@Parameter(hidden = true) @Login UserDto user) {
        return userService.getUserLiveDetail(user);
    }

    @GetMapping("searches")
    UserSearchResponseDto searchUsers(@RequestParam String inquiringString){
        List<String> intraIds = userService.findByPartOfIntraId(inquiringString);
        return new UserSearchResponseDto(intraIds);
    }

    @GetMapping("{targetUserId}/detail")
    public UserDetailResponseDto getUserDetail(@PathVariable String targetUserId){
        return userService.getUserDetail(targetUserId);
    }

    @GetMapping("{targetUserId}/rank")
    public UserRankResponseDto getUserRank(@PathVariable String targetUserId){
        return userService.getUserRankDetail(targetUserId);
    }

    @GetMapping("{userId}/historics")
    public UserHistoryResponseDto getUserHistory(@PathVariable Long userId) {
        return userService.getUserHistory(userId);
    }

    @PutMapping("detail")
    public void doModifyUser (@RequestBody UserModifyRequestDto userModifyRequestDto, @Parameter(hidden = true) @Login UserDto userDto) {
        userService.updateUser(userModifyRequestDto.getRacketType(), userModifyRequestDto.getStatusMessage(),
                userModifyRequestDto.getSnsNotiOpt(), userDto.getId());
    }

}
