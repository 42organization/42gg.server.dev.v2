package gg.recruit.api.admin.service;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import gg.admin.repo.recruit.RecruitmentAdminRepository;
import gg.data.recruit.recruitment.Recruitment;
import gg.utils.annotation.UnitTest;

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
}
