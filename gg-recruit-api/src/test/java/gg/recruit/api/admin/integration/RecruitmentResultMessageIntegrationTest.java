package gg.recruit.api.admin.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.admin.repo.recruit.manage.RecruitResultMessageRepository;
import gg.data.recruit.manage.ResultMessage;
import gg.data.recruit.manage.enums.MessageType;
import gg.data.user.User;
import gg.recruit.api.admin.service.dto.RecruitmentResultMessageDto;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class RecruitmentResultMessageIntegrationTest {
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	RecruitResultMessageRepository recruitResultMessageRepository;
	@Autowired
	MockMvc mockMvc;
	@Autowired
	EntityManager em;

	@Nested
	@DisplayName("POST /admin/recruitments/result/message")
	class PostResultMessage {
		@Test
		@DisplayName("저장 성공")
		void saveSuccess() throws Exception {
			//Arrange
			MessageType targetType = MessageType.FAIL;

			User adminUser = testDataUtils.createAdminUser();
			String accessToken = testDataUtils.getLoginAccessTokenFromUser(adminUser);
			ResultMessage resultMessage1 = makeResultMessage(targetType);
			em.persist(resultMessage1);
			em.flush();
			List<ResultMessage> all = recruitResultMessageRepository.findAll();
			HashSet<Long> previousId = all.stream()
				.map(ResultMessage::getId)
				.collect(Collectors.toCollection(HashSet::new));
			em.clear();

			RecruitmentResultMessageDto req = new RecruitmentResultMessageDto(targetType, "bye");
			String content = objectMapper.writeValueAsString(req);

			//Act
			mockMvc.perform(post(("/admin/recruitments/result/message"))
					.header("Authorization", "Bearer " + accessToken)
					.contentType("application/json")
					.content(content))
				.andExpect(status().isCreated());

			//Assert
			all = recruitResultMessageRepository.findAll();
			for (ResultMessage resultMessage : all) {
				if (previousId.contains(resultMessage.getId())) {
					Assertions.assertThat(resultMessage.getIsUse()).isFalse();
				} else {
					Assertions.assertThat(resultMessage.getIsUse()).isTrue();
				}
			}
		}

		@Test
		@DisplayName("이전에 저장한 같은 타입의 message는 fail여야 한다")
		void previousMessageStatusFalse() throws Exception {
			//Arrange
			MessageType targetType = MessageType.FAIL;
			MessageType differentType = MessageType.PASS;
			MessageType differentType2 = MessageType.INTERVIEW;

			User adminUser = testDataUtils.createAdminUser();
			String accessToken = testDataUtils.getLoginAccessTokenFromUser(adminUser);
			ResultMessage sameResultMessage = makeResultMessage(targetType);
			ResultMessage sameResultMessage2 = makeResultMessage(targetType);
			ResultMessage differentResultMessage = makeResultMessage(differentType);
			ResultMessage differentResultMessage2 = makeResultMessage(differentType2);
			em.persist(sameResultMessage);
			em.persist(sameResultMessage2);
			em.persist(differentResultMessage);
			em.persist(differentResultMessage2);
			em.flush();
			List<ResultMessage> all = recruitResultMessageRepository.findAll();
			all.forEach((result) -> Assertions.assertThat(result.getIsUse()).isTrue());
			HashSet<Long> previousId = all.stream()
				.map(ResultMessage::getId)
				.collect(Collectors.toCollection(HashSet::new));
			em.clear();

			RecruitmentResultMessageDto req = new RecruitmentResultMessageDto(targetType, "bye");
			String content = objectMapper.writeValueAsString(req);

			//Act
			mockMvc.perform(post(("/admin/recruitments/result/message"))
					.header("Authorization", "Bearer " + accessToken)
					.contentType("application/json")
					.content(content))
				.andExpect(status().isCreated());

			//Assert
			all = recruitResultMessageRepository.findAll();
			all = recruitResultMessageRepository.findAll();
			for (ResultMessage resultMessage : all) {
				if (resultMessage.getMessageType().equals(targetType)) {
					if (previousId.contains(resultMessage.getId())) {
						Assertions.assertThat(resultMessage.getIsUse()).isFalse();
					} else {
						Assertions.assertThat(resultMessage.getIsUse()).isTrue();
					}
				} else {
					Assertions.assertThat(resultMessage.getIsUse()).isTrue();
				}
			}
		}
	}

	private static ResultMessage makeResultMessage(MessageType targetType) {
		return ResultMessage.builder()
			.messageType(targetType)
			.content("good bye")
			.build();
	}
}
