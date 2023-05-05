package com.gg.server.domain.season;

import com.gg.server.domain.season.dto.SeasonListResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pingpong/")
public class SeasonController {

    private final SeasonService seasonService;
    @GetMapping("seasons")
    public SeasonListResDto seasonList() {
        return new SeasonListResDto(seasonService.seasonList());
    }
}
