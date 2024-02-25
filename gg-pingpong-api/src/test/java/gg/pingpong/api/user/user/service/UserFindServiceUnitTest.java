package gg.pingpong.api.user.user.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import gg.pingpong.api.user.season.service.SeasonFindService;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.rank.redis.RankRedisRepository;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.annotation.UnitTest;
import gg.pingpong.utils.exception.user.UserNotFoundException;

@UnitTest
class UserFindServiceUnitTest {
	@Mock
	UserRepository userRepository;
	@Mock
	SeasonFindService seasonFindService;
	@Mock
	RankRedisRepository rankRedisRepository;

	@InjectMocks
	UserFindService userFindService;

	@Nested
	@DisplayName("FindUserById")
	class FindUserById {
		@Test
		@DisplayName("유저 조회 성공")
		void userExist() {
			//Arrange
			User user = Mockito.mock(User.class);
			Long id = 1L;
			Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

			//Act
			User findUser = userFindService.findUserById(1L);

			//Assert
			Assertions.assertThat(user).isEqualTo(findUser);
		}

		@Test
		@DisplayName("유저 조회 실패 후 UserNotFoundException 반환")
		void userNotFound() {
			//Arrange
			Mockito.when(userRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());

			//Act, Assert
			assertThatThrownBy(() -> userFindService.findUserById(1L))
				.isInstanceOf(UserNotFoundException.class);
		}
	}

	@Nested
	@DisplayName("FindByIntraId")
	class FindByIntraId {
		@Test
		@DisplayName("유저 조회 성공")
		void userExist() {
			//Arrange
			User user = Mockito.mock(User.class);
			String intraId = "dummy";
			Mockito.when(userRepository.findByIntraId(intraId)).thenReturn(Optional.of(user));

			//Act
			User findUser = userFindService.findByIntraId(intraId);

			//Assert
			Assertions.assertThat(user).isEqualTo(findUser);
		}

		@Test
		@DisplayName("유저 조회 실패 후 UserNotFoundException 반환")
		void userNotFound() {
			//Arrange
			String intraId = "dummy";
			Mockito.when(userRepository.findByIntraId(Mockito.any(String.class))).thenReturn(Optional.empty());

			//Act, Assert
			assertThatThrownBy(() -> userFindService.findByIntraId(intraId))
				.isInstanceOf(UserNotFoundException.class);
		}
	}
}
