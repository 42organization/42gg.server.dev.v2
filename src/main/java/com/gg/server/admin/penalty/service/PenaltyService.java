package com.gg.server.admin.penalty.service;

import com.gg.server.admin.penalty.data.PenaltyAdminRepository;
import com.gg.server.domain.penalty.data.Penalty;
import com.gg.server.domain.penalty.data.RedisPenaltyUser;
import com.gg.server.admin.penalty.data.RedisPenaltyUserRepository;
import com.gg.server.admin.penalty.dto.PenaltyListResponseDto;
import com.gg.server.admin.penalty.dto.PenaltyUserResponseDto;
import com.gg.server.domain.penalty.type.PenaltyType;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
    private final UserRepository userRepository;
    private final PenaltyAdminRepository penaltyRepository;

    @Transactional
    public void givePenalty(String intraId, Integer penaltyTime, String reason) {
        User user = userRepository.findByIntraId(intraId).orElseThrow(() ->
                new InvalidParameterException("user not found", ErrorCode.BAD_REQUEST));
        Optional<RedisPenaltyUser> redisPenaltyUser = redisPenaltyUserRepository.findByIntraId(intraId);
        LocalDateTime releaseTime;
        RedisPenaltyUser penaltyUser;
        LocalDateTime now = LocalDateTime.now();
        if (redisPenaltyUser.isPresent()) {
            releaseTime = redisPenaltyUser.get().getReleaseTime().plusHours(penaltyTime);
            penaltyUser = new RedisPenaltyUser(intraId, redisPenaltyUser.get().getPenaltyTime() + penaltyTime,
                    releaseTime, redisPenaltyUser.get().getStartTime(), reason);
        } else {
            releaseTime = now.plusHours(penaltyTime);
            penaltyUser = new RedisPenaltyUser(intraId, penaltyTime, releaseTime, now, reason);
        }
        Penalty penalty = new Penalty(user, PenaltyType.NOSHOW, reason, now, penaltyTime);
        penaltyRepository.save(penalty);
        redisPenaltyUserRepository.addPenaltyUser(penaltyUser, releaseTime);
    }


    public PenaltyListResponseDto getAllPenaltyUser(Pageable pageable, Boolean current) {
        Page<Penalty> allPenalties;
        if (current) {
            allPenalties = penaltyRepository.findAllCurrent(pageable, LocalDateTime.now());
        } else {
            allPenalties = penaltyRepository.findAll(pageable);
        }
        Page<PenaltyUserResponseDto> responseDtos = allPenalties.map(PenaltyUserResponseDto::new);
        return new PenaltyListResponseDto(responseDtos.getContent(), responseDtos.getNumber() + 1,
                responseDtos.getTotalPages());
    }

    public void deletePenalty(Long penaltyId) {
        Penalty penalty = penaltyRepository.findById(penaltyId).orElseThrow(()
        -> new NoSuchElementException());
        RedisPenaltyUser penaltyUser = redisPenaltyUserRepository
                .findByIntraId(penalty.getUser().getIntraId()).orElseThrow(()
                -> new InvalidParameterException("user not found", ErrorCode.BAD_REQUEST));
        redisPenaltyUserRepository.deletePenaltyInUser(penaltyUser,
                penalty.getPenaltyTime());//redis 시간 줄여주기
        //뒤에 있는 penalty 시간 당겨주기
        modifyStartTimeOfAfterPenalties(penalty);
        penaltyRepository.delete(penalty);
    }

    public PenaltyListResponseDto searchPenaltyUser(Pageable pageable, String intraId, Boolean current) {
        Page<Penalty> allPenalties;
        if (current) {
            allPenalties = penaltyRepository.findAllByIntraId(pageable, intraId);
        } else {
            allPenalties = penaltyRepository.findAllCurrentByIntraId(pageable, LocalDateTime.now(), intraId);
        }
        Page<PenaltyUserResponseDto> responseDtos = allPenalties.map(PenaltyUserResponseDto::new);
        return new PenaltyListResponseDto(responseDtos.getContent(), responseDtos.getNumber() + 1,
                responseDtos.getTotalPages());
    }

    private void modifyStartTimeOfAfterPenalties(Penalty penalty) {
        List<Penalty> afterPenalties = penaltyRepository.findAfterPenaltiesByUser(penalty.getUser().getId(),
                penalty.getStartTime());
        LocalDateTime newStartTime = LocalDateTime.now();
        for (Penalty afterPenalty : afterPenalties) {
            afterPenalty.updateStartTime(newStartTime);
            newStartTime = newStartTime.plusHours(afterPenalty.getPenaltyTime());
        }
    }
}
