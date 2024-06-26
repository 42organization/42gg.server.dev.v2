package gg.recruit.api.admin.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import gg.admin.repo.recruit.manage.RecruitResultMessageRepository;
import gg.data.recruit.manage.enums.MessageType;
import gg.recruit.api.admin.service.param.RecruitmentResultMessageParam;
import gg.utils.annotation.UnitTest;

@UnitTest
class RecruitmentResultMessageAdminServiceImplTest {
	@Mock
	RecruitResultMessageRepository recruitResultMessageRepository;

	@InjectMocks
	RecruitmentResultMessageAdminServiceImpl recruitmentResultMessageAdminService;

	RecruitmentResultMessageParam dto;

	@Nested
	@DisplayName("postResultMessage")
	class PostResultMessage {
		@Test
		@DisplayName("성공")
		void postResultMessageSuccess() {
			//Arrange
			dto = new RecruitmentResultMessageParam(MessageType.FAIL, "message");

			//Act
			//Assert
			recruitmentResultMessageAdminService.postResultMessage(dto);

		}
	}
}
