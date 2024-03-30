package gg.admin.repo.recurit.maage;

import java.util.List;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.recruit.manage.RecruitResultMessageRepository;
import gg.data.recruit.manage.ResultMessage;
import gg.data.recruit.manage.enums.MessageType;
import gg.utils.annotation.IntegrationTest;
import gg.utils.annotation.UnitTest;

@IntegrationTest
@UnitTest
@Transactional
public class RecruitResultMessageRepositoryTest {

	@Autowired
	EntityManager entityManager;

	@Autowired
	RecruitResultMessageRepository recruitResultMessageRepository;

	@Nested
	@DisplayName("disablePreviousResultMessages")
	class DisablePreviousResultMessages {
		@Test
		@DisplayName("MessageType에 해당하는 모든 컬럼이 변경되어야 한다")
		void test() {
			MessageType targetType = MessageType.FAIL;
			MessageType notTargetType = MessageType.PASS;
			//Arrange
			ResultMessage resultMessage1 = ResultMessage.builder()
				.messageType(targetType)
				.content("good bye")
				.build();

			ResultMessage resultMessage2 = ResultMessage.builder()
				.messageType(notTargetType)
				.content("good bye")
				.build();

			ResultMessage resultMessage3 = ResultMessage.builder()
				.messageType(targetType)
				.content("good bye")
				.build();

			entityManager.persist(resultMessage1);
			entityManager.persist(resultMessage2);
			entityManager.persist(resultMessage3);
			entityManager.flush();
			entityManager.clear();

			//Act
			recruitResultMessageRepository.disablePreviousResultMessages(MessageType.FAIL);

			//Assert
			List<ResultMessage> entities = recruitResultMessageRepository.findAll();
			for (ResultMessage entity : entities) {
				if (entity.getMessageType() == targetType) {
					Assertions.assertThat(entity.getIsUse()).isFalse();
				} else {
					Assertions.assertThat(entity.getIsUse()).isTrue();
				}
			}
		}
	}

	@Nested
	@DisplayName("disablePreviousResultMessages")
	class FindAllOrderByIdDesc {
		@Test
		@DisplayName("결과 메시지 조회")
		void success() {
			//Arrange
			createResultMessage(MessageType.FAIL);
			createResultMessage(MessageType.INTERVIEW);
			createResultMessage(MessageType.PASS);
			entityManager.flush();
			entityManager.clear();

			//Act
			List<ResultMessage> results = recruitResultMessageRepository.findAllOrderByIdDesc();

			//Assert
			Assertions.assertThat(results.size()).isEqualTo(3);
		}
	}

	void createResultMessage(MessageType messageType) {
		ResultMessage resultMessage1 = ResultMessage.builder()
			.messageType(messageType)
			.content("hello")
			.build();
		entityManager.persist(resultMessage1);
	}
}
