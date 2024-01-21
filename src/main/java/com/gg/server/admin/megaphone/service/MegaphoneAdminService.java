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

    /**
     * <p>메가폰 기록을 가져옵니다.</p>
     * @param pageable
     * @return
     */
    public MegaphoneHistoryResponseDto getMegaphoneHistory(Pageable pageable) {
        Page<MegaphoneAdminResponseDto> megaphoneHistory = megaphoneAdminRepository.findAll(pageable).map(MegaphoneAdminResponseDto::new);
        return new MegaphoneHistoryResponseDto(megaphoneHistory.getContent(), megaphoneHistory.getTotalPages());
    }

    /**
     * <p>intraId를 기반으로 메가폰 기록을 가져옵니다.</p>
     * @param intraId 타겟 인트라id
     * @param pageable
     * @return
     */
    public MegaphoneHistoryResponseDto getMegaphoneHistoryByIntraId(String intraId, Pageable pageable) {
        Page<MegaphoneAdminResponseDto> megaphoneHistory = megaphoneAdminRepository.findMegaphonesByUserIntraId(intraId, pageable).map(MegaphoneAdminResponseDto::new);
        return new MegaphoneHistoryResponseDto(megaphoneHistory.getContent(), megaphoneHistory.getTotalPages());
    }
}
