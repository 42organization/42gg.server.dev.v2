package gg.calendar.api.user.custom.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import gg.calendar.api.user.custom.CalendarCustomMockData;
import gg.calendar.api.user.custom.controller.request.CalendarCustomCreateReqDto;
import gg.calendar.api.user.custom.controller.request.CalendarCustomUpdateReqDto;
import gg.calendar.api.user.custom.controller.response.CalendarCustomViewResDto;
import gg.calendar.api.user.schedule.privateschedule.PrivateScheduleMockData;
import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.ScheduleGroup;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.user.User;
import gg.repo.calendar.PrivateScheduleRepository;
import gg.repo.calendar.ScheduleGroupRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.dto.ListResponseDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class CalendarCustomControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ScheduleGroupRepository scheduleGroupRepository;

	@Autowired
	private PrivateScheduleRepository privateScheduleRepository;

	@Autowired
	private CalendarCustomMockData calendarCustomMockData;

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
	@DisplayName("ScheduleGroup 생성하기")
	class CreatePrivateSchedule {
		@Test
		@DisplayName("성공 201")
		void success() throws Exception {
			//given
			CalendarCustomCreateReqDto reqDto = CalendarCustomCreateReqDto.builder()
				.title("공부")
				.backgroundColor("#FFFFFF")
				.build();
			//when
			mockMvc.perform(post("/calendar/custom")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isCreated());
			//then
			List<ScheduleGroup> scheduleGroups = scheduleGroupRepository.findAll();
			assertThat(scheduleGroups.size()).isEqualTo(1);
			ScheduleGroup scheduleGroup = scheduleGroups.get(0);
			Assertions.assertThat(scheduleGroup.getTitle()).isEqualTo(reqDto.getTitle());
			Assertions.assertThat(scheduleGroup.getBackgroundColor()).isEqualTo(reqDto.getBackgroundColor());
		}

		@Test
		@DisplayName("잘못된 색상코드(hex code)가 들어온 경우 400")
		void invalidHexColorCode() throws Exception {
			//given
			CalendarCustomCreateReqDto reqDto = CalendarCustomCreateReqDto.builder()
				.title("공부")
				.backgroundColor("잘못된 색상 코드")
				.build();
			//when&then
			mockMvc.perform(post("/calendar/custom")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("ScheduleGroup 수정하기")
	class UpdatePrivateSchedule {
		@Test
		@DisplayName("성공 200")
		void success() throws Exception {
			//given
			ScheduleGroup scheduleGroup = calendarCustomMockData.createScheduleGroup(user);
			CalendarCustomUpdateReqDto reqDto = CalendarCustomUpdateReqDto.builder()
				.title("공부")
				.backgroundColor("#AAAAAA")
				.build();
			//when
			mockMvc.perform(put("/calendar/custom/" + scheduleGroup.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isOk());
			//then
			ScheduleGroup updated = scheduleGroupRepository.findById(scheduleGroup.getId()).orElseThrow();
			Assertions.assertThat(updated.getTitle()).isEqualTo(reqDto.getTitle());
			Assertions.assertThat(updated.getBackgroundColor()).isEqualTo(reqDto.getBackgroundColor());
		}

		@Test
		@DisplayName("잘못된 색상코드(hex code)가 들어온 경우 400")
		void invalidHexColorCode() throws Exception {
			//given
			ScheduleGroup scheduleGroup = calendarCustomMockData.createScheduleGroup(user);
			CalendarCustomUpdateReqDto reqDto = CalendarCustomUpdateReqDto.builder()
				.title("공부")
				.backgroundColor("잘못된 색상 코드")
				.build();
			//when&then
			mockMvc.perform(put("/calendar/custom/" + scheduleGroup.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("스케줄 그룹이 없는 경우 404")
		void notFoundGroup() throws Exception {
			//given
			ScheduleGroup scheduleGroup = calendarCustomMockData.createScheduleGroup(user);
			CalendarCustomUpdateReqDto reqDto = CalendarCustomUpdateReqDto.builder()
				.title("공부")
				.backgroundColor("#AAAAAA")
				.build();
			//when&then
			mockMvc.perform(put("/calendar/custom/" + scheduleGroup.getId() + 12341234)
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(reqDto)))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("ScheduleGroup 목록 조회하기")
	class GetPrivateScheduleView {
		@Test
		@DisplayName("목록 조회 성공 200")
		void success() throws Exception {
			//given
			ScheduleGroup scheduleGroup1 = calendarCustomMockData.createScheduleGroup(user);
			ScheduleGroup scheduleGroup2 = calendarCustomMockData.createScheduleGroup(user);
			//when
			String response = mockMvc.perform(get("/calendar/custom")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			ListResponseDto<CalendarCustomViewResDto> dto = objectMapper.readValue(response, ListResponseDto.class);
			//then
			Assertions.assertThat(dto.getContent().size()).isEqualTo(2);
		}

		@Test
		@DisplayName("빈 목록 반환 200")
		void empty() throws Exception {
			//given
			//스케줄 그룹 생성 X
			//when
			String response = mockMvc.perform(get("/calendar/custom")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			ListResponseDto<CalendarCustomViewResDto> dto = objectMapper.readValue(response, ListResponseDto.class);
			//then
			Assertions.assertThat(dto.getContent().size()).isEqualTo(0);
		}
	}

	@Nested
	@DisplayName("ScheduleGroup 삭제하기")
	class DeletePrivateSchedule {
		@Test
		@DisplayName("성공 204")
		void success() throws Exception {
			//given
			ScheduleGroup scheduleGroup = calendarCustomMockData.createScheduleGroup(user);
			//when
			mockMvc.perform(delete("/calendar/custom/" + scheduleGroup.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
			List<ScheduleGroup> empty = scheduleGroupRepository.findAll();
			//then
			Assertions.assertThat(empty.size()).isEqualTo(0);
		}

		@Test
		@DisplayName("잘못된 아이디인 경우 400")
		void invalidId() throws Exception {
			//given
			ScheduleGroup scheduleGroup = calendarCustomMockData.createScheduleGroup(user);
			//when&then
			mockMvc.perform(delete("/calendar/custom/" + "잘못된 아이디")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("스케줄 그룹이 없는 경우 404")
		void notFoundGroup() throws Exception {
			//given
			ScheduleGroup scheduleGroup = calendarCustomMockData.createScheduleGroup(user);
			//when&then
			mockMvc.perform(delete("/calendar/custom/" + scheduleGroup.getId() + 12341234)
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("스케줄 그룹을 삭제했을 때, 연관된 PrivateSchedule이 삭제되는 경우 204")
		void deleteCascade() throws Exception {
			//given
			ScheduleGroup scheduleGroup = calendarCustomMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.PRIVATE_SCHEDULE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			//when
			mockMvc.perform(delete("/calendar/custom/" + scheduleGroup.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
			List<ScheduleGroup> empty = scheduleGroupRepository.findAll();
			//then
			Assertions.assertThat(empty.size()).isEqualTo(0);
			privateScheduleRepository.findById(privateSchedule.getId()).ifPresent(ps -> {
				Assertions.assertThat(ps.getStatus()).isEqualTo(ScheduleStatus.DELETE);
				Assertions.assertThat(ps.getPublicSchedule().getStatus()).isEqualTo(ScheduleStatus.DELETE);
			});
		}

		@Test
		@DisplayName("스케줄 그룹을 삭제했을 때, 연관된 PublicSchedule은 삭제되지 않는 경우 204")
		void deleteNotCascade() throws Exception {
			//given
			ScheduleGroup scheduleGroup = calendarCustomMockData.createScheduleGroup(user);
			PublicSchedule publicSchedule = privateScheduleMockData.createPublicSchedule(user.getIntraId(),
				DetailClassification.JOB_NOTICE);
			PrivateSchedule privateSchedule = privateScheduleMockData.createPrivateSchedule(user, publicSchedule,
				scheduleGroup.getId());
			//when
			mockMvc.perform(delete("/calendar/custom/" + scheduleGroup.getId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
			List<ScheduleGroup> empty = scheduleGroupRepository.findAll();
			//then
			Assertions.assertThat(empty.size()).isEqualTo(0);
			privateScheduleRepository.findById(privateSchedule.getId()).ifPresent(ps -> {
				Assertions.assertThat(ps.getStatus()).isEqualTo(ScheduleStatus.DELETE);
				Assertions.assertThat(ps.getPublicSchedule().getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
			});
		}
	}
}
