package com.gg.server.admin.slotmanagement.service;

import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.slotmanagement.data.adminSlotManagementRepository;
import com.gg.server.admin.slotmanagement.dto.SlotAdminDto;
import com.gg.server.admin.slotmanagement.dto.SlotCreateRequestDto;
import com.gg.server.admin.slotmanagement.dto.SlotListAdminResponseDto;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.exception.SmNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SlotAdminService {
    private final adminSlotManagementRepository adminSlotManagementRepository;

    @Transactional(readOnly = true)
    public SlotListAdminResponseDto getSlotSetting() {
        List<SlotManagement> slotManagements = adminSlotManagementRepository.findAllByOrderByCreatedAtDesc();
        List<SlotAdminDto> dtoList = new ArrayList<>();
        for (SlotManagement slot : slotManagements) {
            SlotAdminDto dto = new SlotAdminDto(slot);
            dtoList.add(dto);
        }
        return new SlotListAdminResponseDto(dtoList);
    }

    public void addSlotSetting(SlotCreateRequestDto requestDto) {
        SlotManagement slotManagement = new SlotManagement(requestDto);

        adminSlotManagementRepository.save(slotManagement);
    }
}
