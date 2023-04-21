package com.gg.server.user.controller;

import com.gg.server.global.security.config.properties.AppProperties;
import com.gg.server.global.security.cookie.CookieUtil;
import com.gg.server.global.security.jwt.AuthTokenProvider;
import com.gg.server.global.security.jwt.TokenHeaders;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final AuthTokenProvider tokenProvider;

    @GetMapping("/pingpong/user/accesstoken")
    public ResponseEntity generateNewAccessToken(@RequestBody String refreshToken) {
        if (tokenProvider.getTokenClaims(refreshToken) != null) {
            Long userId = tokenProvider.getUserIdFromToken(refreshToken);
            String newAccessToken = tokenProvider.createToken(userId);
            Map<String, String> resp = new HashMap<>();
            resp.put(TokenHeaders.ACCESS_TOKEN, newAccessToken);
            return new ResponseEntity(resp, HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }
}
