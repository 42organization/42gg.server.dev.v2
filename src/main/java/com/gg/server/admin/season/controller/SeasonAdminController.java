package com.gg.server.admin.season.controller;

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

    @PostMapping(value = "/seasons")
    public ResponseEntity createSeason(@Valid @RequestBody SeasonCreateRequestDto seasonCreateReqeustDto) {
        seasonAdminService.createSeason(seasonCreateReqeustDto);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/seasons/{seasonId}")
    public ResponseEntity deleteSeason(@PathVariable Long seasonId) {
        seasonAdminService.deleteSeason(seasonId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/seasons/{seasonId}")
    public ResponseEntity updateSeason(@PathVariable Long seasonId, @RequestBody SeasonUpdateRequestDto seasonUpdateRequestDto) {
        seasonAdminService.updateSeason(seasonId, seasonUpdateRequestDto);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
