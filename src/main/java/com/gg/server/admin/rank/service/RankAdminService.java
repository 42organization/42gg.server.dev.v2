package com.gg.server.admin.rank.service;

import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.domain.rank.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.user.User;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.AdminException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RankAdminService {
    private final RankRepository rankRepository;
    private final UserAdminRepository userAdminRepository;

    @Transactional
    public void addAllUserRankByNewSeason(SeasonAdminDto seasonAdminDto) {
        if (LocalDateTime.now().isAfter(seasonAdminDto.getStartTime()))
            throw new AdminException("시즌 시작시간이 과거입니다", ErrorCode.BAD_REQUEST);
        List<User> users = userAdminRepository.findAll();

        List<Rank> ranks = new ArrayList<>();
        users.forEach(user -> {
            Rank userRank = Rank.from(user, seasonAdminDto.getSeasonId(), seasonAdminDto.getStartPpp());
            ranks.add(userRank);
        });
        rankRepository.saveAll(ranks);
    }
}
