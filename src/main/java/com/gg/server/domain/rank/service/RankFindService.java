package com.gg.server.domain.rank.service;

import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.exception.RankNotFoundException;
import com.gg.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RankFindService {
    private final RankRepository rankRepository;

    @Transactional(readOnly = true)
    public Rank findByUserIdAndSeasonId(Long userId, Long seasonId){
        return  rankRepository.findByUserIdAndSeasonId(userId, seasonId)
                .orElseThrow(() -> new RankNotFoundException("랭크 테이블에 없는 유저입니다.", ErrorCode.RANK_NOT_FOUND));
    }
}
