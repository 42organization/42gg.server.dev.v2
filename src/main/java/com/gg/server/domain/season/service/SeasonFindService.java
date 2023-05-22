package com.gg.server.domain.season.service;

import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.season.exception.SeasonNotFoundException;
import com.gg.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class SeasonFindService {
    private final SeasonRepository seasonRepository;

    @Transactional(readOnly = true)
    public Season findCurrentSeason(LocalDateTime now){
        return seasonRepository.findCurrentSeason(LocalDateTime.now())
                .orElseThrow(() -> new SeasonNotFoundException("현재 시즌이 없습니다.", ErrorCode.SEASON_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Season findSeasonById(Long seasonId){
        return seasonRepository.findById(seasonId)
                .orElseThrow(() -> new SeasonNotFoundException("해당 시즌이 없습니다. season id = " + seasonId, ErrorCode.SEASON_NOT_FOUND));
    }
}
