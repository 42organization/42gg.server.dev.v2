package com.gg.server.domain.rank.controller;

import com.gg.server.domain.rank.dto.ExpRankPageResponseDto;
import com.gg.server.domain.rank.dto.RankPageResponseDto;
import com.gg.server.domain.rank.service.RankService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong")
public class RankController {

    private final RankService rankService;
    @GetMapping("/exp")
    public ExpRankPageResponseDto getExpRankPage(Pageable pageRequest, @Parameter(hidden = true) @Login UserDto user) {
        return rankService.getExpRankPage(pageRequest.getPageNumber(), pageRequest.getPageSize(), user);
    }

    /**
     *
     * @param pageRequest
     * @param user
     * @param season
     * @param gameType
     *
     *  gameType는 single로 고정되어 오고있는데 현재 rank게임은 single만 구현되어있어서 사용 안하기로
     */
    @GetMapping("/ranks/{gameType}")
    public RankPageResponseDto getRankPage(Pageable pageRequest, @Parameter(hidden = true) @Login UserDto user,
                                           Long season,  String gameType){
        return rankService.getRankPage(pageRequest.getPageNumber(), pageRequest.getPageSize(), user, season);
    }
}
