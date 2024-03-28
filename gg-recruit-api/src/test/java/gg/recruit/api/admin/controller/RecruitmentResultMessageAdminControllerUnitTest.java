package gg.recruit.api.admin.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import gg.auth.config.AuthWebConfig;
import gg.data.recruit.manage.ResultMessage;
import gg.data.recruit.manage.enums.MessageType;
import gg.pingpong.api.global.config.WebConfig;
import gg.pingpong.api.global.security.config.SecurityConfig;
import gg.pingpong.api.global.security.jwt.utils.TokenAuthenticationFilter;
import gg.pingpong.api.global.utils.querytracker.LoggingInterceptor;
import gg.recruit.api.admin.service.RecruitmentResultMessageAdminService;
import gg.recruit.api.admin.service.dto.RecruitmentResultMessageDto;
import gg.utils.annotation.UnitTest;

@UnitTest
@WebMvcTest(controllers = RecruitmentResultMessageAdminController.class, excludeFilters = {
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class),
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthWebConfig.class),
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = TokenAuthenticationFilter.class),
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = LoggingInterceptor.class)})
class RecruitmentResultMessageAdminControllerUnitTest {

	@MockBean
	private RecruitmentResultMessageAdminService resultMessageAdminService;
	@Autowired
	private RecruitmentResultMessageAdminController resultMessageAdminController;

	@Nested
	@DisplayName("postResultMessage")
	class PostResultMessage {
		@Test
		@DisplayName("resultMessage 등록 성공")
		void postResultMessageSuccess() {
			//Arrange
			RecruitmentResultMessageDto dto = new RecruitmentResultMessageDto(MessageType.FAIL, "탈락");
			//Act
			ResponseEntity<Void> response = resultMessageAdminController.postResultMessage(dto);
			//Assert
			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		}

		@Test
		@DisplayName("유효하지 않은 dto는 ConstraintViolationException")
		void invalidArgument() {
			//Arrange
			List<RecruitmentResultMessageDto> listDto = new ArrayList<>();
			listDto.add(new RecruitmentResultMessageDto(MessageType.FAIL, null));
			listDto.add(new RecruitmentResultMessageDto(null, "fail"));
			listDto.add(new RecruitmentResultMessageDto(MessageType.FAIL, "f".repeat(ResultMessage.contentLimit + 1)));
			//Act
			//Assert
			for (RecruitmentResultMessageDto dto : listDto) {
				Assertions.assertThatExceptionOfType(ConstraintViolationException.class)
					.isThrownBy(() -> resultMessageAdminController.postResultMessage(dto));
			}
		}
	}
}
