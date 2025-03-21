package gg.calendar.api.user.utils.controller.response;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import gg.utils.annotation.UnitTest;
import lombok.Getter;

@Getter
@UnitTest
class FortyTwoEventResponseTest {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	private LocalDateTime getExamBeginTime() {
		return LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
	}

	private LocalDateTime getExamEndTime() {
		return getExamBeginTime().plusDays(11).withHour(12).withMinute(0);
	}

	private LocalDateTime getCreatedTime() {
		return getExamBeginTime().minusDays(2).withHour(10).withMinute(0);
	}

	private LocalDateTime getUpdatedTime() {
		return getExamBeginTime().minusDays(1).withHour(12).withMinute(0);
	}

	@Test
	@DisplayName("FortyTwoEventResDto 생성자 테스트")
	void toSuccess() throws JsonProcessingException {
		//Given
		LocalDateTime beginAt = getExamBeginTime();
		LocalDateTime endAt = getExamEndTime();
		LocalDateTime createdAt = getCreatedTime();
		LocalDateTime updatedAt = getUpdatedTime();

		ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		String jsonResponse = "{"
			+ "\"id\": 1,"
			+ "\"name\": \"Exam06\","
			+ "\"description\": \"ft_irc\","
			+ "\"location\": \"location\","
			+ "\"kind\": \"event\","
			+ "\"begin_at\": \"" + beginAt.format(FORMATTER) + "\","
			+ "\"end_at\": \"" + endAt.format(FORMATTER) + "\","
			+ "\"created_at\": \"" + createdAt.format(FORMATTER) + "\","
			+ "\"updated_at\": \"" + updatedAt.format(FORMATTER) + "\""
			+ "}";
		//When
		FortyTwoEventResponse response = objectMapper.readValue(jsonResponse, FortyTwoEventResponse.class);

		//Then
		assertThat(response.getId()).isEqualTo(1L);
		assertThat(response.getName()).isEqualTo("Exam06");
		assertThat(response.getDescription()).isEqualTo("ft_irc");
		assertThat(response.getLocation()).isEqualTo("location");
		assertThat(response.getKind()).isEqualTo("event");
		assertThat(response.getBeginAt()).isEqualTo(beginAt);
		assertThat(response.getEndAt()).isEqualTo(endAt);
		assertThat(response.getCreatedAt()).isEqualTo(createdAt);
		assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
	}
}
