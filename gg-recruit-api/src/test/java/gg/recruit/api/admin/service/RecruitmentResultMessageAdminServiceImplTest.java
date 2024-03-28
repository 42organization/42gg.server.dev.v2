package gg.recruit.api.admin.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import gg.data.recruit.manage.enums.MessageType;
import gg.recruit.api.admin.service.dto.RecruitmentResultMessageDto;
import gg.repo.recruit.user.manage.RecruitResultMessageRepository;
import gg.utils.annotation.UnitTest;

@UnitTest
class RecruitmentResultMessageAdminServiceImplTest {
	@Mock
	RecruitResultMessageRepository recruitResultMessageRepository;

	@InjectMocks
	RecruitmentResultMessageAdminServiceImpl recruitmentResultMessageAdminService;

	RecruitmentResultMessageDto dto;

	@Nested
	@DisplayName("postResultMessage")
	class postResultMessage {
		@Test
		@DisplayName("성공")
		void postResultMessageSuccess() {
			//Arrange
			dto = new RecruitmentResultMessageDto(MessageType.FAIL, "message");

			//Act
			//Assert
			recruitmentResultMessageAdminService.postResultMessage(dto);

		}
	}
}