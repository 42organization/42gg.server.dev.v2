package com.gg.server.admin.coin.controller;

import com.gg.server.admin.coin.dto.CoinUpdateRequestDto;
import com.gg.server.admin.coin.service.CoinAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("pingpong/admin/coin")
public class CoinAdminController {
    private final CoinAdminService coinAdminService;

    @PutMapping(value = "")
    public ResponseEntity updateUserCoin(@RequestBody CoinUpdateRequestDto coinUpdateRequestDto) {
        coinAdminService.updateUserCoin(coinUpdateRequestDto);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
