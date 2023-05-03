package com.gg.server.admin.season.service;

import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.season.repository.SeasonAdminRepository;
import com.gg.server.domain.season.Season;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SeasonAdminService {
    private final SeasonAdminRepository seasonAdminRepository;

    public List<SeasonAdminDto> findAllSeasons() {
        List<Season> seasons =  seasonAdminRepository.findAll();
        List<SeasonAdminDto> dtoList = new ArrayList<>();
        for (Season season : seasons) {
            SeasonAdminDto dto = new SeasonAdminDto(season);
            dtoList.add(dto);
        }
        return dtoList;
    }
}
