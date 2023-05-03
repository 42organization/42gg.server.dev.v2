package com.gg.server.domain.match.controller;

import com.gg.server.domain.match.dto.MatchAddDto;
import com.gg.server.domain.match.dto.MatchStatusResponseListDto;
import com.gg.server.domain.match.service.MatchRedisService;
import com.gg.server.domain.user.User;
import com.gg.server.global.utils.argumentresolver.Login;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
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
    public ResponseEntity createUserMatch(@RequestBody MatchAddDto matchRequestDto, @Login User user) {
        System.out.println("user = " + user.getIntraId());
        System.out.println("matchRequestDto = " + matchRequestDto.getMode());
//        System.out.println("matchRequestDto.getStartTime() = " + matchRequestDto.getStartTime());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("match")
    public ResponseEntity deleteUserMatch(@RequestParam("startTime") String timeData, @Login User user) {
        LocalDateTime startTime = LocalDateTime.parse(timeData);
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
