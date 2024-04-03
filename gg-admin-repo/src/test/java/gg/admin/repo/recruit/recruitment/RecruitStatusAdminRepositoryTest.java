package gg.admin.repo.recruit.recruitment;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import gg.data.recruit.application.Application;
import gg.data.recruit.application.RecruitStatus;
import gg.data.recruit.recruitment.Recruitment;
import gg.data.user.User;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.annotation.UnitTest;

@IntegrationTest
@Transactional
@UnitTest
class RecruitStatusAdminRepositoryTest {
	@Autowired
	RecruitStatusAdminRepository recruitStatusAdminRepository;

	@Autowired
	EntityManager entityManager;

	@Autowired
	TestDataUtils testDataUtils;

	@Nested
	@DisplayName("findFirstByRecruitmentIdAndInterviewDateBetween")
	class FindFirstByRecruitmentIdAndInterviewDateBetween {
		@Test
		@DisplayName("시간RecruitStatus 조회 성공")
		void findSuccess() {
			// Arrange
			int minutes = 30;
			LocalDateTime startTime = LocalDateTime.of(2021, 1, 1, 0, 0);
			LocalDateTime endTime = startTime.plusMinutes(minutes);

			User user = testDataUtils.createNewUser();
			Recruitment recruitment = testDataUtils.createNewRecruitment();
			Application application = testDataUtils.createApplication(user, recruitment);
			RecruitStatus recruitStatus = testDataUtils.createRecruitStatus(application, startTime);
			entityManager.flush();
			Long recruitmentId = recruitment.getId();

			// Act
			boolean exist = recruitStatusAdminRepository.existsByRecruitmentIdAndInterviewDateBetween(
				recruitmentId, startTime, endTime);

			// Assert
			assertThat(exist).isTrue();
		}

		@Test
		@DisplayName("여러개 RecruitStatus에서도 1개만 찾는다.")
		void findFirst() {
			// Arrange
			int minutes = 30;
			LocalDateTime startTime = LocalDateTime.of(2021, 1, 1, 0, 0);
			LocalDateTime endTime = startTime.plusMinutes(minutes);

			User user = testDataUtils.createNewUser();
			Recruitment recruitment = testDataUtils.createNewRecruitment();
			Application application1 = testDataUtils.createApplication(user, recruitment);
			Application application2 = testDataUtils.createApplication(user, recruitment);
			RecruitStatus recruitStatus1 = testDataUtils.createRecruitStatus(application1, startTime);
			RecruitStatus recruitStatus2 = testDataUtils.createRecruitStatus(application2, startTime.plusMinutes(10));
			entityManager.flush();
			Long recruitmentId = recruitment.getId();

			// Act
			boolean exist = recruitStatusAdminRepository.existsByRecruitmentIdAndInterviewDateBetween(
				recruitmentId, startTime, endTime);

			// Assert
			assertThat(exist).isTrue();
		}
	}
}
