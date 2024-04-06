package gg.recruit.api.admin.service;

import static gg.data.recruit.application.enums.ApplicationStatus.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import gg.admin.repo.recruit.ApplicationAdminRepository;
import gg.admin.repo.recruit.RecruitmentAdminRepository;
import gg.admin.repo.recruit.recruitment.RecruitStatusAdminRepository;
import gg.data.recruit.application.Application;
import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.Recruitment;
import gg.data.recruit.recruitment.enums.InputType;
import gg.data.user.User;
import gg.recruit.api.admin.service.param.FormParam;
import gg.recruit.api.admin.service.param.UpdateApplicationStatusParam;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.custom.DuplicationException;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.NotExistException;

@UnitTest
class RecruitmentAdminServiceUnitTest {
	@InjectMocks
	private RecruitmentAdminService recruitmentAdminService;

	@Mock
	private RecruitmentAdminRepository recruitmentAdminRepository;

	@Mock
	private ApplicationAdminRepository applicationAdminRepository;

	@Mock
	private RecruitStatusAdminRepository recruitStatusAdminRepository;

	@Test
	@DisplayName("공고 조회")
	void getAllRecruitments() {
		// given
		Slice<Recruitment> mock = mock(Slice.class);
		Pageable mockPageable = mock(Pageable.class);
		given(recruitmentAdminRepository.findAllByOrderByEndTimeDesc(mockPageable)).willReturn(mock);

		// when
		recruitmentAdminService.getAllRecruitments(mockPageable);

		// then
		verify(recruitmentAdminRepository, times(1)).findAllByOrderByEndTimeDesc(mockPageable);
	}

	@Nested
	@DisplayName("공고 수정")
	class UpdateRecruitment {
		LocalDateTime start = LocalDateTime.now().plusDays(1);
		Long recruitId = 1L;

		@Test
		@DisplayName("공고 수정 성공")
		void updateRecruitment() {
			// given
			Recruitment target = Recruitment.builder()
				.title("before-title")
				.contents("before-contents")
				.generation("before-5th")
				.startTime(start.plusDays(1))
				.endTime(start.plusDays(2))
				.build();
			Recruitment recruitment = Recruitment.builder().title("after-title")
				.contents("after-contents")
				.generation("after-6th")
				.startTime(start.plusDays(2))
				.endTime(start.plusDays(3))
				.build();
			List<FormParam> forms = List.of(
				FormParam.builder().question("question").inputType(InputType.TEXT).checkList(List.of()).build());
			given(recruitmentAdminRepository.findById(recruitId)).willReturn(Optional.of(target));

			// when
			recruitmentAdminService.updateRecruitment(recruitId, recruitment, forms);

			// then
			verify(recruitmentAdminRepository, times(1)).findById(recruitId);
			assertThat(recruitment.getTitle()).isEqualTo(target.getTitle());
			assertThat(recruitment.getContents()).isEqualTo(target.getContents());
			assertThat(recruitment.getGeneration()).isEqualTo(target.getGeneration());
			assertThat(recruitment.getStartTime()).isEqualTo(target.getStartTime());
			assertThat(recruitment.getEndTime()).isEqualTo(target.getEndTime());
			List<Question> questions = recruitment.getQuestions();
			List<Question> questions1 = target.getQuestions();
			for (int i = 0; i < questions.size(); i++) {
				assertThat(questions.get(i).getQuestion()).isEqualTo(questions1.get(i).getQuestion());
				assertThat(questions.get(i).getInputType()).isEqualTo(questions1.get(i).getInputType());
			}
		}

		@Test
		@DisplayName("공고가 이미 시작되어 수정 불가능한 경우 Forbidden Exception 발생")
		void updateRecruitmentFail() {
			// given
			Recruitment pastRecruitment = Recruitment.builder()
				.title("after-title")
				.contents("after-contents")
				.generation("after-5th")
				.startTime(LocalDateTime.of(2021, 1, 1, 0, 0))
				.endTime(LocalDateTime.of(2021, 1, 2, 0, 0))
				.build();
			given(recruitmentAdminRepository.findById(recruitId)).willReturn(Optional.of(pastRecruitment));
			Recruitment recruitment = mock(Recruitment.class);

			// when
			assertThatThrownBy(
				() -> recruitmentAdminService.updateRecruitment(recruitId, recruitment, List.of()))
				.isInstanceOf(ForbiddenException.class);

			// then
			verify(recruitmentAdminRepository, times(1)).findById(recruitId);
		}

		@Test
		@DisplayName("공고가 존재하지 않아 수정 불가능한 경우 NotExistException 발생")
		void updateRecruitmentNotExist() {
			// given
			given(recruitmentAdminRepository.findById(recruitId)).willReturn(Optional.empty());
			Recruitment recruitment = mock(Recruitment.class);

			// when
			assertThatThrownBy(
				() -> recruitmentAdminService.updateRecruitment(recruitId, recruitment, List.of()))
				.isInstanceOf(NotExistException.class);

			// then
			verify(recruitmentAdminRepository, times(1)).findById(recruitId);
		}
	}

