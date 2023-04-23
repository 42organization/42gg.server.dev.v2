package com.gg.server.domain.user.controller;

import com.gg.server.global.security.jwt.exception.TokenNotValidException;
import com.gg.server.global.security.jwt.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class UserController {
    private final TokenService tokenService;

    @GetMapping("/pingpong/user/accesstoken")
    public ResponseEntity generateNewAccessToken(@RequestParam String refreshToken) {
        try{
            String accessToken = tokenService.generateNewAccessToken(refreshToken);
            Map<String, String> resp = new HashMap<>();
            resp.put("access_token", accessToken);
            return new ResponseEntity(resp, HttpStatus.OK);
        } catch (TokenNotValidException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
