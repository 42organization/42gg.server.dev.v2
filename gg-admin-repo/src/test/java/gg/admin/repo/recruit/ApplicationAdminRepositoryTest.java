package gg.admin.repo.recruit;

import java.util.ArrayList;
import java.util.List;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import gg.data.recruit.application.Application;
import gg.data.recruit.application.RecruitStatus;
import gg.data.recruit.application.ApplicationAnswerCheckList;
import gg.data.recruit.application.ApplicationAnswerText;
import gg.data.recruit.recruitment.CheckList;
import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.Recruitment;
import gg.data.recruit.recruitment.enums.InputType;
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

	@Nested
	@DisplayName("application by getRecruitmentApplications condition success test")
	class ApplicationByGetRecruitmentApplicationsCondition {
		Long wrongApplicationId;
		Long userId;
		Long recruitmentId;
		Long applicationId;
		Long questionId;
		Long checkListId1;
		Long checkListId2;
		Long applicationAnswerCheckListId;
		String search = "hello world";

		@BeforeEach
		public void init() {
			Recruitment recruitment = testDataUtils.createNewRecruitment();
			User user = testDataUtils.createNewUser();

			//target 1
			Application application = testDataUtils.createApplication(user, recruitment);
			Question question = testDataUtils.createNewQuestion(recruitment, InputType.SINGLE_CHECK, "dubby", 2);
			CheckList checkList = testDataUtils.createNewCheckList(question, "dd");
			ApplicationAnswerCheckList applicationAnswerCheckList = testDataUtils.createNewApplicationAnswerCheckList(
				application, question, checkList);

			//target 2
			Application application2 = testDataUtils.createApplication(user, recruitment);
			CheckList checkList2 = testDataUtils.createNewCheckList(question, "dd");
			ApplicationAnswerCheckList applicationAnswerCheckList2 = testDataUtils.createNewApplicationAnswerCheckList(
				application2, question, checkList2);

			//must not contain
			Application application3 = testDataUtils.createApplication(user, recruitment);
			CheckList checkList3 = testDataUtils.createNewCheckList(question, "dd");
			ApplicationAnswerCheckList applicationAnswerCheckList3 = testDataUtils.createNewApplicationAnswerCheckList(
				application, question, checkList3);

			// search target
			ApplicationAnswerText answerText = testDataUtils.createNewApplicationAnswerText(application, question,
				search);
			ApplicationAnswerText answerText2 = testDataUtils.createNewApplicationAnswerText(application2, question,
				"pp" + search + "pp");
			ApplicationAnswerText answerText3 = testDataUtils.createNewApplicationAnswerText(application3, question,
				"pp" + search);

			wrongApplicationId = application3.getId();
			userId = user.getId();
			recruitmentId = recruitment.getId();
			applicationId = application.getId();
			questionId = question.getId();
			checkListId1 = checkList.getId();
			checkListId2 = checkList2.getId();
			applicationAnswerCheckListId = applicationAnswerCheckList.getId();

			entityManager.flush();
			entityManager.clear();
		}

		@Nested
		@DisplayName("findByRecruitIdAndIsDeletedFalse")
		class findByRecruitIdAndIsDeletedFalse {

			@Test
			@DisplayName("조회 성공")
			void success() {
				//Arrange
				Pageable pageable = PageRequest.of(0, 10);

				// Act
				Page<Application> result = applicationAdminRepository.findByRecruitIdAndIsDeletedFalseOrderByIdDesc(
					recruitmentId,
					pageable);

				// Assert
				Assertions.assertThat(result.getContent().size()).isEqualTo(3);
			}
		}

		@Nested
		@DisplayName("FindAllByCheckList")
		class FindAllByCheckList {

			@Test
			@DisplayName("여러개의 조건 중 하나만 만족해도 조회가 성공")
			void success() {
				//Arrange
				List<Long> checkListTargetId = new ArrayList<>();
				checkListTargetId.add(checkListId1);
				checkListTargetId.add(checkListId2);
				Pageable pageable = PageRequest.of(0, 10);

				// Act
				Page<Application> result = applicationAdminRepository.findAllByCheckList(recruitmentId,
					questionId, checkListTargetId, pageable);

				// Assert
				Assertions.assertThat(result.getContent().size()).isEqualTo(2);
				for (Application entity : result.getContent()) {
					Assertions.assertThat(entity.getId()).isNotEqualTo(wrongApplicationId);
				}
			}
		}

		@Nested
		@DisplayName("findAllByContainSearch")
		class FindAllByContainSearch {

			@Test
			@DisplayName("search를 포함하면 조회 성공")
			void success() {
				//Arrange
				Pageable pageable = PageRequest.of(0, 10);

				// Act
				Page<Application> result = applicationAdminRepository.findAllByContainSearch(recruitmentId,
					questionId, search, pageable);

				// Assert
				Assertions.assertThat(result.getContent().size()).isEqualTo(3);
			}
		}

	}

}
