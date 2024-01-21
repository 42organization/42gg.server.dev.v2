package com.gg.server.domain.megaphone.controller;

import com.gg.server.domain.megaphone.dto.MegaphoneDetailResponseDto;
import com.gg.server.domain.megaphone.dto.MegaphoneTodayListResponseDto;
import com.gg.server.domain.megaphone.dto.MegaphoneUseRequestDto;
import com.gg.server.domain.megaphone.service.MegaphoneService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/megaphones")
public class MegaphoneController {
    private final MegaphoneService megaphoneService;

    @PostMapping()
    public ResponseEntity<Void> useMegaphone(@RequestBody @Valid MegaphoneUseRequestDto megaphoneUseRequestDto,
                                       @Parameter(hidden = true) @Login UserDto user) {
        megaphoneService.useMegaphone(megaphoneUseRequestDto, user, LocalTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{megaphoneId}")
    public ResponseEntity<Void> deleteMegaphone(@PathVariable Long megaphoneId,
                                          @Parameter(hidden = true) @Login UserDto user) {
        megaphoneService.deleteMegaphone(megaphoneId, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/receipt/{receiptId}")
    public ResponseEntity<MegaphoneDetailResponseDto> getMegaphoneDetail(@PathVariable Long receiptId, @Parameter(hidden = true) @Login UserDto user) {
        return ResponseEntity.ok(megaphoneService.getMegaphoneDetail(receiptId, user));
    }

    @GetMapping()
    public ResponseEntity<List<MegaphoneTodayListResponseDto>> getMegaphoneTodayList(@Parameter(hidden = true) @Login UserDto user) {
        return ResponseEntity.ok(megaphoneService.getMegaphoneTodayList());
    }
}
