package com.gg.server.admin.penalty.service;

import com.gg.server.admin.penalty.data.RedisPenaltyUser;
import com.gg.server.admin.penalty.data.RedisPenaltyUserRepository;
import com.gg.server.admin.penalty.dto.PenaltyListResponseDto;
import com.gg.server.admin.penalty.dto.PenaltyUserResponseDto;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PenaltyService {
    private final RedisPenaltyUserRepository redisPenaltyUserRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public void givePenalty(String intraId, Integer penaltyTime, String reason) {
        userRepository.findByIntraId(intraId).orElseThrow(() ->
                new InvalidParameterException("user not found", ErrorCode.BAD_REQUEST));
        Optional<RedisPenaltyUser> user = redisPenaltyUserRepository.findByIntraId(intraId);
        LocalDateTime releaseTime;
        RedisPenaltyUser penaltyUser;
        if (user.isPresent()) {
            releaseTime = user.get().getReleaseTime().plusHours(penaltyTime);
            penaltyUser = new RedisPenaltyUser(intraId, user.get().getPenaltyTime() + penaltyTime,
                    releaseTime, user.get().getStartTime(), reason);
        } else {
            LocalDateTime now = LocalDateTime.now();
            releaseTime = now.plusHours(penaltyTime);
            penaltyUser = new RedisPenaltyUser(intraId, penaltyTime, releaseTime, now, reason);
        }
        redisPenaltyUserRepository.addPenaltyUser(penaltyUser, releaseTime);
    }

    public PenaltyListResponseDto getAllPenaltyUser(Integer page, Integer size) {
        List<RedisPenaltyUser> penaltyUsers = redisPenaltyUserRepository.findAll();
        List<PenaltyUserResponseDto> penaltyUserResponseDtos = penaltyUsers.stream()
                .map(PenaltyUserResponseDto::new).collect(Collectors.toList());
        Integer totalPages = (penaltyUserResponseDtos.size() - 1) / size + 1;
        List<PenaltyUserResponseDto> pagedUserDtos = paging(size, page, penaltyUserResponseDtos);
        return new PenaltyListResponseDto(pagedUserDtos, page, totalPages);
    }

    public void releasePenaltyUser(String intraId) {
        redisPenaltyUserRepository.deletePenaltyUser(intraId);
    }

    public PenaltyListResponseDto searchPenaltyUser(String keyword, Integer page, Integer size) {
        List<RedisPenaltyUser> penaltyUsers = redisPenaltyUserRepository.findAllByKeyword(keyword);
        List<PenaltyUserResponseDto> penaltyUserResponseDtos =
                penaltyUsers.stream().map(PenaltyUserResponseDto::new).collect(Collectors.toList());
        Integer totalPages = (penaltyUserResponseDtos.size() - 1) / size + 1;
        List<PenaltyUserResponseDto> pagedUserDtos = paging(size, page, penaltyUserResponseDtos);
        return new PenaltyListResponseDto(pagedUserDtos, page, totalPages);
    }
    private List<PenaltyUserResponseDto> paging(Integer size, Integer page,
                                                List<PenaltyUserResponseDto> penaltyUserResponseDtos) {
        if ((page - 1) * size >= penaltyUserResponseDtos.size()) {
            return new ArrayList<PenaltyUserResponseDto>();
        }
        Integer pageIndex = (page - 1) * size;
        Integer nextPageIndex = page * size;
        return penaltyUserResponseDtos.subList(pageIndex, Math.min(nextPageIndex, penaltyUserResponseDtos.size()));
    }
}
