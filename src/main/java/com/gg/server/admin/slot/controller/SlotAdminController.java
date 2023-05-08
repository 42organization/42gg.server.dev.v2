package com.gg.server.admin.slot.controller;

import com.gg.server.admin.slot.dto.SlotAdminDto;
import com.gg.server.admin.slot.service.SlotAdminService;
import com.gg.server.global.exception.custom.BusinessException;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/slot")
public class SlotAdminController {
    private final SlotAdminService slotAdminService;

    @GetMapping
    public SlotAdminDto getSlotSetting(HttpResponse httpResponse) {
        SlotAdminDto responseDto = slotAdminService.getSlotSetting();
        if (responseDto == null) {
            throw new BusinessException("SN001");//널값인 경우
        }
        return responseDto;
    }

    @PutMapping
    public ResponseEntity modifySlotSetting(@Valid @RequestBody SlotAdminDto requestDto){
        slotAdminService.addSlotSetting(requestDto.getPastSlotTime(),
                requestDto.getFutureSlotTime(),
                requestDto.getInterval(),
                requestDto.getOpenMinute());
        return ResponseEntity.ok().build();
    }

}
