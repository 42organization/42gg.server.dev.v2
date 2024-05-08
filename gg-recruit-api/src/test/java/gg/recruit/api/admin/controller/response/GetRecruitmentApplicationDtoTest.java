package gg.recruit.api.admin.controller.response;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.recruit.ApplicationAdminRepository;
import gg.data.recruit.application.Application;
import gg.data.recruit.application.enums.ApplicationStatus;
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
class GetRecruitmentApplicationDtoTest {
	@Autowired
	EntityManager entityManager;

	@Autowired
	ApplicationAdminRepository applicationAdminRepository;

	@Autowired
	TestDataUtils testDataUtils;

	@Nested
	@DisplayName("MapperTest")
	class MapperTest {

		@Test
		@DisplayName("mapper에 조건에 맞게 모든 요소가 들어가 있어야 한다")
		void success() {
			String search = "hello world";

			Recruitment recruitment = testDataUtils.createNewRecruitment();
			User user = testDataUtils.createNewUser();

			Application application = testDataUtils.createApplication(user, recruitment);
			Question question = testDataUtils.createNewQuestion(recruitment, InputType.MULTI_CHECK, "multi", 2);
			CheckList checkList = testDataUtils.createNewCheckList(question, "dd");
			CheckList checkList2 = testDataUtils.createNewCheckList(question, "dd");
			testDataUtils.createNewApplicationAnswerCheckList(application, question, checkList);
			testDataUtils.createNewApplicationAnswerCheckList(application, question, checkList2);

			Question question2 = testDataUtils.createNewQuestion(recruitment, InputType.SINGLE_CHECK, "single", 2);
			testDataUtils.createNewApplicationAnswerCheckList(application, question2, checkList);

			Question question3 = testDataUtils.createNewQuestion(recruitment, InputType.TEXT, "text", 2);
			testDataUtils.createNewApplicationAnswerText(application, question3, search);

			Long recruitmentId = recruitment.getId();
			Long applicationId = application.getId();
			String intraId = user.getIntraId();
			ApplicationStatus status = application.getStatus();

			entityManager.flush();
			entityManager.clear();

			//Arrange
			Pageable pageable = PageRequest.of(0, 10);
			Page<Application> all = applicationAdminRepository.findByRecruitIdAndIsDeletedFalseOrderByIdDesc(
				recruitmentId, pageable);
			Application applicationResult = all.getContent().get(0);
			GetRecruitmentApplicationDto dto = GetRecruitmentApplicationDto.MapStruct.INSTANCE.entityToDto(
				applicationResult);

			// Assert
			Assertions.assertThat(dto.getApplicationId()).isEqualTo(applicationId);
			Assertions.assertThat(dto.getIntraId()).isEqualTo(intraId);
			Assertions.assertThat(dto.getStatus()).isEqualTo(status);
			Assertions.assertThat(dto.getForms().size()).isEqualTo(3);
			for (GetRecruitmentApplicationDto.Form form : dto.getForms()) {
				if (form.getInputType().equals(InputType.TEXT)) {
					Assertions.assertThat(form.getCheckedList().size()).isEqualTo(0);
					Assertions.assertThat(form.getAnswer()).isEqualTo(search);
				}
				if (form.getInputType().equals(InputType.SINGLE_CHECK)) {
					Assertions.assertThat(form.getCheckedList().size()).isEqualTo(1);
					Assertions.assertThat(form.getAnswer()).isNull();
				}
				if (form.getInputType().equals(InputType.MULTI_CHECK)) {
					Assertions.assertThat(form.getCheckedList().size()).isEqualTo(2);
					Assertions.assertThat(form.getAnswer()).isNull();
				}
			}
		}
	}
}
