package gg.admin.repo.recruit;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
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
	@DisplayName("findByIdAndRecruitId")
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

	@Nested
	@DisplayName("findAllByRecruitmentIdWithUserAndRecruitStatusFetchJoin")
	class FindAllByRecruitmentIdWithUserAndRecruitStatusFetchJoin {
		@Test
		@DisplayName("조회 성공")
		void findSuccess() {
			//Arrange
			User user = testDataUtils.createNewUser();
			Recruitment recruitment = testDataUtils.createNewRecruitment();
			Application application1 = testDataUtils.createApplication(user, recruitment);
			entityManager.flush();
			Application application2 = testDataUtils.createApplication(user, recruitment);
			RecruitStatus recruitStatus = testDataUtils.createRecruitStatus(application2);
			entityManager.flush();

			Long recruitmentId = recruitment.getId();
			Long application1Id = application1.getId();
			Long application2Id = application2.getId();
			Long userId = user.getId();
			Long recruitStatusId = recruitStatus.getId();
			entityManager.clear();

			//Act
			List<Application> res;
			res = applicationAdminRepository
				.findAllByRecruitmentIdWithUserAndRecruitStatusFetchJoinOrderByIdDesc(recruitmentId);

			//Assert
			Assertions.assertThat(res.get(0).getId()).isEqualTo(application2Id);
			Assertions.assertThat(res.get(0).getUser().getId()).isEqualTo(userId);
			Assertions.assertThat(res.get(0).getRecruitStatus().getId()).isEqualTo(recruitStatusId);

			Assertions.assertThat(res.get(1).getId()).isEqualTo(application1Id);
			Assertions.assertThat(res.get(1).getUser().getId()).isEqualTo(userId);
			Assertions.assertThat(res.get(1).getRecruitStatus()).isNull();
		}
	}
}
