package gg.pingpong.api.user.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.pingpong.store.CoinHistory;
import gg.data.user.User;
import gg.pingpong.api.user.user.controller.response.CoinHistoryResponseDto;
import gg.pingpong.api.user.user.controller.response.UserCoinHistoryListResponseDto;
import gg.pingpong.api.user.user.controller.response.UserCoinResponseDto;
import gg.repo.store.CoinHistoryRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserCoinService {
	private final UserRepository userRepository;
	private final CoinHistoryRepository coinHistoryRepository;

	@Transactional(readOnly = true)
	public UserCoinResponseDto getUserCoin(String intraId) {
		int userCoin = userRepository.findByIntraId(intraId).orElseThrow(() -> new UserNotFoundException()).getGgCoin();

		return new UserCoinResponseDto(userCoin);
	}

	@Transactional(readOnly = true)
	public UserCoinHistoryListResponseDto getUserCoinHistory(Pageable pageable, String intraId) {
		User user = userRepository.findByIntraId(intraId).orElseThrow(UserNotFoundException::new);

		Page<CoinHistory> coinHistories = coinHistoryRepository.findAllByUserOrderByIdDesc(user, pageable);
		Page<CoinHistoryResponseDto> coinHistoryResponseDtos = coinHistories.map(CoinHistoryResponseDto::new);
		UserCoinHistoryListResponseDto responseDto = new UserCoinHistoryListResponseDto(
			coinHistoryResponseDtos.getContent(),
			coinHistoryResponseDtos.getTotalPages());

		return responseDto;
	}
}
