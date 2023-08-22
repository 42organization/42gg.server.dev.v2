package com.gg.server.admin.megaphone.service;

import com.gg.server.admin.megaphone.data.MegaphoneAdminRepository;

import com.gg.server.admin.megaphone.dto.MegaphoneAdminResponseDto;
import com.gg.server.admin.megaphone.dto.MegaphoneHistoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MegaphoneAdminService {
    private final MegaphoneAdminRepository megaphoneAdminRepository;

    public MegaphoneHistoryResponseDto getMegaphoneHistory(Pageable pageable) {
        Page<MegaphoneAdminResponseDto> megaphoneHistory = megaphoneAdminRepository.findAll(pageable).map(MegaphoneAdminResponseDto::new);
        return new MegaphoneHistoryResponseDto(megaphoneHistory.getContent(), megaphoneHistory.getTotalPages());
    }

    public MegaphoneHistoryResponseDto getMegaphoneHistoryByIntraId(String intraId, Pageable pageable) {
        Page<MegaphoneAdminResponseDto> megaphoneHistory = megaphoneAdminRepository.findMegaphonesByUserIntraId(intraId, pageable).map(MegaphoneAdminResponseDto::new);
        return new MegaphoneHistoryResponseDto(megaphoneHistory.getContent(), megaphoneHistory.getTotalPages());
    }
}
