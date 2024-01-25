package com.gg.server.domain.season.service;

import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.season.dto.CurSeason;
import com.gg.server.domain.season.dto.SeasonResDto;
import com.gg.server.domain.season.exception.SeasonNotFoundException;
import com.gg.server.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeasonService {
    private final SeasonRepository seasonRepository;

    public List<SeasonResDto> seasonList() {
        return seasonRepository.findActiveSeasonsDesc(LocalDateTime.now())
                .stream().map(SeasonResDto::new).collect(Collectors.toList());
    }

   public CurSeason getCurSeason() {
       return new CurSeason(seasonRepository.findCurrentSeason(LocalDateTime.now())
               .orElseThrow(() -> new SeasonNotFoundException("현재 시즌이 존재하지 않습니다.")));
   }
}
