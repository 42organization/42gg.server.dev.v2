package com.gg.server.domain.user.controller;

import com.gg.server.domain.user.User;
import com.gg.server.domain.user.dto.UserLiveResponseDto;
import com.gg.server.domain.user.dto.UserNormalDetailResponseDto;
import com.gg.server.domain.user.dto.UserSearchResponseDto;
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
@RequestMapping("/pingpong/")
public class UserController {

    private final UserService userService;

    @PostMapping("users/accesstoken")
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

    @GetMapping("users")
    UserNormalDetailResponseDto getUserNormalDetail(@Parameter(hidden = true) @Login User user){
        Boolean isAdmin = user.getRoleType() == RoleType.ADMIN;
        return new UserNormalDetailResponseDto(user.getIntraId(), user.getImageUri(), isAdmin);
    }

    @GetMapping("users/live")
    UserLiveResponseDto getUserLiveDetail(@Login User user) {
        return userService.getUserLiveDetail(user);
    }

    @GetMapping("users/searches")
    UserSearchResponseDto searchUsers(@RequestParam String q){
        List<String> intraIds = userService.findByPartofIntraId(q);
        return new UserSearchResponseDto(intraIds);
    }


}
