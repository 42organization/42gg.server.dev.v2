package com.gg.server.admin.penalty.service;

import com.gg.server.admin.penalty.data.PenaltyAdminRepository;
import com.gg.server.domain.penalty.data.Penalty;
import com.gg.server.domain.penalty.data.RedisPenaltyUser;
import com.gg.server.admin.penalty.data.RedisPenaltyUserRepository;
import com.gg.server.admin.penalty.dto.PenaltyListResponseDto;
import com.gg.server.admin.penalty.dto.PenaltyUserResponseDto;
import com.gg.server.domain.penalty.exception.PenaltyExpiredException;
import com.gg.server.domain.penalty.exception.PenaltyNotFoundException;
import com.gg.server.domain.penalty.exception.RedisPenaltyUserNotFoundException;
import com.gg.server.domain.penalty.type.PenaltyType;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.service.UserFindService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PenaltyService {
    private final RedisPenaltyUserRepository redisPenaltyUserRepository;
    private final UserFindService userFindService;
    private final PenaltyAdminRepository penaltyRepository;

    @Transactional
    public void givePenalty(String intraId, Integer penaltyTime, String reason) {
        User user = userFindService.findByIntraId(intraId);
        Optional<RedisPenaltyUser> redisPenaltyUser = redisPenaltyUserRepository.findByIntraId(intraId);
        LocalDateTime releaseTime;
        RedisPenaltyUser penaltyUser;
        Penalty penalty;
        LocalDateTime now = LocalDateTime.now();
        if (redisPenaltyUser.isPresent()) {
            releaseTime = redisPenaltyUser.get().getReleaseTime().plusHours(penaltyTime);
            penaltyUser = new RedisPenaltyUser(intraId, redisPenaltyUser.get().getPenaltyTime() + penaltyTime,
                    releaseTime, redisPenaltyUser.get().getStartTime(), reason);
            penalty = new Penalty(user, PenaltyType.NOSHOW, reason, redisPenaltyUser.get().getReleaseTime(), penaltyTime);
        } else {
            releaseTime = now.plusHours(penaltyTime);
            penaltyUser = new RedisPenaltyUser(intraId, penaltyTime, releaseTime, now, reason);
            penalty = new Penalty(user, PenaltyType.NOSHOW, reason, now, penaltyTime);
        }
        penaltyRepository.save(penalty);
        redisPenaltyUserRepository.addPenaltyUser(penaltyUser, releaseTime);
    }


    @Transactional(readOnly = true)
    public PenaltyListResponseDto getAllPenalties(Pageable pageable, Boolean current) {
        Page<Penalty> allPenalties;
        if (current) {
            allPenalties = penaltyRepository.findAllCurrent(pageable, LocalDateTime.now());
        } else {
            allPenalties = penaltyRepository.findAll(pageable);
        }
        Page<PenaltyUserResponseDto> responseDtos = allPenalties.map(PenaltyUserResponseDto::new);
        return new PenaltyListResponseDto(responseDtos.getContent(), responseDtos.getTotalPages());
    }


    @Transactional
    public void deletePenalty(Long penaltyId) {
        Penalty penalty = penaltyRepository.findById(penaltyId).orElseThrow(()
        -> new PenaltyNotFoundException());
        if (penalty.getStartTime().plusHours(penalty.getPenaltyTime()).isBefore(LocalDateTime.now())) {
            throw new PenaltyExpiredException();
        }
        RedisPenaltyUser penaltyUser = redisPenaltyUserRepository
                .findByIntraId(penalty.getUser().getIntraId()).orElseThrow(()
                -> new RedisPenaltyUserNotFoundException());
        redisPenaltyUserRepository.deletePenaltyInUser(penaltyUser,
                penalty.getPenaltyTime());//redis 시간 줄여주기
        //뒤에 있는 penalty 시간 당겨주기
        modifyStartTimeOfAfterPenalties(penalty);
        penaltyRepository.delete(penalty);
    }

    @Transactional(readOnly = true)
    public PenaltyListResponseDto getAllPenaltiesByIntraId(Pageable pageable, String intraId, Boolean current) {
        Page<Penalty> allPenalties;
        if (current) {
            allPenalties = penaltyRepository.findAllCurrentByIntraId(pageable, LocalDateTime.now(), intraId);
        } else {
            allPenalties = penaltyRepository.findAllByIntraId(pageable, intraId);
        }
        Page<PenaltyUserResponseDto> responseDtos = allPenalties.map(PenaltyUserResponseDto::new);
        return new PenaltyListResponseDto(responseDtos.getContent(), responseDtos.getTotalPages());
    }

    private void modifyStartTimeOfAfterPenalties(Penalty penalty) {
        List<Penalty> afterPenalties = penaltyRepository.findAfterPenaltiesByUser(penalty.getUser().getId(),
                penalty.getStartTime());
        LocalDateTime newStartTime;
        if (penalty.getStartTime().isAfter(LocalDateTime.now())) {
            newStartTime = penalty.getStartTime();
        } else {
            newStartTime = LocalDateTime.now();
        }
        for (Penalty afterPenalty : afterPenalties) {
            afterPenalty.updateStartTime(newStartTime);
            newStartTime = newStartTime.plusHours(afterPenalty.getPenaltyTime());
        }
    }
}
