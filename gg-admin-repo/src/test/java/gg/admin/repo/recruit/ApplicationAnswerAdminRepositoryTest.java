package gg.admin.repo.recruit;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import gg.data.recruit.application.Application;
import gg.data.recruit.application.ApplicationAnswerCheckList;
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
class ApplicationAnswerAdminRepositoryTest {
	@Autowired
	EntityManager entityManager;

	@Autowired
	ApplicationAnswerAdminRepository applicationAnswerAdminRepository;

	@Autowired
	TestDataUtils testDataUtils;

	@Nested
	@DisplayName("FindAllByCheckList")
	class FindAllByCheckList {
		@Test
		@DisplayName("여러개의 조건 중 하나만 만족해도 조회가 성공")
		void success() {
			//Arrange
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
				application, question, checkList2);

			//must not search
			Application application3 = testDataUtils.createApplication(user, recruitment);
			CheckList checkList3 = testDataUtils.createNewCheckList(question, "dd");
			ApplicationAnswerCheckList applicationAnswerCheckList3 = testDataUtils.createNewApplicationAnswerCheckList(
				application, question, checkList3);
			Long wrongApplicationId = application3.getId();

			Long userId = user.getId();
			Long recruitmentId = recruitment.getId();
			Long applicationId = application.getId();
			Long questionId = question.getId();
			Long checkListId1 = checkList.getId();
			Long checkListId2 = checkList2.getId();
			Long applicationAnswerCheckListId = applicationAnswerCheckList.getId();

			entityManager.flush();
			entityManager.clear();

			List<Long> checkListTargetId = new ArrayList<>();
			checkListTargetId.add(checkListId1);
			checkListTargetId.add(checkListId2);
			Pageable pageable = PageRequest.of(0, 10, Sort.by(new Sort.Order(Sort.Direction.DESC, "id")));

			//Act
			Page<ApplicationAnswerCheckList> result = applicationAnswerAdminRepository.findAllByCheckList(recruitmentId,
				questionId, checkListTargetId, pageable);

			//Assert
			Assertions.assertThat(result.getContent().size()).isEqualTo(2);
			for (ApplicationAnswerCheckList entity : result.getContent()) {
				Assertions.assertThat(entity.getApplication().getId()).isNotEqualTo(wrongApplicationId);
			}
		}
	}
}
