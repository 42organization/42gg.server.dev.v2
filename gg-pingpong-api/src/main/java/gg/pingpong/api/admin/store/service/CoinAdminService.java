package gg.pingpong.api.admin.store.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.pingpong.store.CoinHistory;
import gg.data.user.User;
import gg.pingpong.api.admin.store.controller.request.CoinUpdateRequestDto;
import gg.pingpong.api.user.store.service.CoinHistoryService;
import gg.repo.user.UserRepository;
import gg.utils.exception.user.UserNotFoundException;
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
