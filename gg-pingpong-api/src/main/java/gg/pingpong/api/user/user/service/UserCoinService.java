package gg.pingpong.api.user.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.api.user.user.dto.CoinHistoryResponseDto;
import gg.pingpong.api.user.user.dto.UserCoinHistoryListResponseDto;
import gg.pingpong.api.user.user.dto.UserCoinResponseDto;
import gg.pingpong.data.store.CoinHistory;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.coin.CoinHistoryRepository;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.exception.user.UserNotFoundException;
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
