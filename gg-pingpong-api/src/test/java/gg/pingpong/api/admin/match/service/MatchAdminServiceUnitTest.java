package gg.pingpong.api.admin.match.service;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import gg.admin.repo.match.RedisMatchTimeAdminRepository;
import gg.admin.repo.user.UserAdminRepository;
import gg.data.pingpong.match.RedisMatchUser;
import gg.data.pingpong.match.type.Option;
import gg.data.user.User;
import gg.pingpong.api.admin.match.service.dto.MatchUser;
import gg.utils.annotation.UnitTest;

@UnitTest
public class MatchAdminServiceUnitTest {
	@InjectMocks
	private MatchAdminService matchAdminService;

	@Mock
	private RedisMatchTimeAdminRepository redisMatchTimeAdminRepository;

	@Mock
	private UserAdminRepository userAdminRepository;

	@ParameterizedTest
	@MethodSource("parameterProvider")
	@DisplayName("매칭큐 데이터를 Option(BOTH, RANK, NORMAL, null)에 따라 필터링하여 반환한다.")
	public void testGetMatches(Map<LocalDateTime, List<RedisMatchUser>> allEnrolledSlots, Option option) {
		// given
		given(redisMatchTimeAdminRepository.getAllEnrolledSlots()).willReturn(allEnrolledSlots);
		given(userAdminRepository.findById(anyLong()))
			.willReturn(Optional.of(User.builder().intraId("user").build()));

		// when
		Map<LocalDateTime, List<MatchUser>> response = matchAdminService.getMatches(option);

		// then
		if (option == null || option == Option.BOTH) {
			assertThat(response.size(), is(allEnrolledSlots.size()));
			return;
		}
		for (Map.Entry<LocalDateTime, List<MatchUser>> entry : response.entrySet()) {
			for (MatchUser matchUser : entry.getValue()) {
				assertThat(matchUser.getOption(), either(is(option)).or(is(Option.BOTH)));
			}
		}
	}

	private static Stream<Arguments> parameterProvider() {
		LocalDateTime localDateTime1 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
		LocalDateTime localDateTime2 = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
		RedisMatchUser user1 = new RedisMatchUser(1L, 210, Option.NORMAL);
		RedisMatchUser user2 = new RedisMatchUser(2L, 100, Option.BOTH);
		RedisMatchUser user3 = new RedisMatchUser(3L, 150, Option.RANK);

		return Stream.of(
			Arguments.arguments(Map.of(localDateTime1, Arrays.asList(user1, user2, user3),
					localDateTime2, Arrays.asList(user1, user2, user3)),
				Option.RANK),
			Arguments.arguments(Map.of(localDateTime1, Arrays.asList(user1, user2, user3)), Option.RANK),
			Arguments.arguments(Map.of(localDateTime1, Arrays.asList(user1, user2, user3)), Option.NORMAL),
			Arguments.arguments(Map.of(localDateTime1, Arrays.asList(user1, user2, user3)), Option.NORMAL)
		);
	}

}
