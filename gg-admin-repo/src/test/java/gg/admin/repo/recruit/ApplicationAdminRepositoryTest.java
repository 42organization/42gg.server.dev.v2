package gg.admin.repo.recruit;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import gg.data.recruit.application.Application;
import gg.data.recruit.recruitment.Recruitment;
import gg.data.user.User;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.annotation.UnitTest;

@IntegrationTest
@UnitTest
@Transactional
class ApplicationAdminRepositoryTest {
	@Autowired
	EntityManager entityManager;

	@Autowired
	ApplicationAdminRepository applicationAdminRepository;

	@Autowired
	TestDataUtils testDataUtils;

	@Nested
	class FindByIdAndRecruitId {
		@Test
		@DisplayName("조회 성공")
		void findSuccess() {
			//Arrange
			User user = testDataUtils.createNewUser();
			Recruitment recruitment = testDataUtils.createNewRecruitment();
			Application application = testDataUtils.createApplication(user, recruitment);
			entityManager.flush();
			Long recruitmentId = recruitment.getId();
			Long applicationId = application.getId();
			entityManager.clear();

			//Act
			Optional<Application> res = applicationAdminRepository.findByIdAndRecruitId(applicationId, recruitmentId);

			//Assert
			Assertions.assertThat(res.get()).isNotNull();
		}
	}
}
