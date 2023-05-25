package com.gg.server.admin.slotmanagement.controller;

import com.gg.server.admin.slotmanagement.dto.SlotAdminDto;
import com.gg.server.admin.slotmanagement.service.SlotAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/slot-management")
public class SlotAdminController {
    private final SlotAdminService slotAdminService;

    @GetMapping
    public SlotAdminDto getSlotSetting() {
        SlotAdminDto responseDto = slotAdminService.getSlotSetting();

        return responseDto;
    }

    @PutMapping
    public ResponseEntity modifySlotSetting(@Valid @RequestBody SlotAdminDto requestDto){
        slotAdminService.addSlotSetting(requestDto);
        return ResponseEntity.ok().build();
    }

}
