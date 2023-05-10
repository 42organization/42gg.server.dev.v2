package com.gg.server.domain.season;

import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.season.dto.SeasonResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeasonService {
    private final SeasonRepository seasonRepository;

    public List<SeasonResDto> seasonList() {
        return seasonRepository.findAll().stream().map(SeasonResDto::new).collect(Collectors.toList());
    }

//    public CurSeason getCurSeason() {
//        return new CurSeason(seasonRepository.findByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(LocalDateTime.now())
//                .orElseThrow(() -> new NotExistException("현재 시즌이 존재하지 않습니다.", ErrorCode.NOT_FOUND)));
//    }
}
