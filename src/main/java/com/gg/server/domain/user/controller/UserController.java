package com.gg.server.domain.user.controller;

import com.gg.server.domain.user.service.UserService;
import com.gg.server.global.security.jwt.exception.TokenNotValidException;
import com.gg.server.global.security.jwt.repository.JwtRedisRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.global.security.jwt.utils.TokenHeaders;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @PostMapping("/pingpong/user/accesstoken")
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
}
