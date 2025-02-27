package gg.calendar.api.user.schedule.privateschedule.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.calendar.api.user.schedule.privateschedule.PrivateScheduleMockData;
import gg.calendar.api.user.schedule.privateschedule.controller.request.ImportedScheduleUpdateReqDto;
import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.ScheduleGroup;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.user.User;
import gg.repo.calendar.PrivateScheduleRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class ImportedScheduleControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PrivateScheduleRepository privateScheduleRepository;

	@Autowired
	private PrivateScheduleMockData privateScheduleMockData;

	@Autowired
	private TestDataUtils testDataUtils;

	private User user;
	private String accessToken;

	@BeforeEach
	void setUp() {
		user = testDataUtils.createNewUser();
		accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
	}

	@Nested
	@DisplayName("가져온 PrivateSchedule 수정하기")
	class UpdateImportedSchedule {
		@Test
		@DisplayName("성공 200")
		void success() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			ImportedScheduleUpdateReqDto reqDto = ImportedScheduleUpdateReqDto.builder()
				.alarm(false)
				.groupId(scheduleGroup.getId())
				.build();
			//when
			mockMvc.perform(put("/calendar/private/imported/" + privateSchedule.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isOk());
			//then
			PrivateSchedule updated = privateScheduleRepository.findById(privateSchedule.getId()).orElseThrow();
			Assertions.assertThat(privateSchedule.getGroupId()).isEqualTo(updated.getGroupId());
			Assertions.assertThat(privateSchedule.getAlarm()).isEqualTo(updated.getAlarm());
		}

		@Test
		@DisplayName("작성자가 아닌 사람이 일정을 수정 하려는 경우 403")
		void notMatchAuthor() throws Exception {
			//given
			User other = testDataUtils.createNewUser("other");
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(other, publicSchedule,
				scheduleGroup.getId());
			ImportedScheduleUpdateReqDto reqDto = ImportedScheduleUpdateReqDto.builder()
				.alarm(false)
				.groupId(scheduleGroup.getId())
				.build();
			//when&then
			mockMvc.perform(put("/calendar/private/imported/" + privateSchedule.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("일정이 없는 경우 404")
		void notFoundSchedule() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			ImportedScheduleUpdateReqDto reqDto = ImportedScheduleUpdateReqDto.builder()
				.alarm(false)
				.groupId(scheduleGroup.getId())
				.build();
			//when&then
			mockMvc.perform(put("/calendar/private/imported/" + privateSchedule.getId() + 123411243)
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("커스터마이징 그룹이 없는 경우 404")
		void notFoundScheduleGroup() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			ImportedScheduleUpdateReqDto reqDto = ImportedScheduleUpdateReqDto.builder()
				.alarm(false)
				.groupId(0L)
				.build();
			//when&then
			mockMvc.perform(put("/calendar/private/imported/" + privateSchedule.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("가져온 PrivateSchedule 삭제하기")
	class DeleteImportedSchedule {
		@Test
		@DisplayName("삭제 성공 204")
		void success() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.EVENT);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			//when
			mockMvc.perform(patch("/calendar/private/imported/" + privateSchedule.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
			//then
			Assertions.assertThat(privateSchedule.getStatus()).isEqualTo(ScheduleStatus.DELETE);
			Assertions.assertThat(privateSchedule.getPublicSchedule().getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
		}

		@Test
		@DisplayName("작성자가 아닌 사람이 삭제하는 경우 403")
		void notMatchAuthor() throws Exception {
			//given
			User other = testDataUtils.createNewUser("other");
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.EVENT);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(other, publicSchedule,
				scheduleGroup.getId());
			//when
			mockMvc.perform(patch("/calendar/private/imported/" + privateSchedule.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
			//then
			Assertions.assertThat(privateSchedule.getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
			Assertions.assertThat(privateSchedule.getPublicSchedule().getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
		}

		@Test
		@DisplayName("가져온 일정이 아닌 일정을 삭제하는 경우 403")
		void invalidDetailClassification() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			//when
			mockMvc.perform(patch("/calendar/private/imported/" + privateSchedule.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
			//then
			Assertions.assertThat(privateSchedule.getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
			Assertions.assertThat(privateSchedule.getPublicSchedule().getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
		}

		@Test
		@DisplayName("없는 일정인 경우 404")
		void notFoundSchedule() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.EVENT);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			//when
			mockMvc.perform(patch("/calendar/private/imported/" + privateSchedule.getId() + 1234)
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
			//then
			Assertions.assertThat(privateSchedule.getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
			Assertions.assertThat(privateSchedule.getPublicSchedule().getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
		}
	}
}
