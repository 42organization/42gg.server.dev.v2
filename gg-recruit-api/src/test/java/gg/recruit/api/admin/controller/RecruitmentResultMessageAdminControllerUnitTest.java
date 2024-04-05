package gg.recruit.api.admin.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import gg.data.recruit.manage.ResultMessage;
import gg.data.recruit.manage.enums.MessageType;
import gg.recruit.api.WebMvcTestApplicationContext;
import gg.recruit.api.admin.controller.response.GetRecruitmentResultMessagesResponseDto;
import gg.recruit.api.admin.service.RecruitmentResultMessageAdminService;
import gg.recruit.api.admin.service.param.RecruitmentResultMessageParam;
import gg.utils.annotation.UnitTest;

@UnitTest
@WebMvcTest(controllers = RecruitmentResultMessageAdminController.class)
@ActiveProfiles("test-mvc")
@ContextConfiguration(classes = WebMvcTestApplicationContext.class)
class RecruitmentResultMessageAdminControllerUnitTest {

	@MockBean
	private RecruitmentResultMessageAdminService resultMessageAdminService;
	@Autowired
	private RecruitmentResultMessageAdminController resultMessageAdminController;

	@Nested
	@DisplayName("postResultMessage")
	class PostResultMessage {
		@Test
		@DisplayName("resultMessage 등록 성공")
		void postResultMessageSuccess() {
			//Arrange
			RecruitmentResultMessageParam dto = new RecruitmentResultMessageParam(MessageType.FAIL, "탈락");
			//Act
			ResponseEntity<Void> response = resultMessageAdminController.postResultMessage(dto);
			//Assert
			Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		}

		@Test
		@DisplayName("유효하지 않은 dto는 ConstraintViolationException")
		void invalidArgument() {
			//Arrange
			List<RecruitmentResultMessageParam> listDto = new ArrayList<>();
			listDto.add(new RecruitmentResultMessageParam(MessageType.FAIL, null));
			listDto.add(new RecruitmentResultMessageParam(null, "fail"));
			listDto.add(
				new RecruitmentResultMessageParam(MessageType.FAIL, "f".repeat(ResultMessage.contentLimit + 1)));
			//Act
			//Assert
			for (RecruitmentResultMessageParam dto : listDto) {
				Assertions.assertThatExceptionOfType(ConstraintViolationException.class)
					.isThrownBy(
						() -> resultMessageAdminController.postResultMessage(dto));
			}
		}
	}

	@Nested
	@DisplayName("getResultMessage")
	class GetResultMessage {

		@Test
		@DisplayName("등록된 결과가 없을때 조회 결과는 null이어서는 안된다")
		void successEmpty() {
			//Arrange
			ResponseEntity<GetRecruitmentResultMessagesResponseDto> resultMessages;

			//Act
			resultMessages = resultMessageAdminController.getResultMessages();

			//Assert
			Assertions.assertThat(resultMessages).isNotNull();
			Assertions.assertThat(resultMessages.getBody()).isNotNull();
			Assertions.assertThat(resultMessages.getBody().getMessages()).isNotNull();
			Assertions.assertThat(resultMessages.getStatusCode()).isEqualTo(HttpStatus.OK);
		}

		@Test
		@DisplayName("등록된 결과가 있을때 조회 결과 모든 요소가 들어가 있어야 한다")
		void successNotEmpty() {
			//Arrange
			List<ResultMessage> listDto = makeSampleResultMessages();
			ResponseEntity<GetRecruitmentResultMessagesResponseDto> resultMessages;
			Mockito.when(resultMessageAdminService.getResultMessages()).thenReturn(listDto);

			//Act
			resultMessages = resultMessageAdminController.getResultMessages();

			//Assert
			Assertions.assertThat(resultMessages.getBody().getMessages().size()).isEqualTo(listDto.size());
			for (int i = 0; i < listDto.size(); i++) {
				Assertions.assertThat(resultMessages.getBody().getMessages().get(i).getMessageId())
					.isEqualTo(listDto.get(i).getId());
				Assertions.assertThat(resultMessages.getBody().getMessages().get(i).getMessage())
					.isEqualTo(listDto.get(i).getContent());
				Assertions.assertThat(resultMessages.getBody().getMessages().get(i).getMessageType())
					.isEqualTo(listDto.get(i).getMessageType());
				Assertions.assertThat(resultMessages.getBody().getMessages().get(i).getIsUse())
					.isEqualTo(listDto.get(i).getIsUse());
			}

			Assertions.assertThat(resultMessages.getStatusCode()).isEqualTo(HttpStatus.OK);
		}
	}

	List<ResultMessage> makeSampleResultMessages() {
		List<ResultMessage> resultMessages = new ArrayList<>();
		MessageType[] messageTypes = {MessageType.FAIL, MessageType.PASS, MessageType.INTERVIEW};
		for (int i = 0; i < 3; i++) {
			ResultMessage message = ResultMessage.builder().content("메시지" + i).messageType(messageTypes[i]).build();
			ReflectionTestUtils.setField(message, "id", Long.valueOf(i));
			resultMessages.add(message);
		}
		return resultMessages;
	}
}
