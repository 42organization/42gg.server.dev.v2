package com.gg.server.admin.season.controller;

import com.gg.server.admin.rank.service.RankAdminService;
import com.gg.server.admin.rank.service.RankRedisAdminService;
import com.gg.server.admin.season.dto.SeasonCreateRequestDto;
import com.gg.server.admin.season.dto.SeasonUpdateRequestDto;
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
    private final RankRedisAdminService rankRedisAdminService;
    private final RankAdminService rankAdminService;

    @GetMapping(value = "/seasons")
    public SeasonListAdminResponseDto rankSeasonList() {
        List<SeasonAdminDto> seasons = seasonAdminService.findAllSeasons();

        return new SeasonListAdminResponseDto(seasons);
    }

    @PostMapping(value = "/season")
    public void createSeason(@Valid @RequestBody SeasonCreateRequestDto seasonCreateReqeustDto) {
        Long seasonId = seasonAdminService.createSeason(seasonCreateReqeustDto);

        SeasonAdminDto seasonAdminDto = seasonAdminService.findSeasonById(seasonId);
        if (LocalDateTime.now().isBefore(seasonAdminDto.getStartTime())) {
            rankAdminService.addAllUserRankByNewSeason(seasonAdminDto);
            rankRedisAdminService.addAllUserRankByNewSeason(seasonAdminDto);
        }
    }

    @DeleteMapping(value = "/season/{seasonId}")
    public void deleteSeason(@PathVariable Long seasonId) {
        SeasonAdminDto seasonDto = seasonAdminService.findSeasonById(seasonId);
        seasonAdminService.deleteSeason(seasonId);
        if (LocalDateTime.now().isBefore(seasonDto.getStartTime())) {
            rankAdminService.deleteAllUserRankBySeason(seasonDto);
            rankRedisAdminService.deleteSeasonRankBySeasonId(seasonDto.getSeasonId());
        }
    }

    @PutMapping(value = "/season/{seasonId}")
    public void updateSeason(@PathVariable Long seasonId, @RequestBody SeasonUpdateRequestDto seasonUpdateRequestDto) {
        seasonAdminService.updateSeason(seasonId, seasonUpdateRequestDto);
        SeasonAdminDto seasonAdminDto = seasonAdminService.findSeasonById(seasonId);
        if (LocalDateTime.now().isBefore(seasonAdminDto.getStartTime())) {
            rankAdminService.deleteAllUserRankBySeason(seasonAdminDto);
            rankAdminService.addAllUserRankByNewSeason(seasonAdminDto);
            rankRedisAdminService.deleteSeasonRankBySeasonId(seasonAdminDto.getSeasonId());
            rankRedisAdminService.addAllUserRankByNewSeason(seasonAdminDto);
        }
    }
}
