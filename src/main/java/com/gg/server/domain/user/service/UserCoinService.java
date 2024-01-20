package com.gg.server.domain.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.domain.coin.data.CoinHistory;
import com.gg.server.domain.coin.data.CoinHistoryRepository;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.CoinHistoryResponseDto;
import com.gg.server.domain.user.dto.UserCoinHistoryListResponseDto;
import com.gg.server.domain.user.dto.UserCoinResponseDto;
import com.gg.server.domain.user.exception.UserNotFoundException;

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
