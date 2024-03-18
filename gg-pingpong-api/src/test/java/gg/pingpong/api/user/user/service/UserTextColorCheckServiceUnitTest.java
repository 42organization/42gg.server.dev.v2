package gg.pingpong.api.user.user.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import gg.utils.annotation.UnitTest;

@UnitTest
class UserTextColorCheckServiceUnitTest {

	UserTextColorCheckService userTextColorCheckService = new UserTextColorCheckService();

	@Nested
	@DisplayName("check")
	class Check {
		@Test
		@DisplayName("textColor가 null일 경우 false 반환")
		void nullCheck() {
			//Arrange
			String textColor = null;

			//Act
			boolean check = userTextColorCheckService.check(textColor);

			//Assert
			Assertions.assertThat(check).isFalse();
		}

		@ParameterizedTest
		@DisplayName("7글자가 아닐경우 false 반환")
		@ValueSource(strings = {"", "#", "#1", "#12", "#123", "#12345", "#1234567"})
		void lengthCheck(String textColor) {
			//Arrange
			//Act
			boolean check = userTextColorCheckService.check(textColor);

			//Assert
			Assertions.assertThat(check).isFalse();
		}
	}

	@ParameterizedTest
	@DisplayName("첫 글자 # 아닐경우 false 반환")
	@ValueSource(strings = {"#,", "a#12345", "ㄱ#12345", "1#12345", "Z#12345", " #12345"})
	void startCheck(String textColor) {
		//Arrange
		//Act
		boolean check = userTextColorCheckService.check(textColor);

		//Assert
		Assertions.assertThat(check).isFalse();
	}

	@ParameterizedTest
	@DisplayName("정규식 실패할경우 false 반환")
	@ValueSource(strings = {"#12345G", "#gabcde", "#zABCDE", "#Ab3DEx"})
	void regexFail(String textColor) {
		//Arrange
		//Act
		boolean check = userTextColorCheckService.check(textColor);

		//Assert
		Assertions.assertThat(check).isFalse();
	}

	@ParameterizedTest
	@DisplayName("모든 조건을 통과할경우 true 반환")
	@ValueSource(strings = {"#023589", "#abcdef", "#ABCDEF", "#Ab3DeE"})
	void checkSuccess(String textColor) {
		//Arrange
		//Act
		boolean check = userTextColorCheckService.check(textColor);

		//Assert
		Assertions.assertThat(check).isTrue();
	}
}
