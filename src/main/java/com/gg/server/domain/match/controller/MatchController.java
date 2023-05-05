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

    //redis 객체 따로 만들기 - option
    @PostMapping("match")
    public ResponseEntity createUserMatch(@RequestBody @Valid MatchRequestDto matchRequestDto, @Parameter(hidden = true) @Login UserDto user) {
        //rank - 관련해서 따로 정해야할 필요가 있음
        //user가 rank에 없을 시 season ppp 초기값 넣을 필요가 있음
        //3회 이상 매칭 잡을 시 예외 처리
        if (matchRedisService.isUserMatch(user.getIntraId(), matchRequestDto.getStartTime())) {
            throw new InvalidParameterException("match is already enrolled", ErrorCode.VALID_FAILED);
        }
        if (matchRedisService.countUserMatch(user.getIntraId()) >= 3L) {
            throw new InvalidParameterException("enroll already three times", ErrorCode.VALID_FAILED);
        }
        matchRedisService.makeMatch(user.getIntraId(), 1000, matchRequestDto.getOption(), matchRequestDto.getStartTime());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping("match")
    public ResponseEntity deleteUserMatch(@RequestParam("startTime")
                                              @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
                                          @Parameter(hidden = true) @Login UserDto user) {
        if (!matchRedisService.isUserMatch(user.getIntraId(), startTime)) {
            throw new InvalidParameterException("match is not enrolled", ErrorCode.VALID_FAILED);
        }
        matchRedisService.cancelMatch(user.getIntraId(), startTime);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("match/time/scope")
    public MatchStatusResponseListDto getMatchTimeScope(@Parameter(hidden = true) @Login UserDto user){
        return null;
    }
}
