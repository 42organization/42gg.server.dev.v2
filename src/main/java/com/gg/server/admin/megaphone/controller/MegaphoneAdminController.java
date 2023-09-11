package com.gg.server.admin.megaphone.controller;

import com.gg.server.admin.megaphone.dto.MegaphoneHistoryResponseDto;
import com.gg.server.admin.megaphone.service.MegaphoneAdminService;
import com.gg.server.global.dto.PageRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/admin/megaphones")
public class MegaphoneAdminController {
    private final MegaphoneAdminService megaphoneAdminService;

    @GetMapping("/history")
    public ResponseEntity<MegaphoneHistoryResponseDto> getMegaphoneHistory(@ModelAttribute @Valid PageRequestDto pageRequestDto,
                                                                           @RequestParam(required = false) String intraId) {
        Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1, pageRequestDto.getSize(),
                Sort.by("id").descending());
        if (intraId == null)
            return ResponseEntity.ok(megaphoneAdminService.getMegaphoneHistory(pageable));
        return ResponseEntity.ok(megaphoneAdminService.getMegaphoneHistoryByIntraId(intraId, pageable));

    }
}
