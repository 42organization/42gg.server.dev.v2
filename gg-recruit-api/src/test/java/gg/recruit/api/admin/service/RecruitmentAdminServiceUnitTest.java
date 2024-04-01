package gg.recruit.api.admin.service;

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

import gg.admin.repo.recruit.RecruitmentAdminRepository;
import gg.data.recruit.recruitment.Recruitment;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.custom.NotExistException;

@UnitTest
class RecruitmentAdminServiceUnitTest {
	@InjectMocks
	private RecruitmentAdminService recruitmentAdminService;

	@Mock
	private RecruitmentAdminRepository recruitmentAdminRepository;

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
}
