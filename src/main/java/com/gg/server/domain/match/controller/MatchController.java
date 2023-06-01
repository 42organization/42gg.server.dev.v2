package com.gg.server.domain.match.controller;

import com.gg.server.domain.match.dto.MatchRequestDto;
import com.gg.server.domain.match.dto.MatchStatusResponseListDto;
import com.gg.server.domain.match.dto.SlotStatusResponseListDto;
import com.gg.server.domain.match.service.MatchService;
import com.gg.server.domain.match.service.MatchFindService;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.user.dto.UserDto;
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
    private final MatchService matchService;
    private final MatchFindService matchFindService;
    @PostMapping("match")
    public ResponseEntity createUserMatch(@RequestBody @Valid MatchRequestDto matchRequestDto,
                                          @Parameter(hidden = true) @Login UserDto user) {
        matchService.makeMatch(user, matchRequestDto.getOption(), matchRequestDto.getStartTime());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @DeleteMapping("match")
    public ResponseEntity deleteUserMatch(@RequestParam("startTime")
                                              @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startTime,
                                          @Parameter(hidden = true) @Login UserDto user) {
        matchService.cancelMatch(user, startTime);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("match/time/scope")
    public SlotStatusResponseListDto getMatchTimeScope(@RequestParam("mode") Option option,
                                                       @Parameter(hidden = true) @Login UserDto user){
        return matchFindService.getAllMatchStatus(user.getId(), option);
    }

    @GetMapping("match")
    public MatchStatusResponseListDto getCurrentMatch(@Parameter(hidden = true) @Login UserDto user) {
        return matchFindService.getCurrentMatch(user);
    }

}
