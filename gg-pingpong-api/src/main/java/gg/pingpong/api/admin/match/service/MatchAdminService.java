package gg.pingpong.api.admin.match.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gg.admin.repo.manage.AdminSlotManagementsRepository;
import gg.admin.repo.match.RedisMatchTimeAdminRepository;
import gg.admin.repo.user.UserAdminRepository;
import gg.data.pingpong.manage.SlotManagement;
import gg.data.pingpong.match.RedisMatchUser;
import gg.data.pingpong.match.type.Option;
import gg.pingpong.api.admin.match.service.dto.MatchUser;
import gg.utils.exception.match.SlotNotFoundException;
import gg.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchAdminService {
	private final RedisMatchTimeAdminRepository redisMatchTimeAdminRepository;
	private final AdminSlotManagementsRepository adminSlotManagementsRepository;
	private final UserAdminRepository userAdminRepository;

	/**
	 * redis 매칭큐 조회
	 * @param option : BOTH, NORMAL, RANK (null일 경우 BOTH 조회)
	 * @return 매칭큐
	 */
	public Map<LocalDateTime, List<MatchUser>> getMatches(Option option) {
		Map<LocalDateTime, List<RedisMatchUser>> allEnrolledSlots = redisMatchTimeAdminRepository.getAllEnrolledSlots();
		Map<LocalDateTime, List<MatchUser>> response = new HashMap<>();

		for (Map.Entry<LocalDateTime, List<RedisMatchUser>> entry : allEnrolledSlots.entrySet()) {
			List<MatchUser> matchUsers = convertToMatchUser(entry.getValue());
			response.put(entry.getKey(), matchUsers);
		}

		if (option == null || Option.BOTH.equals(option)) {
			return response;
		}
		for (Map.Entry<LocalDateTime, List<MatchUser>> entry : response.entrySet()) {
			entry.getValue().removeIf(
				matchUser -> !Option.BOTH.equals(matchUser.getOption()) && !matchUser.getOption().equals(option));
		}
		return response;
	}

	/**
	 * @return gameInterval (분)
	 */
	public int getGameInterval() {
		SlotManagement slotManagement = adminSlotManagementsRepository.findCurrent(LocalDateTime.now())
			.orElseThrow(SlotNotFoundException::new);
		return slotManagement.getGameInterval();
	}

	/**
	 * RedisMatchUser -> MatchUser 변환
	 * @param redisMatchUsers 변환할 RedisMatchUser 리스트
	 * @return 변환된 MatchUser 리스트
	 */
	private List<MatchUser> convertToMatchUser(List<RedisMatchUser> redisMatchUsers) {
		return redisMatchUsers.stream()
			.map(redisMatchUser -> {
				String intraId = userAdminRepository.findById(redisMatchUser.getUserId())
					.orElseThrow(UserNotFoundException::new)
					.getIntraId();
				return MatchUser.of(redisMatchUser, intraId);
			})
			.collect(Collectors.toList());
	}
}
