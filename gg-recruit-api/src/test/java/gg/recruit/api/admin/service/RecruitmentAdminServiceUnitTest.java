package gg.recruit.api.admin.service;

import static gg.data.recruit.application.enums.ApplicationStatus.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
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
import gg.data.recruit.recruitment.Recruitment;
import gg.data.user.User;
import gg.recruit.api.admin.service.dto.UpdateApplicationStatusDto;
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
			UpdateApplicationStatusDto dto = new UpdateApplicationStatusDto(FAIL, 1L, 1L);
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
			UpdateApplicationStatusDto dto = new UpdateApplicationStatusDto(FAIL, 1L, 1L);
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
			UpdateApplicationStatusDto dto = new UpdateApplicationStatusDto(
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
			UpdateApplicationStatusDto dto = new UpdateApplicationStatusDto(
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
