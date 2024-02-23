package gg.pingpong.api.admin.match.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gg.pingpong.data.match.RedisMatchUser;
import gg.pingpong.data.match.type.Option;
import gg.pingpong.repo.match.RedisMatchTimeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchAdminService {
	private final RedisMatchTimeRepository redisMatchTimeRepository;

	public Map<LocalDateTime, List<RedisMatchUser>> getMatches(Option option) {
		Map<LocalDateTime, List<RedisMatchUser>> allEnrolledSlots = redisMatchTimeRepository.getAllEnrolledSlots();
		if (option == null) {
			return allEnrolledSlots;
		}
		return allEnrolledSlots.entrySet().stream()
			.filter(entry -> entry.getValue().stream().anyMatch(user -> option.equals(user.getOption())))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
