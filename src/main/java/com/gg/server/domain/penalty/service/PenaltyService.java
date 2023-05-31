package com.gg.server.domain.penalty.service;

import com.gg.server.domain.penalty.data.Penalty;
import com.gg.server.domain.penalty.data.PenaltyRepository;
import com.gg.server.domain.penalty.redis.PenaltyUserRedisRepository;
import com.gg.server.domain.penalty.redis.RedisPenaltyUser;
import com.gg.server.domain.penalty.type.PenaltyType;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.service.UserFindService;
import com.gg.server.domain.user.service.UserService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PenaltyService {
    private final PenaltyRepository penaltyRepository;
    private final PenaltyUserRedisRepository penaltyUserRedisRepository;
    private final UserFindService userFindService;

    /**
     * penalty 1분 부여
     * **/
    public void givePenalty(UserDto userDto, Integer penaltyMinutes) {

        User user = userFindService.findUserById(userDto.getId());
        Optional<RedisPenaltyUser> redisPenaltyUser = penaltyUserRedisRepository
                .findByIntraId(userDto.getIntraId());
        LocalDateTime releaseTime;
        RedisPenaltyUser penaltyUser;
        Penalty penalty;
        LocalDateTime now = LocalDateTime.now();
        if (redisPenaltyUser.isPresent()) {
            releaseTime = redisPenaltyUser.get().getReleaseTime().plusMinutes(penaltyMinutes);
            penaltyUser = new RedisPenaltyUser(userDto.getIntraId(), redisPenaltyUser.get().getPenaltyTime() + penaltyMinutes,
                    releaseTime, redisPenaltyUser.get().getStartTime(), "[AUTO] 매칭 취소");
            penalty = new Penalty(user, PenaltyType.CANCEL, "[AUTO] 매칭 취소", redisPenaltyUser.get().getReleaseTime(), penaltyMinutes);
        } else {
            releaseTime = now.plusHours(penaltyMinutes);
            penaltyUser = new RedisPenaltyUser(user.getIntraId(), penaltyMinutes, releaseTime, now, "[AUTO] 매칭 취소");
            penalty = new Penalty(user, PenaltyType.CANCEL, "[AUTO] 매칭 취소", now, penaltyMinutes);
        }
        penaltyRepository.save(penalty);
        penaltyUserRedisRepository.addPenaltyUser(penaltyUser, releaseTime);
    }

    public Boolean isPenaltyUser(String intraId) {
        return penaltyUserRedisRepository.findByIntraId(intraId).isPresent();
    }
}
