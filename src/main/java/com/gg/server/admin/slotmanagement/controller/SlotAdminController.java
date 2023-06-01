package com.gg.server.admin.slotmanagement.controller;

import com.gg.server.admin.slotmanagement.dto.SlotCreateRequestDto;
import com.gg.server.admin.slotmanagement.dto.SlotListAdminResponseDto;
import com.gg.server.admin.slotmanagement.service.SlotAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/slot-management")
public class SlotAdminController {
    private final SlotAdminService slotAdminService;

    @GetMapping
    public SlotListAdminResponseDto getSlotSetting() {

        return slotAdminService.getSlotSetting();
    }

    @PostMapping
    public ResponseEntity addSlotSetting(@Valid @RequestBody SlotCreateRequestDto requestDto){
        slotAdminService.addSlotSetting(requestDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }

}
