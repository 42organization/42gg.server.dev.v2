package gg.recruit.api.admin;

import java.time.LocalDateTime;

import gg.admin.repo.recruit.RecruitmentAdminRepository;
import gg.data.recruit.recruitment.Recruitment;
import lombok.RequiredArgsConstructor;

// @Component
@RequiredArgsConstructor
public class RecruitAdminMockData {
	private final RecruitmentAdminRepository recruitmentAdminRepository;

	public Recruitment createRecruitment() {
		Recruitment recruitments = new Recruitment("title", "contents", "generation",
			LocalDateTime.now(), LocalDateTime.now().plusDays(1));
		return recruitmentAdminRepository.save(recruitments);
	}

}
