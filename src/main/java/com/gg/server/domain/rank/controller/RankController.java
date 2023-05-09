package com.gg.server.domain.rank.controller;

import com.gg.server.domain.rank.dto.ExpRankPageResponseDto;
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
    @GetMapping("/vip")
    public ExpRankPageResponseDto getExpRankPage(Pageable pageRequest, @Parameter(hidden = true) @Login UserDto user) {
        return rankService.getExpRankPage(pageRequest.getPageNumber(), pageRequest.getPageSize(), user);
    }
}
