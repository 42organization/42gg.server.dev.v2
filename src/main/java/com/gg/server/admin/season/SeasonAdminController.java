package com.gg.server.admin.season;

import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.season.dto.SeasonListAdminResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/pingpong/admin")
@AllArgsConstructor
public class SeasonAdminController {
    private final SeasonAdminService seasonAdminService;

    @GetMapping(value = "/seasons")
    public SeasonListAdminResponseDto rankSeasonList() {
        List<SeasonAdminDto> seasons = seasonAdminService.findAllSeasons();

        SeasonListAdminResponseDto responseDto = SeasonListAdminResponseDto.builder()
                .seasonList(seasons)
                .build();
        return responseDto;
    }

}
