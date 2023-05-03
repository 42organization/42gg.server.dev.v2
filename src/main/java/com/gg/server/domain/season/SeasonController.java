package com.gg.server.domain.season;

import com.gg.server.domain.season.dto.SeasonListResDto;
import com.gg.server.domain.season.dto.SeasonResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
@RestController("/pingpong")
@RequiredArgsConstructor
public class SeasonController {

    private final SeasonService seasonService;
    @GetMapping("/seasons")
    public SeasonListResDto seasonList() {
        return new SeasonListResDto(seasonService.seasonList());
    }
}
