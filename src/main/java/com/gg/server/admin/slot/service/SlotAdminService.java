package com.gg.server.admin.slot.service;

import com.gg.server.admin.slot.data.SlotManagementRepository;
import com.gg.server.admin.slot.dto.SlotAdminDto;
import com.gg.server.domain.slotmanagement.SlotManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SlotAdminService {
    private final SlotManagementRepository slotManagementRepository;

    @Transactional(readOnly = true)
    public SlotAdminDto getSlotSetting() {
        SlotManagement slotManagement = slotManagementRepository.findFirstByOrderByCreatedAtDesc();
        if (slotManagement == null) {
            return null;
        }
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
        slotManagementRepository.save(slotManagement);
    }
}
