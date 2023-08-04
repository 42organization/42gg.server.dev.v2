package com.gg.server.admin.coin.controller;

import com.gg.server.admin.coin.dto.CoinPolicyAdminAddDto;
import com.gg.server.admin.coin.dto.CoinPolicyAdminListResponseDto;
import com.gg.server.admin.coin.service.CoinPolicyAdminService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.dto.PageRequestDto;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("pingpong/admin")
@Validated
public class CoinPolicyAdminController {
    private final CoinPolicyAdminService coinPolicyAdminService;

    @GetMapping("/coinpolicy")
    public ResponseEntity<CoinPolicyAdminListResponseDto> getCoinPolicyList(@ModelAttribute @Valid PageRequestDto coReq){
        Pageable pageable = PageRequest.of(coReq.getPage() - 1, coReq.getSize(), Sort.by("createdAt").descending());

        return ResponseEntity.ok()
                .body(coinPolicyAdminService.findAllCoinPolicy(pageable));
    }

    @PostMapping("/coinpolicy")
    public ResponseEntity addCoinPolicy(@Parameter(hidden = true) @Login UserDto userDto, @Valid @RequestBody CoinPolicyAdminAddDto addDto){

        coinPolicyAdminService.addCoinPolicy(userDto, addDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }

}
