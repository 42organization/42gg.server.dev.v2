package gg.pingpong.api.admin.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.store.CoinPolicyAdminRepository;
import gg.admin.repo.user.UserAdminRepository;
import gg.auth.UserDto;
import gg.data.pingpong.store.CoinPolicy;
import gg.data.user.User;
import gg.pingpong.api.admin.store.controller.response.CoinPolicyAdminListResponseDto;
import gg.pingpong.api.admin.store.controller.response.CoinPolicyAdminResponseDto;
import gg.pingpong.api.admin.store.dto.CoinPolicyAdminAddDto;
import gg.utils.exception.user.UserNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CoinPolicyAdminService {
	private final CoinPolicyAdminRepository coinPolicyAdminRepository;
	private final UserAdminRepository userAdminRepository;

	/**
	 * 코인 정책을 페이지별로 조회합니다.
	 * @param pageable
	 * @return 코인 정책 리스트 응답 Dto
	 */
	@Transactional(readOnly = true)
	public CoinPolicyAdminListResponseDto findAllCoinPolicy(Pageable pageable) {
		Page<CoinPolicy> allCoinPolicy = coinPolicyAdminRepository.findAll(pageable);
		Page<CoinPolicyAdminResponseDto> responseDtos = allCoinPolicy.map(CoinPolicyAdminResponseDto::new);

		return new CoinPolicyAdminListResponseDto(responseDtos.getContent(),
			responseDtos.getTotalPages());
	}

	/**
	 * 새로운 코인 정책을 추가합니다.
	 * @param userDto 유저 Dto
	 * @param addDto 새로 추가 할 코인 정책 Dto
	 * @exception UserNotFoundException 유저가 존재하지 않을 경우
	 */
	@Transactional
	public void addCoinPolicy(UserDto userDto, CoinPolicyAdminAddDto addDto) {
		User user = userAdminRepository.findByIntraId(userDto.getIntraId()).orElseThrow(UserNotFoundException::new);

		CoinPolicy coinPolicy = CoinPolicy.from(user, addDto.getAttendance(), addDto.getNormal(),
			addDto.getRankWin(), addDto.getRankLose());
		coinPolicyAdminRepository.save(coinPolicy);
	}
}
