package com.gg.server.domain.match.controller;

import com.gg.server.domain.match.dto.MatchRequestDto;
import com.gg.server.domain.match.dto.MatchStatusResponseListDto;
import com.gg.server.domain.match.service.MatchRedisService;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDateTime;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/")
public class MatchController {
    private final MatchRedisService matchRedisService;
    @PostMapping("match")
    public ResponseEntity createUserMatch(@RequestBody @Valid MatchRequestDto matchRequestDto, @Parameter(hidden = true) @Login UserDto user) {
        //3회 이상 매칭 잡을 시 예외 처리
        if (matchRedisService.isUserMatch(user.getId(), matchRequestDto.getStartTime())) {
            throw new InvalidParameterException("match is already enrolled", ErrorCode.VALID_FAILED);
        }
        if (matchRedisService.countUserMatch(user.getId()) >= 3L) {
            throw new InvalidParameterException("enroll already three times", ErrorCode.VALID_FAILED);
        }
        matchRedisService.makeMatch(user.getId(), matchRequestDto.getOption(), matchRequestDto.getStartTime());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @DeleteMapping("match")
    public ResponseEntity deleteUserMatch(@RequestParam("startTime")
                                              @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
                                          @Parameter(hidden = true) @Login UserDto user) {
        if (!matchRedisService.isUserMatch(user.getId(), startTime)) {
            throw new InvalidParameterException("match is not enrolled", ErrorCode.VALID_FAILED);
        }
        matchRedisService.cancelMatch(user.getId(), startTime);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("match/time/scope")
    public MatchStatusResponseListDto getMatchTimeScope(@RequestParam("mode") Option option,
            @Parameter(hidden = true) @Login UserDto user){
        return matchRedisService.getAllMatchStatus(user.getId(), option);
    }
}
