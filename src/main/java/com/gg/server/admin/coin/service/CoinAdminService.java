package com.gg.server.admin.coin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.admin.coin.dto.CoinUpdateRequestDto;
import com.gg.server.data.store.CoinHistory;
import com.gg.server.data.user.User;
import com.gg.server.domain.coin.service.CoinHistoryService;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoinAdminService {
	private final UserRepository userRepository;
	private final CoinHistoryService coinHistoryService;

	@Transactional
	public void updateUserCoin(CoinUpdateRequestDto coinUpdateRequestDto) {
		User user = userRepository.findByIntraId(coinUpdateRequestDto.getIntraId())
			.orElseThrow(UserNotFoundException::new);
		user.addGgCoin(coinUpdateRequestDto.getChange());
		coinHistoryService.addCoinHistory(
			new CoinHistory(user, coinUpdateRequestDto.getContent(), coinUpdateRequestDto.getChange()));
	}
}
