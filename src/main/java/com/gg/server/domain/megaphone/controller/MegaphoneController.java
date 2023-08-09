package com.gg.server.domain.megaphone.controller;

import com.gg.server.domain.megaphone.dto.MegaphoneUseRequestDto;
import com.gg.server.domain.megaphone.service.MegaphoneService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/megaphones")
public class MegaphoneController {
    private final MegaphoneService megaphoneService;

    @PostMapping()
    public ResponseEntity useMegaphone(@RequestBody @Valid MegaphoneUseRequestDto megaphoneUseRequestDto,
                                       @Parameter(hidden = true) @Login UserDto user) {
        megaphoneService.useMegaphone(megaphoneUseRequestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/receipt/{receiptId}")
    public ResponseEntity deleteMegaphone(@PathVariable Long receiptId,
                                          @Parameter(hidden = true) @Login UserDto user) {
        megaphoneService.deleteMegaphone(receiptId, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
