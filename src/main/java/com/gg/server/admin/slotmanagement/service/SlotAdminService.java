package com.gg.server.admin.slotmanagement.service;

import com.gg.server.admin.slotmanagement.data.adminSlotManagementRepository;
import com.gg.server.admin.slotmanagement.dto.SlotAdminDto;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.exception.SmNotFoundException;
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
                .orElseThrow(() -> new SmNotFoundException());

        return new SlotAdminDto(slotManagement);
    }

    public void addSlotSetting(SlotAdminDto requestDto) {
        SlotManagement slotManagement = new SlotManagement(requestDto);

        adminSlotManagementRepository.save(slotManagement);
    }
}
