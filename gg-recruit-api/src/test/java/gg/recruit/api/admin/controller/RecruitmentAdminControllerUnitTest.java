package gg.recruit.api.admin.controller;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import gg.data.recruit.application.enums.ApplicationStatus;
import gg.recruit.api.WebMvcTestApplicationContext;
import gg.recruit.api.admin.controller.request.SetFinalApplicationStatusResultReqDto;
import gg.recruit.api.admin.service.RecruitmentAdminService;
import gg.utils.annotation.UnitTest;

@UnitTest
@WebMvcTest(controllers = RecruitmentAdminController.class)
@ActiveProfiles("test-mvc")
@ContextConfiguration(classes = WebMvcTestApplicationContext.class)
class RecruitmentAdminControllerUnitTest {

	@MockBean
	private RecruitmentAdminService recruitmentAdminService;
	@Autowired
	private RecruitmentAdminController recruitmentAdminController;

	@Nested
	class SetFinalApplicationStatusResult {
		@ParameterizedTest
		@DisplayName("PASS, FAIL 파라미터 전달 시 최종 결과 등록 성공")
		@EnumSource(value = ApplicationStatus.class, mode = EnumSource.Mode.INCLUDE, names = {"PASS", "FAIL"})
		void setResultSuccess(ApplicationStatus status) {
			//Arrange
			SetFinalApplicationStatusResultReqDto reqDto = new SetFinalApplicationStatusResultReqDto(status);

			//Act
			ResponseEntity<Void> response = recruitmentAdminController.setFinalApplicationStatusResult(1L, 1L, reqDto);

			//Assert
			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		}

		@ParameterizedTest
		@DisplayName("PASS, FAIL를 제외한 status는 허용되지 않는다")
		@EnumSource(value = ApplicationStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"PASS", "FAIL"})
		void invalidStatus(ApplicationStatus status) {
			//Arrange
			SetFinalApplicationStatusResultReqDto reqDto = new SetFinalApplicationStatusResultReqDto(status);

			//Act
			//Assert
			Assertions.assertThatThrownBy(
					() -> recruitmentAdminController.setFinalApplicationStatusResult(1L, 1L, reqDto))
				.isInstanceOf(ConstraintViolationException.class)
				.hasMessageContaining(SetFinalApplicationStatusResultReqDto.MUST_FINAL_STATUS);
		}

		@Test
		@DisplayName("status null은 허용되지 않는다")
		void invalidStatusNull() {
			//Arrange
			SetFinalApplicationStatusResultReqDto reqDto = new SetFinalApplicationStatusResultReqDto(null);

			//Act
			//Assert
			Assertions.assertThatThrownBy(
					() -> recruitmentAdminController.setFinalApplicationStatusResult(1L, 1L, reqDto))
				.isInstanceOf(ConstraintViolationException.class)
				.hasMessageContaining(SetFinalApplicationStatusResultReqDto.MUST_FINAL_STATUS);
		}

	}
}

