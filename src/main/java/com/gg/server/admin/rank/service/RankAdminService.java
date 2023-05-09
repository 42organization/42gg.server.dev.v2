package com.gg.server.admin.rank.service;

import com.gg.server.admin.season.data.SeasonAdminRepository;
import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.user.User;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.AdminException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RankAdminService {
    private final RankRepository rankRepository;
    private final UserAdminRepository userAdminRepository;
    private final SeasonRepository seasonRepository;
    private final SeasonAdminRepository seasonAdminRepository;

    @Transactional
    public void addAllUserRankByNewSeason(SeasonAdminDto seasonAdminDto) {
        if (LocalDateTime.now().isAfter(seasonAdminDto.getStartTime()))
            throw new AdminException("시즌 시작시간이 과거입니다", ErrorCode.BAD_REQUEST);
        List<User> users = userAdminRepository.findAll();

        List<Rank> ranks = new ArrayList<>();
        Season season = seasonRepository.findById(seasonAdminDto.getSeasonId()).get();
        users.forEach(user -> {
            Rank userRank = Rank.from(user, season, seasonAdminDto.getStartPpp());
            ranks.add(userRank);
        });
        rankRepository.saveAll(ranks);
    }

    @Transactional
    public void deleteAllUserRankBySeason(SeasonAdminDto seasonAdminDto) {
        if (LocalDateTime.now().isAfter(seasonAdminDto.getStartTime()))
            throw new AdminException("현재시간 이전의 시즌을 생성 할 수 없습니다.", ErrorCode.BAD_REQUEST);

        rankRepository.deleteAllBySeasonId(seasonAdminDto.getSeasonId());
    }
}
