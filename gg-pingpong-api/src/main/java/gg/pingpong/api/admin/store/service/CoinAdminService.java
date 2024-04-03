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

	/**
	 * 유저의 코인 정보를 업데이트합니다.
	 * @param coinUpdateRequestDto 코인 업데이트에 필요한 Dto
	 * @throws UserNotFoundException 유저가 존재하지 않을 경우
	 */
	@Transactional
	public void updateUserCoin(CoinUpdateRequestDto coinUpdateRequestDto) {
		User user = userRepository.findByIntraId(coinUpdateRequestDto.getIntraId())
			.orElseThrow(UserNotFoundException::new);
		user.addGgCoin(coinUpdateRequestDto.getChange());
		coinHistoryService.addCoinHistory(new CoinHistory(user, coinUpdateRequestDto.getContent(),
			coinUpdateRequestDto.getChange()));
	}
}
