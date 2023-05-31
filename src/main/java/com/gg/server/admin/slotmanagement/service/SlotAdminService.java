package com.gg.server.admin.slotmanagement.service;

import com.gg.server.admin.slotmanagement.data.adminSlotManagementRepository;
import com.gg.server.admin.slotmanagement.dto.SlotAdminDto;
import com.gg.server.admin.slotmanagement.dto.SlotCreateRequestDto;
import com.gg.server.admin.slotmanagement.dto.SlotListAdminResponseDto;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.exception.SlotManagementForbiddenException;
import com.gg.server.domain.slotmanagement.exception.SlotManagementNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
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

    @Transactional
    public void addSlotSetting(SlotCreateRequestDto requestDto) {
        updateNowSlotManagementEndTime(requestDto.getStartTime());
        SlotManagement slotManagement = new SlotManagement(requestDto);

        adminSlotManagementRepository.save(slotManagement);
    }

    private void updateNowSlotManagementEndTime(LocalDateTime endTime){
        SlotManagement nowSlotManagement = adminSlotManagementRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new SlotManagementNotFoundException());
        LocalDateTime nowFutureSlotTime = LocalDateTime.now().plusHours(nowSlotManagement.getFutureSlotTime());

        if (nowFutureSlotTime.isAfter(endTime))
            throw new SlotManagementForbiddenException();

        nowSlotManagement.updateEndTime(endTime);
    }
}
