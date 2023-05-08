package com.gg.server.admin.season.controller;

import com.gg.server.admin.season.dto.SeasonCreateRequestDto;
import com.gg.server.admin.season.service.SeasonAdminService;
import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.season.dto.SeasonListAdminResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/pingpong/admin")
@AllArgsConstructor
public class SeasonAdminController {
    private final SeasonAdminService seasonAdminService;

    @GetMapping(value = "/seasons")
    public SeasonListAdminResponseDto rankSeasonList() {
        List<SeasonAdminDto> seasons = seasonAdminService.findAllSeasons();

        return new SeasonListAdminResponseDto(seasons);
    }

    @PostMapping(value = "/season")
    public void createSeason(@Valid @RequestBody SeasonCreateRequestDto seasonCreateReqeustDto) {
        Integer seasonId = seasonAdminService.createSeason(seasonCreateReqeustDto);
        SeasonDto seasonDto = seasonAdminService.findSeasonById(seasonId);
        if ((seasonDto.getSeasonMode() == Mode.BOTH || seasonDto.getSeasonMode() == Mode.RANK)
                && LocalDateTime.now().isBefore(seasonDto.getStartTime())) {
            rankAdminService.addAllUserRankByNewSeason(seasonDto, seasonDto.getStartPpp());
            RankRedisAdminService.addAllUserRankByNewSeason(seasonDto, seasonDto.getStartPpp());
        }
    }

    @DeleteMapping(value = "/season/{seasonId}")
    public void deleteSeason(Integer seasonId) {
        SeasonDto seasonDto = seasonAdminService.findSeasonById(seasonId);
        seasonAdminService.deleteSeason(seasonId);
        if ((seasonDto.getSeasonMode() == Mode.BOTH || seasonDto.getSeasonMode() == Mode.RANK)
                && LocalDateTime.now().isBefore(seasonDto.getStartTime())) {
            rankAdminService.deleteAllUserRankBySeason(seasonDto);
            RankRedisAdminService.deleteSeasonRankBySeasonId(seasonDto.getId());
        }
    }

    @PutMapping(value = "/season/{seasonId}")
    public void updateSeason(Integer seasonId, SeasonUpdateRequestDto seasonUpdateRequestDto) {
        seasonAdminService.updateSeason(seasonId, seasonUpdateRequestDto);
        SeasonDto seasonDto = seasonAdminService.findSeasonById(seasonId);
        if ((seasonDto.getSeasonMode() == Mode.BOTH || seasonDto.getSeasonMode() == Mode.RANK)
                && LocalDateTime.now().isBefore(seasonDto.getStartTime())) {
            rankAdminService.deleteAllUserRankBySeason(seasonDto);
            rankAdminService.addAllUserRankByNewSeason(seasonDto, seasonDto.getStartPpp());
            RankRedisAdminService.deleteSeasonRankBySeasonId(seasonDto.getId());
            RankRedisAdminService.addAllUserRankByNewSeason(seasonDto, seasonDto.getStartPpp());
        }
    }
}
