package com.gg.server.domain.match.controller;

import com.gg.server.domain.match.dto.MatchRequestDto;
import com.gg.server.domain.match.service.MatchRedisService;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDateTime;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
        //rank, normal, both 아닌 다른 mode 들어오면 IllegalArgumentException
        try {
            Option option = Option.getEnumValue(matchRequestDto.getOption());
            //rank 부분 결정 안되서 ppp hard coding
            matchRedisService.makeMatch(user.getIntraId(), 1000, option, matchRequestDto.getStartTime());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @DeleteMapping("match")
    public ResponseEntity deleteUserMatch(@RequestParam("startTime") String timeData, @Parameter(hidden = true) @Login UserDto user) {
        LocalDateTime startTime = LocalDateTime.parse(timeData);
        matchRedisService.cancelMatch(user.getIntraId(), startTime);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

//    @GetMapping("match")
//    public MatchStatusResponseListDto getMatchBoards(@RequestParam(name="mode") String mode, @Login User user) {
////        MatchStatusResponseDto matchStatusResponseDto = new MatchStatusResponseDto();
////        MatchStatusResponseListDto matchBoards = new MatchStatusResponseListDto(List.of(matchStatusResponseDto));
////        return matchBoards;
//        return null;
//    }
}
