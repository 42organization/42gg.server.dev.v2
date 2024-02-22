package gg.pingpong.api.admin.coin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.pingpong.api.admin.coin.controller.request.CoinUpdateRequestDto;
import gg.pingpong.api.admin.coin.service.CoinAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("pingpong/admin/coin")
public class CoinAdminController {
	private final CoinAdminService coinAdminService;

	@PutMapping()
	public ResponseEntity updateUserCoin(@RequestBody CoinUpdateRequestDto coinUpdateRequestDto) {
		coinAdminService.updateUserCoin(coinUpdateRequestDto);
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}
}
