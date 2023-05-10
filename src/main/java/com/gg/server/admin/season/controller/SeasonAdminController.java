package com.gg.server.admin.season.controller;

import com.gg.server.admin.rank.service.RankAdminService;
import com.gg.server.admin.rank.service.RankRedisAdminService;
import com.gg.server.admin.season.dto.SeasonCreateRequestDto;
import com.gg.server.admin.season.dto.SeasonUpdateRequestDto;
import com.gg.server.admin.season.service.SeasonAdminService;
import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.season.dto.SeasonListAdminResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
    public ResponseEntity createSeason(@Valid @RequestBody SeasonCreateRequestDto seasonCreateReqeustDto) {
        Long seasonId = seasonAdminService.createSeason(seasonCreateReqeustDto);

        SeasonAdminDto seasonAdminDto = seasonAdminService.findSeasonById(seasonId);
        if (LocalDateTime.now().isBefore(seasonAdminDto.getStartTime())) {
            rankAdminService.addAllUserRankByNewSeason(seasonAdminDto);
            rankRedisAdminService.addAllUserRankByNewSeason(seasonAdminDto);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/season/{seasonId}")
    public ResponseEntity deleteSeason(@PathVariable Long seasonId) {
        SeasonAdminDto seasonDto = seasonAdminService.findSeasonById(seasonId);
        seasonAdminService.deleteSeason(seasonId);

        if (LocalDateTime.now().isBefore(seasonDto.getStartTime())) {
            rankAdminService.deleteAllUserRankBySeason(seasonDto);
            rankRedisAdminService.deleteSeasonRankBySeasonId(seasonDto.getSeasonId());
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/season/{seasonId}")
    public ResponseEntity updateSeason(@PathVariable Long seasonId, @RequestBody SeasonUpdateRequestDto seasonUpdateRequestDto) {
        seasonAdminService.updateSeason(seasonId, seasonUpdateRequestDto);
        SeasonAdminDto seasonAdminDto = seasonAdminService.findSeasonById(seasonId);
        if (LocalDateTime.now().isBefore(seasonAdminDto.getStartTime())) {
            rankAdminService.deleteAllUserRankBySeason(seasonAdminDto);
            rankAdminService.addAllUserRankByNewSeason(seasonAdminDto);
            rankRedisAdminService.deleteSeasonRankBySeasonId(seasonAdminDto.getSeasonId());
            rankRedisAdminService.addAllUserRankByNewSeason(seasonAdminDto);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
