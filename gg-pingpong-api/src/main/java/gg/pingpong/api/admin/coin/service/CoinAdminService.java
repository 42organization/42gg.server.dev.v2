package gg.pingpong.api.admin.coin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.api.admin.coin.controller.request.CoinUpdateRequestDto;
import gg.pingpong.api.user.coin.service.CoinHistoryService;
import gg.pingpong.data.store.CoinHistory;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.exception.user.UserNotFoundException;
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
		coinHistoryService.addCoinHistory(new CoinHistory(user, coinUpdateRequestDto.getContent(),
			coinUpdateRequestDto.getChange()));
	}
}