	@Nested
	@DisplayName("공고 삭제")
	class DeleteRecruitment {
		@Test
		@DisplayName("공고가 존재할 경우 삭제 성공 (isDeleted = true)")
		void deleteRecruitment() {
			// given
			Long recruitmentId = 1L;
			LocalDateTime date = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
			Recruitment recruitment = new Recruitment("title", "contents", "5th", date, date.plusDays(1));
			given(recruitmentAdminRepository.findById(recruitmentId)).willReturn(Optional.of(recruitment));

			// when
			recruitmentAdminService.deleteRecruitment(recruitmentId);

			// then
			verify(recruitmentAdminRepository, times(1)).findById(recruitmentId);
			assertThat(recruitment.getIsDeleted()).isTrue();
		}

		@Test
		@DisplayName("공고가 존재하지 않을 경우 NotExistException 발생하고 삭제는 실패한다.")
		void deleteRecruitmentFail() {
			// given
			Long recruitmentId = 1L;
			given(recruitmentAdminRepository.findById(recruitmentId)).willReturn(Optional.empty());

			// when then
			assertThatThrownBy(() -> recruitmentAdminService.deleteRecruitment(recruitmentId))
				.isInstanceOf(NotExistException.class);

			// then
			verify(recruitmentAdminRepository, times(1)).findById(recruitmentId);
		}
	}

	@Nested
	@DisplayName("서류 전형 결과 등록 테스트")
	class UpdateFinalApplicationStatusAndNotification {
		@Test
		@DisplayName("서류전형 진행중인 지원서가 아닌 경우 실패 - ForbiddenException")
		void invalidApplicationStatus() {
			// given
			Application application = new Application(mock(User.class), mock(Recruitment.class));
			application.updateApplicationStatus(PROGRESS_INTERVIEW);
			UpdateApplicationStatusParam dto = new UpdateApplicationStatusParam(FAIL, 1L, 1L);
			given(
				applicationAdminRepository.findByIdAndRecruitId(dto.getApplicationId(), dto.getRecruitId()))
				.willReturn(Optional.of(application));

			// when, then
			assertThatThrownBy(() -> recruitmentAdminService.updateDocumentScreening(dto))
				.isInstanceOf(ForbiddenException.class);
		}

		@Test
		@DisplayName("서류전형 탈락 등록 성공")
		void validApplicationStatus() {
			// given
			Application application = new Application(mock(User.class), mock(Recruitment.class));
			UpdateApplicationStatusParam dto = new UpdateApplicationStatusParam(FAIL, 1L, 1L);
			given(
				applicationAdminRepository.findByIdAndRecruitId(dto.getApplicationId(), dto.getRecruitId()))
				.willReturn(Optional.of(application));

			// when
			recruitmentAdminService.updateDocumentScreening(dto);

			// then
			verify(applicationAdminRepository, times(1)).findByIdAndRecruitId(
				dto.getApplicationId(), dto.getRecruitId());
			assertThat(application.getStatus()).isEqualTo(FAIL);
		}

		@Test
		@DisplayName("면접날짜 등록 성공")
		void validInterviewDate() {
			// given
			Application application = new Application(mock(User.class), mock(Recruitment.class));
			UpdateApplicationStatusParam dto = new UpdateApplicationStatusParam(
				PROGRESS_INTERVIEW, 1L, 1L, LocalDateTime.of(2024, 1, 1, 0, 0, 0));
			given(
				applicationAdminRepository.findByIdAndRecruitId(dto.getApplicationId(), dto.getRecruitId()))
				.willReturn(Optional.of(application));
			given(recruitStatusAdminRepository.existsByRecruitmentIdAndInterviewDateBetween(
				dto.getRecruitId(), dto.getInterviewDate().minusMinutes(30), dto.getInterviewDate().plusMinutes(30)))
				.willReturn(false);

			// when
			recruitmentAdminService.updateDocumentScreening(dto);

			// then
			verify(recruitStatusAdminRepository, times(1)).existsByRecruitmentIdAndInterviewDateBetween(
				dto.getRecruitId(), dto.getInterviewDate().minusMinutes(30), dto.getInterviewDate().plusMinutes(30));
			assertThat(application.getStatus()).isEqualTo(PROGRESS_INTERVIEW);
		}

		@Test
		@DisplayName("면접 시간 중복으로 등록 실패 - DuplicationException")
		void invalidInterviewDate() {
			// given
			Application application = new Application(mock(User.class), mock(Recruitment.class));
			UpdateApplicationStatusParam dto = new UpdateApplicationStatusParam(
				PROGRESS_INTERVIEW, 1L, 1L, LocalDateTime.of(2024, 1, 1, 0, 0, 0));
			given(
				applicationAdminRepository.findByIdAndRecruitId(dto.getApplicationId(), dto.getRecruitId()))
				.willReturn(Optional.of(application));
			given(recruitStatusAdminRepository.existsByRecruitmentIdAndInterviewDateBetween(
				dto.getRecruitId(), dto.getInterviewDate().minusMinutes(30), dto.getInterviewDate().plusMinutes(30)))
				.willReturn(true);

			// when, then
			assertThatThrownBy(() -> recruitmentAdminService.updateDocumentScreening(dto))
				.isInstanceOf(DuplicationException.class);
		}

	}

}
