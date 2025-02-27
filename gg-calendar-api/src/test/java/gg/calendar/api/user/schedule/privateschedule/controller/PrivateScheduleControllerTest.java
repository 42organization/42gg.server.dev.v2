package gg.calendar.api.user.schedule.privateschedule.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
import gg.calendar.api.user.schedule.privateschedule.controller.request.PrivateScheduleCreateReqDto;
import gg.calendar.api.user.schedule.privateschedule.controller.request.PrivateScheduleUpdateReqDto;
import gg.calendar.api.user.schedule.privateschedule.controller.response.PrivateScheduleDetailResDto;
import gg.calendar.api.user.schedule.privateschedule.controller.response.PrivateSchedulePeriodResDto;
import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.ScheduleGroup;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.user.User;
import gg.repo.calendar.PrivateScheduleRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.dto.ListResponseDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class PrivateScheduleControllerTest {
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
	@DisplayName("PrivateSchedule 생성하기")
	class CreatePrivateSchedule {
		@Test
		@DisplayName("성공 201")
		void success() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PrivateScheduleCreateReqDto reqDto = PrivateScheduleCreateReqDto.builder()
				.title("title")
				.link("")
				.content("")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(1))
				.alarm(true)
				.groupId(scheduleGroup.getId())
				.status(ScheduleStatus.ACTIVATE)
				.build();
			//when
			mockMvc.perform(post("/calendar/private")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isCreated());
			//then

			List<PrivateSchedule> schedules = privateScheduleRepository.findAll();
			assertThat(schedules.size()).isEqualTo(1);
			PrivateSchedule privateSchedule = schedules.get(0);
			Assertions.assertThat(privateSchedule.getGroupId()).isEqualTo(scheduleGroup.getId());
			Assertions.assertThat(privateSchedule.getAlarm()).isEqualTo(reqDto.isAlarm());
		}

		@Test
		@DisplayName("일정 그룹이 없는 경우 404")
		void notFoundGroup() throws Exception {
			//given
			PrivateScheduleCreateReqDto reqDto = PrivateScheduleCreateReqDto.builder()
				.title("title")
				.link("")
				.content("")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(1))
				.alarm(true)
				.groupId(2L)
				.status(ScheduleStatus.ACTIVATE)
				.build();
			//when&then
			mockMvc.perform(post("/calendar/private")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("시작 날짜보다 끝나는 날짜가 빠른 경우 400")
		void endTimeBeforeStartTime() throws Exception {
			//given
			PrivateScheduleCreateReqDto reqDto = PrivateScheduleCreateReqDto.builder()
				.title("title")
				.link("")
				.content("")
				.startTime(LocalDateTime.now().plusDays(1))
				.endTime(LocalDateTime.now())
				.alarm(true)
				.groupId(2L)
				.status(ScheduleStatus.ACTIVATE)
				.build();
			//when&then
			mockMvc.perform(post("/calendar/private")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("PrivateSchedule 수정하기")
	class UpdatePrivateSchedule {
		@Test
		@DisplayName("성공 200")
		void success() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			PrivateScheduleUpdateReqDto reqDto = PrivateScheduleUpdateReqDto.builder()
				.alarm(false)
				.title("123")
				.content("")
				.link(null)
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(1))
				.groupId(scheduleGroup.getId())
				.build();
			//when
			mockMvc.perform(put("/calendar/private/" + privateSchedule.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isOk());
			//then
			PrivateSchedule updated = privateScheduleRepository.findById(privateSchedule.getId()).orElseThrow();
			Assertions.assertThat(privateSchedule.getGroupId()).isEqualTo(updated.getGroupId());
			Assertions.assertThat(privateSchedule.getAlarm()).isEqualTo(updated.getAlarm());
			Assertions.assertThat(privateSchedule.getGroupId()).isEqualTo(updated.getGroupId());
			Assertions.assertThat(privateSchedule.getPublicSchedule()).isEqualTo(updated.getPublicSchedule());
		}

		@Test
		@DisplayName("종료 날짜가 시작 날짜보다 빠른 경우 400")
		void endTimeBeforeStartTime() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			PrivateScheduleUpdateReqDto reqDto = PrivateScheduleUpdateReqDto.builder()
				.alarm(false)
				.title("123")
				.content("")
				.link(null)
				.startTime(LocalDateTime.now().plusDays(1))
				.endTime(LocalDateTime.now())
				.groupId(scheduleGroup.getId())
				.build();
			//when&then
			mockMvc.perform(put("/calendar/private/" + privateSchedule.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("작성자가 아닌 사람이 일정을 수정 하려는 경우 403")
		void notMatchAuthor() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule("author",
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			PrivateScheduleUpdateReqDto reqDto = PrivateScheduleUpdateReqDto.builder()
				.alarm(false)
				.title("123")
				.content("")
				.link(null)
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(1))
				.groupId(scheduleGroup.getId())
				.build();
			//when&then
			mockMvc.perform(put("/calendar/private/" + privateSchedule.getId())
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
			PrivateScheduleUpdateReqDto reqDto = PrivateScheduleUpdateReqDto.builder()
				.alarm(false)
				.title("123")
				.content("")
				.link(null)
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(1))
				.groupId(scheduleGroup.getId())
				.build();
			//when&then
			mockMvc.perform(put("/calendar/private/" + privateSchedule.getId() + 123411243)
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
			PrivateScheduleUpdateReqDto reqDto = PrivateScheduleUpdateReqDto.builder()
				.alarm(false)
				.title("123")
				.content("")
				.link(null)
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(1))
				.groupId(0L)
				.build();
			//when&then
			mockMvc.perform(put("/calendar/private/" + privateSchedule.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("PrivateSchedule 삭제하기")
	class DeletePrivateSchedule {
		@Test
		@DisplayName("삭제 성공 204")
		void success() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			//when
			mockMvc.perform(patch("/calendar/private/" + privateSchedule.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
			//then
			Assertions.assertThat(privateSchedule.getStatus()).isEqualTo(ScheduleStatus.DELETE);
			Assertions.assertThat(privateSchedule.getPublicSchedule().getStatus()).isEqualTo(ScheduleStatus.DELETE);
		}

		@Test
		@DisplayName("작성자가 아닌 사람이 삭제하는 경우 403")
		void notMatchAuthor() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule("author",
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			//when
			mockMvc.perform(patch("/calendar/private/" + privateSchedule.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
			//then
			Assertions.assertThat(privateSchedule.getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
			Assertions.assertThat(privateSchedule.getPublicSchedule().getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
		}

		@Test
		@DisplayName("상세분류가 개인일정이 아닌 일정을 삭제하는 경우 403")
		void notPrivateSchedule() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.EVENT);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			//when
			mockMvc.perform(patch("/calendar/private/" + privateSchedule.getId())
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
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			//when
			mockMvc.perform(patch("/calendar/private/" + privateSchedule.getId() + 1234)
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
			//then
			Assertions.assertThat(privateSchedule.getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
			Assertions.assertThat(privateSchedule.getPublicSchedule().getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
		}
	}

	@Nested
	@DisplayName("PrivateSchedule 상세조회")
	class DetailPrivateSchedule {
		@Test
		@DisplayName("조회 성공 200")
		void success() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			//when
			String response = mockMvc.perform(get("/calendar/private/" + privateSchedule.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			PrivateScheduleDetailResDto dto = objectMapper.readValue(response, PrivateScheduleDetailResDto.class);
			//then
			Assertions.assertThat(privateSchedule.getId()).isEqualTo(dto.getId());
			Assertions.assertThat(privateSchedule.getAlarm()).isEqualTo(dto.isAlarm());
			Assertions.assertThat(scheduleGroup.getTitle()).isEqualTo(dto.getGroupTitle());
			Assertions.assertThat(scheduleGroup.getBackgroundColor()).isEqualTo(dto.getGroupColor());
			Assertions.assertThat(publicSchedule.getClassification()).isEqualTo(dto.getClassification());
			Assertions.assertThat(publicSchedule.getEventTag()).isEqualTo(dto.getEventTag());
			Assertions.assertThat(publicSchedule.getJobTag()).isEqualTo(dto.getJobTag());
			Assertions.assertThat(publicSchedule.getTechTag()).isEqualTo(dto.getTechTag());
			Assertions.assertThat(publicSchedule.getTitle()).isEqualTo(dto.getTitle());
			Assertions.assertThat(publicSchedule.getContent()).isEqualTo(dto.getContent());
			Assertions.assertThat(publicSchedule.getLink()).isEqualTo(dto.getLink());
			Assertions.assertThat(publicSchedule.getAuthor()).isEqualTo(dto.getAuthor());
			Assertions.assertThat(publicSchedule.getStartTime()).isEqualTo(dto.getStartTime());
			Assertions.assertThat(publicSchedule.getEndTime()).isEqualTo(dto.getEndTime());
		}

		@Test
		@DisplayName("작성자가 아닌 사람이 조회하는 경우 403")
		void notMatchAuthor() throws Exception {
			//given
			User other = testDataUtils.createNewUser("other");
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(other, publicSchedule,
				scheduleGroup.getId());
			//when&then
			mockMvc.perform(get("/calendar/private/" + privateSchedule.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("없는 일정인 경우 404")
		void notFoundSchedule() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			//when&then
			mockMvc.perform(get("/calendar/private/" + privateSchedule.getId() + 1234)
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("스케줄 그룹이 없는 경우 404")
		void notFoundScheduleGroup() throws Exception {
			//given
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				0L);
			//when&then
			mockMvc.perform(get("/calendar/private/" + privateSchedule.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("PrivateSchedule 기간조회")
	class PeriodPrivateSchedule {
		@Test
		@DisplayName("조회 성공 200")
		void success() throws Exception {
			//given
			ScheduleGroup scheduleGroup = privateScheduleMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule1 = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			PrivateSchedule privateSchedule2 = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			LocalDate start = LocalDate.now().minusDays(10);
			LocalDate end = LocalDate.now().plusDays(10);
			//when
			String response = mockMvc.perform(get("/calendar/private")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("start", start.toString())
					.param("end", end.toString()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			ListResponseDto<PrivateSchedulePeriodResDto> dto = objectMapper.readValue(response, ListResponseDto.class);
			//then
			Assertions.assertThat(dto.getContent().size()).isEqualTo(2);
			Assertions.assertThat(publicSchedule.getStartTime().isBefore(end.atTime(LocalTime.MAX))).isEqualTo(true);
			Assertions.assertThat(publicSchedule.getEndTime().isAfter(start.atStartOfDay())).isEqualTo(true);
		}

		@Test
		@DisplayName("종료 날짜가 시작 날짜보다 빠른 경우 400")
		void endTimeBeforeStartTime() throws Exception {
			//given
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				0L);
			//when&then
			mockMvc.perform(get("/calendar/private")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("start", LocalDate.now().plusDays(10).toString())
					.param("end", LocalDate.now().toString()))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("스케줄 그룹이 없는 경우 404")
		void notFoundScheduleGroup() throws Exception {
			//given
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				0L);
			//when&then
			mockMvc.perform(get("/calendar/private")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("start", LocalDate.now().toString())
					.param("end", LocalDate.now().plusDays(10).toString()))
				.andExpect(status().isNotFound());
		}
	}
}
