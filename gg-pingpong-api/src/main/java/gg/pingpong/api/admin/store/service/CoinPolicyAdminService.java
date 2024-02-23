package gg.pingpong.api.admin.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.admin.repo.store.CoinPolicyAdminRepository;
import gg.pingpong.admin.repo.user.UserAdminRepository;
import gg.pingpong.api.admin.store.controller.response.CoinPolicyAdminListResponseDto;
import gg.pingpong.api.admin.store.controller.response.CoinPolicyAdminResponseDto;
import gg.pingpong.api.admin.store.dto.CoinPolicyAdminAddDto;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.pingpong.data.manage.CoinPolicy;
import gg.pingpong.data.user.User;
import gg.pingpong.utils.exception.user.UserNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CoinPolicyAdminService {
	private final CoinPolicyAdminRepository coinPolicyAdminRepository;
	private final UserAdminRepository userAdminRepository;

	@Transactional(readOnly = true)
	public CoinPolicyAdminListResponseDto findAllCoinPolicy(Pageable pageable) {
		Page<CoinPolicy> allCoinPolicy = coinPolicyAdminRepository.findAll(pageable);
		Page<CoinPolicyAdminResponseDto> responseDtos = allCoinPolicy.map(CoinPolicyAdminResponseDto::new);

		return new CoinPolicyAdminListResponseDto(responseDtos.getContent(),
			responseDtos.getTotalPages());
	}

	@Transactional
	public void addCoinPolicy(UserDto userDto, CoinPolicyAdminAddDto addDto) {
		User user = userAdminRepository.findByIntraId(userDto.getIntraId()).orElseThrow(UserNotFoundException::new);

		CoinPolicy coinPolicy = CoinPolicy.from(user, addDto.getAttendance(), addDto.getNormal(),
			addDto.getRankWin(), addDto.getRankLose());
		coinPolicyAdminRepository.save(coinPolicy);
	}
}
