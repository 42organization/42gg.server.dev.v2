package com.gg.server.admin.season.service;

import com.gg.server.admin.rank.service.RankAdminService;
import com.gg.server.admin.rank.service.RankRedisAdminService;
import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.season.data.SeasonAdminRepository;
import com.gg.server.admin.season.dto.SeasonCreateRequestDto;
import com.gg.server.admin.season.dto.SeasonUpdateRequestDto;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.exception.SeasonForbiddenException;
import com.gg.server.domain.season.exception.SeasonNotFoundException;
import com.gg.server.domain.season.exception.SeasonTimeBeforeException;
import com.gg.server.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SeasonAdminService {
    private final SeasonAdminRepository seasonAdminRepository;
    private final RankRedisAdminService rankRedisAdminService;
    private final RankAdminService rankAdminService;

    public List<SeasonAdminDto> findAllSeasons() {
        List<Season> seasons =  seasonAdminRepository.findAll();
        List<SeasonAdminDto> dtoList = new ArrayList<>();
        for (Season season : seasons) {
            SeasonAdminDto dto = new SeasonAdminDto(season);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Transactional
    public void createSeason(SeasonCreateRequestDto createDto) {
        Season newSeason = new Season(createDto);

        insert(newSeason);
        seasonAdminRepository.save(newSeason);

        Long seasonId = newSeason.getId();
        SeasonAdminDto seasonAdminDto = findSeasonById(seasonId);

        if (LocalDateTime.now().isBefore(seasonAdminDto.getStartTime())) {
            rankAdminService.addAllUserRankByNewSeason(seasonAdminDto);
            rankRedisAdminService.addAllUserRankByNewSeason(seasonAdminDto);
        }
    }

    @Transactional
    public SeasonAdminDto findSeasonById(Long seasonId) {
        Season season = seasonAdminRepository.findById(seasonId).orElseThrow(()-> new SeasonNotFoundException());

        return new SeasonAdminDto(season);
    }


    @Transactional
    public void deleteSeason(Long seasonId) {
        SeasonAdminDto seasonDto = findSeasonById(seasonId);

        Season season = seasonAdminRepository.findById(seasonDto.getSeasonId())
                .orElseThrow(() -> new SeasonNotFoundException());
        detach(season);

        if (LocalDateTime.now().isBefore(seasonDto.getStartTime())) {
            rankAdminService.deleteAllUserRankBySeason(seasonDto);
            rankRedisAdminService.deleteSeasonRankBySeasonId(seasonDto.getSeasonId());
            seasonAdminRepository.delete(season);
        }
    }

    @Transactional
    public void updateSeason(Long seasonId, SeasonUpdateRequestDto updateDto) {
        Season season = seasonAdminRepository.findById(seasonId)
                .orElseThrow(() -> new SeasonNotFoundException());

        if (LocalDateTime.now().isBefore(season.getStartTime())) {
            detach(season);
            season.setSeasonName(updateDto.getSeasonName());
            season.setStartTime(updateDto.getStartTime());
            season.setStartPpp(updateDto.getStartPpp());
            season.setPppGap(updateDto.getPppGap());
            insert(season);
            seasonAdminRepository.save(season);
        }

        SeasonAdminDto seasonAdminDto = findSeasonById(seasonId);
        if (LocalDateTime.now().isBefore(seasonAdminDto.getStartTime())) {
            rankAdminService.deleteAllUserRankBySeason(seasonAdminDto);
            rankAdminService.addAllUserRankByNewSeason(seasonAdminDto);
            rankRedisAdminService.deleteSeasonRankBySeasonId(seasonAdminDto.getSeasonId());
            rankRedisAdminService.addAllUserRankByNewSeason(seasonAdminDto);
        }
    }

    private void insert(Season season)
    {
        List<Season> beforeSeasons = seasonAdminRepository.findBeforeSeasons(season.getStartTime());
        Season beforeSeason;
        if (beforeSeasons.isEmpty())
            beforeSeason = null;
        else
            beforeSeason = beforeSeasons.get(0).getId() != season.getId()? beforeSeasons.get(0) : beforeSeasons.get(1);
        List<Season> afterSeasons = seasonAdminRepository.findAfterSeasons(season.getStartTime());
        Season afterSeason = afterSeasons.isEmpty() ? null : afterSeasons.get(0);

        if (LocalDateTime.now().plusHours(24).isAfter(season.getStartTime()))
            throw new SeasonTimeBeforeException();
        if (beforeSeason != null) {
            if (beforeSeason.getStartTime().plusDays(1).isAfter(season.getStartTime()))
               throw new SeasonForbiddenException();
            beforeSeason.setEndTime(season.getStartTime().minusSeconds(1));
        }
        if (afterSeason != null)
            season.setEndTime(afterSeason.getStartTime().minusSeconds(1));
        else
            season.setEndTime(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
    }

    private void detach(Season season)
    {
        List<Season> beforeSeasons = seasonAdminRepository.findBeforeSeasons(season.getStartTime());
        Season beforeSeason = beforeSeasons.isEmpty() ? null : beforeSeasons.get(0);
        List<Season> afterSeasons = seasonAdminRepository.findAfterSeasons(season.getStartTime());
        Season afterSeason = afterSeasons.isEmpty() ? null : afterSeasons.get(0);

        if ((LocalDateTime.now().isAfter(season.getStartTime()) && LocalDateTime.now().isBefore(season.getEndTime()))
                || season.getEndTime().isBefore(LocalDateTime.now()))
            throw new SeasonForbiddenException();
        if (beforeSeason != null) {
            if (afterSeason != null)
                beforeSeason.setEndTime(afterSeason.getStartTime().minusSeconds(1));
            else
                beforeSeason.setEndTime(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        }
    }
}
