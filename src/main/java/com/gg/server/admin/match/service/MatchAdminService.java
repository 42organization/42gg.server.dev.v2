package com.gg.server.admin.match.service;

import com.gg.server.domain.match.data.RedisMatchTimeRepository;
import com.gg.server.domain.match.data.RedisMatchUser;
import com.gg.server.domain.match.type.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
