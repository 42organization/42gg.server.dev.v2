package com.gg.server.domain.user.controller;

import com.gg.server.domain.user.dto.*;
import com.gg.server.domain.user.service.UserService;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/accesstoken")
    public UserAccessTokenDto generateAccessToken(@RequestParam String refreshToken) {
        String accessToken = userService.regenerate(refreshToken);
        return new UserAccessTokenDto(accessToken);
    }

    @GetMapping
    UserNormalDetailResponseDto getUserNormalDetail(@Parameter(hidden = true) @Login UserDto user){
        Boolean isAdmin = user.getRoleType() == RoleType.ADMIN;
        return new UserNormalDetailResponseDto(user.getIntraId(), user.getImageUri(), isAdmin);
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
    public void doModifyUser (@Valid @RequestBody UserModifyRequestDto userModifyRequestDto, @PathVariable String intraId) {
        userService.updateUser(userModifyRequestDto.getRacketType(), userModifyRequestDto.getStatusMessage(),
                userModifyRequestDto.getSnsNotiOpt(), intraId);
    }

}
