package gg.recruit.api.admin.controller;

import static org.mockito.ArgumentMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import gg.data.recruit.application.Application;
import gg.data.recruit.application.RecruitStatus;
import gg.data.recruit.application.enums.ApplicationStatus;
import gg.data.user.User;
import gg.recruit.api.WebMvcTestApplicationContext;
import gg.recruit.api.admin.controller.request.SetFinalApplicationStatusResultReqDto;
import gg.recruit.api.admin.controller.response.RecruitmentApplicantResultResponseDto;
import gg.recruit.api.admin.controller.response.RecruitmentApplicantResultsResponseDto;
import gg.recruit.api.admin.service.RecruitmentAdminService;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.custom.BusinessException;

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

	@Nested
	@DisplayName("getRecruitmentsApplicants")
	class GetRecruitmentsApplicants {
		@Test
		@DisplayName("등록된 지원서가 없어도 조회 결과는 null 이어서는 안된다")
		void successEmpty() {
			//Arrange
			ResponseEntity<RecruitmentApplicantResultsResponseDto> result;

			//Act
			result = recruitmentAdminController.getRecruitmentApplicantResults(1L);

			//Assert
			Assertions.assertThat(result).isNotNull();
			Assertions.assertThat(result.getBody()).isNotNull();
			Assertions.assertThat(result.getBody().getApplicationResults()).isNotNull();
			Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		}
	}

	@Test
	@DisplayName("모든 정보가 잘 들어가 있어야 한다")
	void successNotEmpty() {
		//Arrange
		Application application = Mockito.mock(Application.class);
		User user = Mockito.mock(User.class);
		RecruitStatus recruitStatus = Mockito.mock(RecruitStatus.class);
		ApplicationStatus status = ApplicationStatus.FAIL;

		Long applicationId = 1L;
		LocalDateTime interviewDate = LocalDateTime.now();
		String intraId = "dummy";

		Mockito.when(application.getId()).thenReturn(applicationId);
		Mockito.when(application.getUser()).thenReturn(user);
		Mockito.when(application.getRecruitStatus()).thenReturn(recruitStatus);
		Mockito.when(application.getStatus()).thenReturn(status);

		Mockito.when(user.getIntraId()).thenReturn(intraId);

		Mockito.when(recruitStatus.getInterviewDate()).thenReturn(interviewDate);

		List<Application> serviceResult = new ArrayList<>();
		serviceResult.add(application);

		Mockito.when(recruitmentAdminService.getRecruitmentApplicants(any(Long.class))).thenReturn(serviceResult);

		//Act
		RecruitmentApplicantResultResponseDto result = recruitmentAdminController
			.getRecruitmentApplicantResults(1L).getBody().getApplicationResults().get(0);

		//Assert
		Assertions.assertThat(result.getResult()).isEqualTo(status);
		Assertions.assertThat(result.getIntraId()).isEqualTo(intraId);
		Assertions.assertThat(result.getApplicationId()).isEqualTo(applicationId);
		Assertions.assertThat(result.getInterviewDate()).isEqualTo(interviewDate);

	}

	@Nested
	@DisplayName("getRecruitmentApplications")
	class GetRecruitmentApplications {
		@Test
		@DisplayName("long으로 파싱할 수 없는 유효하지 않은 checks의 경우 exceptions 발생")
		void invalidChecks() {
			//Arrange
			//Act
			//Assert
			Assertions.assertThatThrownBy(
					() -> recruitmentAdminController.getRecruitmentApplications(1L, null, "d,d,d", null,
						PageRequest.of(1, 10)))
				.isInstanceOf(BusinessException.class);

		}

		@Test
		@DisplayName("성공")
		@Disabled
		void success() {
			//Arrange
			//Act
			recruitmentAdminController.getRecruitmentApplications(1L, null, "1,2,3", null,
				PageRequest.of(1, 10));

			//Assert
		}
	}
}

