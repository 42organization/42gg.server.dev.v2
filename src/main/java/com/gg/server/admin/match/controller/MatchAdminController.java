package com.gg.server.admin.match.controller;

import com.gg.server.admin.match.dto.EnrolledMatchesResponseDto;
import com.gg.server.admin.match.service.MatchAdminService;
import com.gg.server.domain.match.data.RedisMatchUser;
import com.gg.server.domain.match.type.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/match")
@Validated
public class MatchAdminController {
    private final MatchAdminService matchAdminService;

    /**
     * 매칭큐 조회
     * @param mode : BOTH, NORMAL, RANK, 쿼리 파라미터가 없을 경우 null (전체 조회)
     * @return
     */
    @GetMapping
    public ResponseEntity<EnrolledMatchesResponseDto> getMatch(@RequestParam(name = "mode", required = false) Option mode) {
        Map<LocalDateTime, List<RedisMatchUser>> matches = matchAdminService.getMatches(mode);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
