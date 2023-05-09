package com.gg.server.admin.slot.service;

import com.gg.server.admin.slot.data.adminSlotManagementRepository;
import com.gg.server.admin.slot.dto.SlotAdminDto;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.AdminException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SlotAdminService {
    private final adminSlotManagementRepository adminSlotManagementRepository;

    @Transactional(readOnly = true)
    public SlotAdminDto getSlotSetting() {
        SlotManagement slotManagement = adminSlotManagementRepository.findFirstByOrderByCreatedAtDesc()
                .orElseThrow(() -> new AdminException("현재 슬롯DB값이 null입니다.", ErrorCode.SN001));

        return new SlotAdminDto(slotManagement);
    }

    public void addSlotSetting(Integer pastSlotTime, Integer futureSlotTime,
                               Integer interval, Integer openMinute) {
        SlotManagement slotManagement = SlotManagement.builder()
                .futureSlotTime(futureSlotTime)
                .pastSlotTime(pastSlotTime)
                .gameInterval(interval)
                .openMinute(openMinute)
                .build();
        adminSlotManagementRepository.save(slotManagement);
    }
}
