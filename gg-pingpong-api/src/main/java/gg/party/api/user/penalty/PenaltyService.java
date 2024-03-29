package gg.party.api.user.penalty;

import org.springframework.stereotype.Service;

import gg.auth.UserDto;
import gg.data.party.PartyPenalty;
import gg.party.api.user.penalty.controller.response.PenaltyResDto;
import gg.repo.party.PartyPenaltyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PenaltyService {
	public PartyPenaltyRepository partyPenaltyRepository;

	/**
	 * 현재 유저가 패널티 상태인지 조회
	 * @return 언제까지 패널티인지 DateTime으로 리턴
	 */
	public PenaltyResDto findIsPenalty(UserDto userDto) {
		PartyPenalty partyPenalty = partyPenaltyRepository.findTopByUserIdOrderByStartTimeDesc(userDto.getId());
		if (PartyPenalty.isOnPenalty(partyPenalty)) {
			return new PenaltyResDto(partyPenalty);
		} else {
			return new PenaltyResDto();
		}
	}

}
