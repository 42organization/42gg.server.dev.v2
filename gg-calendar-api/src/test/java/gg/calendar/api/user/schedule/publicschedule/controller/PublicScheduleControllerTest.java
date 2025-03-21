package gg.calendar.api.user.schedule.publicschedule.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.calendar.api.user.schedule.publicschedule.PublicScheduleMockData;
import gg.calendar.api.user.schedule.publicschedule.controller.request.PublicScheduleCreateEventReqDto;
import gg.calendar.api.user.schedule.publicschedule.controller.request.PublicScheduleCreateJobReqDto;
import gg.calendar.api.user.schedule.publicschedule.controller.request.PublicScheduleUpdateReqDto;
import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.ScheduleGroup;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.EventTag;
import gg.data.calendar.type.JobTag;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.calendar.type.TechTag;
import gg.data.user.User;
import gg.repo.calendar.PrivateScheduleRepository;
import gg.repo.calendar.PublicScheduleRepository;
import gg.repo.calendar.ScheduleGroupRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class PublicScheduleControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PublicScheduleRepository publicScheduleRepository;

	@Autowired
	private PrivateScheduleRepository privateScheduleRepository;

	private User user;
	private String accessToken;
	@Autowired
	private TestDataUtils testDataUtils;

	@Autowired
	private PublicScheduleMockData mockData;

	@Autowired
	EntityManager em;

	@BeforeEach
	void setUp() {
		user = testDataUtils.createNewUser();
		accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
	}

	@Nested
	@DisplayName("공개일정:생성")
	class CreateEventPublicSchedule {
		@Test
		@DisplayName("[201]공개일정-42event 생성성공")
		void createEventPublicScheduleSuccess() throws Exception {
			// given
			PublicScheduleCreateEventReqDto eventPublicSchedule = PublicScheduleCreateEventReqDto.builder()
				.eventTag(EventTag.INSTRUCTION)
				.author(user.getIntraId())
				.title("42EventTag")
				.content("42EventTagTest")
				.link("https://test.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(1))
				.build();

			// when
			mockMvc.perform(post("/calendar/public/event").header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(eventPublicSchedule))).andExpect(status().isCreated());
			// then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).hasSize(1);
			assertThat(schedules.get(0).getTitle()).isEqualTo(eventPublicSchedule.getTitle());
		}

		@Test
		@DisplayName("[404]공개일정-42event 생성실패-작성자가 다를 때")
		void createEventPublicScheduleFailNotMatchAuthor() throws Exception {
			// given
			PublicScheduleCreateEventReqDto eventPublicScheduleDto = PublicScheduleCreateEventReqDto.builder()
				.eventTag(EventTag.INSTRUCTION)
				.author("another")
				.title("42EventTag")
				.content("42EventTagTest")
				.link("https://test.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(1))
				.build();

			// when
			mockMvc.perform(post("/calendar/public/event").header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(eventPublicScheduleDto)))
				.andExpect(status().isForbidden())
				.andDo(print());
			// then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).isEmpty();
		}

		@Test
		@DisplayName("[400]공개일정-42event 생성실패- 기간이 잘못되었을 때(종료날짜가 시작날짜보다 빠를때)")
		void createEventPublicScheduleFailFaultPeriod() throws Exception {
			// given
			PublicScheduleCreateEventReqDto eventPublicScheduleDto = PublicScheduleCreateEventReqDto.builder()
				.eventTag(EventTag.INSTRUCTION)
				.author(user.getIntraId())
				.title("42EventTag")
				.content("42EventTagTest")
				.link("https://test.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().minusDays(1))
				.build();

			// when
			mockMvc.perform(post("/calendar/public/event").header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(eventPublicScheduleDto)))
				.andExpect(status().isBadRequest())
				.andDo(print());
			// then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).isEmpty();
		}

		@Test
		@DisplayName("[201]공개일정-job 생성성공")
		void createJobPublicScheduleSuccess() throws Exception {
			// given
			PublicScheduleCreateJobReqDto jobPublicScheduleDto = PublicScheduleCreateJobReqDto.builder()
				.jobTag(JobTag.NEW_COMER)
				.techTag(TechTag.CLOUD)
				.author(user.getIntraId())
				.title("JobTag")
				.content("42JobTagTest")
				.link("https://test.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(1))
				.build();
			// when
			mockMvc.perform(post("/calendar/public/job").header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(jobPublicScheduleDto))).andExpect(status().isCreated());
			// then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).hasSize(1);
			assertThat(schedules.get(0).getTitle()).isEqualTo(jobPublicScheduleDto.getTitle());
		}

		@Test
		@DisplayName("[403]공개일정-job 생성실패-작성자가 다를 때")
		void createJobPublicScheduleFailNotMatchAuthor() throws Exception {
			// given
			PublicScheduleCreateJobReqDto jobPublicScheduleDto = PublicScheduleCreateJobReqDto.builder()
				.jobTag(JobTag.EXPERIENCED)
				.techTag(TechTag.NETWORK)
				.author("another")
				.title("JobTag")
				.content("JobTagTest")
				.link("https://test.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(1))
				.build();

			// when
			mockMvc.perform(post("/calendar/public/job").header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(jobPublicScheduleDto)))
				.andExpect(status().isForbidden())
				.andDo(print());
			// then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).isEmpty();
		}

		@Test
		@DisplayName("[400]공개일정-job 생성실패- 기간이 잘못되었을 때(종료닐짜가 시작날짜보다 빠를때)")
		void createJobPublicScheduleFailFaultPeriod() throws Exception {
			// given
			PublicScheduleCreateJobReqDto jobPublicScheduleDto = PublicScheduleCreateJobReqDto.builder()
				.jobTag(JobTag.EXPERIENCED)
				.techTag(TechTag.NETWORK)
				.author(user.getIntraId())
				.title("JobTag")
				.content("JobTagTest")
				.link("https://test.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().minusDays(1))
				.build();

			// when
			mockMvc.perform(post("/calendar/public/job").header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(jobPublicScheduleDto)))
				.andExpect(status().isBadRequest())
				.andDo(print());
			// then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).isEmpty();
		}
	}

	@Nested
	@DisplayName("공개일정:업데이트")
	class UpdatePublicSchedule {

		@Test
		@DisplayName("[200]공개일정업데이트성공시-42event")
		void updateEventPublicScheduleSuccess() throws Exception {
			// given
			PublicSchedule eventPublicSchedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(),
				PublicScheduleCreateEventReqDto.builder()
					.eventTag(EventTag.INSTRUCTION)
					.author(user.getIntraId())
					.title("42EventTag")
					.content("42EventTagTest")
					.link("https://test.com")
					.startTime(LocalDateTime.now())
					.endTime(LocalDateTime.now().plusDays(1))
					.build(), ScheduleStatus.ACTIVATE);
			publicScheduleRepository.save(eventPublicSchedule);

			PublicScheduleUpdateReqDto updateDto = PublicScheduleUpdateReqDto.builder()
				.classification(DetailClassification.EVENT)
				.eventTag(EventTag.INSTRUCTION)
				.author(user.getIntraId())
				.title("Updated Title")
				.content("Updated Content")
				.link("https://updated.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(2))
				.build();

			// when
			mockMvc.perform(
				put("/calendar/public/" + eventPublicSchedule.getId()).header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isOk());

			// then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).hasSize(1);
			assertThat(schedules.get(0).getTitle()).isEqualTo("Updated Title");
			assertThat(schedules.get(0).getContent()).isEqualTo("Updated Content");
		}

		@Test
		@DisplayName("[200]공개일정업데이트성공시-42event : DEACTIVATE -> ACTIVATE")
		void updateEventPublicScheduleSuccessActivate() throws Exception {
			// given
			PublicSchedule eventPublicSchedule = PublicSchedule.builder()
				.classification(DetailClassification.EVENT)
				.eventTag(EventTag.INSTRUCTION)
				.author(user.getIntraId())
				.title("42EventTag")
				.content("42EventTagTest")
				.link("https://test.com")
				.status(ScheduleStatus.DEACTIVATE)
				.startTime(LocalDateTime.now().minusDays(3))
				.endTime(LocalDateTime.now().minusDays(1))
				.build();
			publicScheduleRepository.save(eventPublicSchedule);

			PublicScheduleUpdateReqDto updateDto = PublicScheduleUpdateReqDto.builder()
				.classification(DetailClassification.EVENT)
				.eventTag(EventTag.INSTRUCTION)
				.author(user.getIntraId())
				.title("Updated Title")
				.content("Updated Content")
				.link("https://updated.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(2))
				.build();

			// when
			mockMvc.perform(
				put("/calendar/public/" + eventPublicSchedule.getId()).header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isOk());

			// then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).hasSize(1);
			assertThat(schedules.get(0).getTitle()).isEqualTo("Updated Title");
			assertThat(schedules.get(0).getContent()).isEqualTo("Updated Content");
			assertThat(schedules.get(0).getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
		}

		@Test
		@DisplayName("[200]공개일정업데이트성공시-42event : ACTIVATE -> DEACTIVATE")
		void updateEventPublicScheduleSuccessDeactivate() throws Exception {
			// given
			PublicSchedule eventPublicSchedule = PublicSchedule.builder()
				.classification(DetailClassification.EVENT)
				.eventTag(EventTag.INSTRUCTION)
				.author(user.getIntraId())
				.title("42EventTag")
				.content("42EventTagTest")
				.link("https://test.com")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(2))
				.build();
			publicScheduleRepository.save(eventPublicSchedule);

			PublicScheduleUpdateReqDto updateDto = PublicScheduleUpdateReqDto.builder()
				.classification(DetailClassification.EVENT)
				.eventTag(EventTag.INSTRUCTION)
				.author(user.getIntraId())
				.title("Updated Title")
				.content("Updated Content")
				.link("https://updated.com")
				.startTime(LocalDateTime.now().minusDays(3))
				.endTime(LocalDateTime.now().minusDays(1))
				.build();

			// when
			mockMvc.perform(
				put("/calendar/public/" + eventPublicSchedule.getId()).header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isOk());

			// then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).hasSize(1);
			assertThat(schedules.get(0).getTitle()).isEqualTo("Updated Title");
			assertThat(schedules.get(0).getContent()).isEqualTo("Updated Content");
			assertThat(schedules.get(0).getStatus()).isEqualTo(ScheduleStatus.DEACTIVATE);
		}

		@Test
		@DisplayName("[200]공개일정업데이트성공시-Job")
		void updateJobPublicScheduleSuccess() throws Exception {
			// given
			PublicSchedule jobPublicSchedule = PublicScheduleCreateJobReqDto.toEntity(user.getIntraId(),
				PublicScheduleCreateJobReqDto.builder()
					.jobTag(JobTag.NEW_COMER)
					.techTag(TechTag.CLOUD)
					.author(user.getIntraId())
					.title("42JobTag")
					.content("42JobTest")
					.link("https://test.com")
					.startTime(LocalDateTime.now())
					.endTime(LocalDateTime.now().plusDays(1))
					.build(), ScheduleStatus.ACTIVATE);
			publicScheduleRepository.save(jobPublicSchedule);

			PublicScheduleUpdateReqDto updateDto = PublicScheduleUpdateReqDto.builder()
				.classification(DetailClassification.JOB_NOTICE)
				.jobTag(JobTag.EXPERIENCED)
				.techTag(TechTag.NETWORK)
				.author(user.getIntraId())
				.title("Updated Title")
				.content("Updated Content")
				.link("https://updated.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(2))
				.build();

			// when
			mockMvc.perform(
				put("/calendar/public/" + jobPublicSchedule.getId()).header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isOk());

			// then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).hasSize(1);
			assertThat(schedules.get(0).getTitle()).isEqualTo("Updated Title");
			assertThat(schedules.get(0).getContent()).isEqualTo("Updated Content");
		}

		@Test
		@DisplayName("[403]공개일정업데이트실패-작성자가 다를 때")
		void updatePublicScheduleFailNotMatchAuthor() throws Exception {
			// given
			PublicSchedule jobPublicSchedule = PublicScheduleCreateJobReqDto.toEntity(user.getIntraId(),
				PublicScheduleCreateJobReqDto.builder()
					.jobTag(JobTag.NEW_COMER)
					.techTag(TechTag.CLOUD)
					.author(user.getIntraId())
					.title("42JobTag")
					.content("42JobTest")
					.link("https://test.com")
					.startTime(LocalDateTime.now())
					.endTime(LocalDateTime.now().plusDays(1))
					.build(), ScheduleStatus.ACTIVATE);
			publicScheduleRepository.save(jobPublicSchedule);

			PublicScheduleUpdateReqDto updateDto = PublicScheduleUpdateReqDto.builder()
				.classification(DetailClassification.JOB_NOTICE)
				.jobTag(JobTag.NEW_COMER)
				.techTag(TechTag.CLOUD)
				.author("another")
				.title("42JobTag")
				.content("42JobTest")
				.link("https://test.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(2))
				.build();

			// when
			mockMvc.perform(
					put("/calendar/public/"
						+ jobPublicSchedule.getId()).header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(status().isForbidden())
				.andDo(print());

			// then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).hasSize(1);
		}

		@Test
		@DisplayName("[403]공개일정업데이트실패-기존일정작성자가다를때")
		void updatePublicScheduleFailExistingAuthorNotMatch() throws Exception {
			// given
			PublicSchedule jobPublicSchedule = PublicScheduleCreateJobReqDto.toEntity(user.getIntraId(),
				PublicScheduleCreateJobReqDto.builder()
					.jobTag(JobTag.NEW_COMER)
					.techTag(TechTag.CLOUD)
					.author(user.getIntraId())
					.title("42JobTag")
					.content("42JobTest")
					.link("https://test.com")
					.startTime(LocalDateTime.now())
					.endTime(LocalDateTime.now().plusDays(1))
					.build(), ScheduleStatus.ACTIVATE);
			publicScheduleRepository.save(jobPublicSchedule);

			PublicScheduleUpdateReqDto updateDto = PublicScheduleUpdateReqDto.builder()
				.classification(DetailClassification.JOB_NOTICE)
				.jobTag(JobTag.NEW_COMER)
				.techTag(TechTag.CLOUD)
				.author("another")
				.title("42JobTag")
				.content("42JobTest")
				.link("https://test.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(2))
				.build();

			//when & then
			mockMvc.perform(
					put("/calendar/public/"
						+ jobPublicSchedule.getId()).header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(status().isForbidden())
				.andDo(print());

			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).hasSize(1);
		}

		@Test
		@DisplayName("[400]공개일정업데이트실패-기간이 잘못되었을 때(종료날짜가 시작날짜보다 빠를 때)")
		void updatePublicScheduleFailFaultPeriod() throws Exception {
			// given
			PublicSchedule jobPublicSchedule = PublicScheduleCreateJobReqDto.toEntity(user.getIntraId(),
				PublicScheduleCreateJobReqDto.builder()
					.jobTag(JobTag.NEW_COMER)
					.techTag(TechTag.CLOUD)
					.author(user.getIntraId())
					.title("42JobTag")
					.content("42JobTest")
					.link("https://test.com")
					.startTime(LocalDateTime.now())
					.endTime(LocalDateTime.now().plusDays(1))
					.build(), ScheduleStatus.ACTIVATE);
			publicScheduleRepository.save(jobPublicSchedule);

			PublicScheduleUpdateReqDto updateDto = PublicScheduleUpdateReqDto.builder()
				.classification(DetailClassification.JOB_NOTICE)
				.jobTag(JobTag.NEW_COMER)
				.techTag(TechTag.CLOUD)
				.author(user.getIntraId())
				.title("42JobTag")
				.content("42JobTest")
				.link("https://test.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().minusDays(2))
				.build();

			// when
			mockMvc.perform(
					put("/calendar/public/"
						+ jobPublicSchedule.getId()).header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(status().isBadRequest())
				.andDo(print());

			// then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).hasSize(1);
		}

		@Test
		@DisplayName("[404]공개일정업데이트실패-없는 일정일 때")
		void updatePublicScheduleFailNotExist() throws Exception {
			// given
			PublicScheduleUpdateReqDto updateDto = PublicScheduleUpdateReqDto.builder()
				.classification(DetailClassification.EVENT)
				.eventTag(EventTag.INSTRUCTION)
				.author(user.getIntraId())
				.title("Updated Title")
				.content("Updated Content")
				.link("https://updated.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(2))
				.build();

			// when
			mockMvc.perform(put("/calendar/public/9999").header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isNotFound()).andDo(print());

			// then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).hasSize(0);
		}

		@Test
		@DisplayName("[400]공개일정업데이트실패-제목이 50글자 초과일 때")
		void updatePublicScheduleFailTitleTooLong() throws Exception {
			// given
			PublicSchedule publicSchedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(),
				PublicScheduleCreateEventReqDto.builder()
					.eventTag(EventTag.INSTRUCTION)
					.author(user.getIntraId())
					.title("Original Title")
					.content("Original Content")
					.link("https://original.com")
					.startTime(LocalDateTime.now())
					.endTime(LocalDateTime.now().plusDays(1))
					.build(), ScheduleStatus.ACTIVATE);
			publicScheduleRepository.save(publicSchedule);

			String longTitle = "publicScheduleTest".repeat(20);
			PublicScheduleUpdateReqDto updateDto = PublicScheduleUpdateReqDto.builder()
				.classification(DetailClassification.EVENT)
				.author(user.getIntraId())
				.title(longTitle)
				.content("Updated Content")
				.link("https://updated.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(2))
				.build();

			// when
			mockMvc.perform(
					put("/calendar/public/" + publicSchedule.getId()).header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(status().isBadRequest())
				.andDo(print());

			//then
			assertThat(publicScheduleRepository.findById(publicSchedule.getId())
				.map(PublicSchedule::getTitle)
				.orElseThrow()).isEqualTo("Original Title");
		}

		@Test
		@DisplayName("[400]공개일정업데이트실패-내용이 2000글자 초과일 때")
		void updatePublicScheduleFailContentTooLong() throws Exception {
			// given
			PublicSchedule publicSchedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(),
				PublicScheduleCreateEventReqDto.builder()
					.eventTag(EventTag.INSTRUCTION)
					.author(user.getIntraId())
					.title("Original Title")
					.content("Original Content")
					.link("https://original.com")
					.startTime(LocalDateTime.now())
					.endTime(LocalDateTime.now().plusDays(1))
					.build(), ScheduleStatus.ACTIVATE);
			publicScheduleRepository.save(publicSchedule);

			String longContent = "publicScheduleTest".repeat(200);
			PublicScheduleUpdateReqDto updateDto = PublicScheduleUpdateReqDto.builder()
				.classification(DetailClassification.EVENT)
				.author(user.getIntraId())
				.title("Updated Title")
				.content(longContent)
				.link("https://updated.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(2))
				.build();

			// when
			mockMvc.perform(
					put("/calendar/public/" + publicSchedule.getId()).header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(status().isBadRequest())
				.andDo(print());

			//then
			assertThat(publicScheduleRepository.findById(publicSchedule.getId())
				.map(PublicSchedule::getTitle)
				.orElseThrow()).isEqualTo("Original Title");
		}

		@Test
		@DisplayName("[400]공개일정업데이트 실패-잘못된 id가 들어왔을때(숫자가 아닌 문자열)")
		void updatePublicScheduleFailWrongId() throws Exception {
			// given
			PublicSchedule publicSchedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(),
				PublicScheduleCreateEventReqDto.builder()
					.eventTag(EventTag.INSTRUCTION)
					.author(user.getIntraId())
					.title("Original Title")
					.content("Original Content")
					.link("https://original.com")
					.startTime(LocalDateTime.now())
					.endTime(LocalDateTime.now().plusDays(1))
					.build(), ScheduleStatus.ACTIVATE);
			publicScheduleRepository.save(publicSchedule);

			PublicScheduleUpdateReqDto updateDto = PublicScheduleUpdateReqDto.builder()
				.classification(DetailClassification.EVENT)
				.author(user.getIntraId())
				.title("Updated Title")
				.content("Updated Content")
				.link("https://updated.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(2))
				.build();

			// when
			mockMvc.perform(put("/calendar/public/abc").header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isBadRequest()).andDo(print());

			// then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).hasSize(1);
			assertThat(schedules.get(0).getTitle()).isEqualTo("Original Title");
		}

		@Test
		@DisplayName("[400]공개일정업데이트실패-42Event 태그가 아닌 경우(JOB_TAG 들어왔을 때)")
		void updateEventNotEventTag() throws Exception {
			// given
			PublicSchedule publicSchedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(),
				PublicScheduleCreateEventReqDto.builder()
					.eventTag(EventTag.INSTRUCTION)
					.author(user.getIntraId())
					.title("Original Title")
					.content("Original Content")
					.link("https://original.com")
					.startTime(LocalDateTime.now())
					.endTime(LocalDateTime.now().plusDays(1))
					.build(), ScheduleStatus.ACTIVATE);
			publicScheduleRepository.save(publicSchedule);

			PublicScheduleUpdateReqDto updatePublicSchedule = PublicScheduleUpdateReqDto.builder()
				.classification(DetailClassification.EVENT)
				.jobTag(JobTag.EXPERIENCED)
				.author(user.getIntraId())
				.title("Updated Title")
				.content("Updated Content")
				.link("https://updated.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(2))
				.build();

			//when
			mockMvc.perform(
					put("/calendar/public/" + publicSchedule.getId()).header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updatePublicSchedule)))
				.andExpect(status().isBadRequest())
				.andDo(print());

			//then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).hasSize(1);
			assertThat(schedules.get(0).getTitle()).isEqualTo("Original Title");
		}

		@Test
		@DisplayName("[400]공개일정업데이트실패-42Job 태그가 아닌 경우(EVENT_TAG 들어왔을 때)")
		void updateJobNotJobTag() throws Exception {
			// given
			PublicSchedule publicSchedule = PublicScheduleCreateJobReqDto.toEntity(user.getIntraId(),
				PublicScheduleCreateJobReqDto.builder()
					.jobTag(JobTag.NEW_COMER)
					.techTag(TechTag.CLOUD)
					.author(user.getIntraId())
					.title("Original Title")
					.content("Original Content")
					.link("https://original.com")
					.startTime(LocalDateTime.now())
					.endTime(LocalDateTime.now().plusDays(1))
					.build(), ScheduleStatus.ACTIVATE);
			publicScheduleRepository.save(publicSchedule);

			PublicScheduleUpdateReqDto updatePublicSchedule = PublicScheduleUpdateReqDto.builder()
				.classification(DetailClassification.JOB_NOTICE)
				.eventTag(EventTag.INSTRUCTION)
				.author(user.getIntraId())
				.title("Updated Title")
				.content("Updated Content")
				.link("https://updated.com")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(2))
				.build();

			//when
			mockMvc.perform(
					put("/calendar/public/" + publicSchedule.getId()).header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updatePublicSchedule)))
				.andExpect(status().isBadRequest())
				.andDo(print());

			//then
			List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
			assertThat(schedules).hasSize(1);
			assertThat(schedules.get(0).getTitle()).isEqualTo("Original Title");
		}

		@Nested
		@DisplayName("공개일정:삭제")
		class DeletePublicSchedule {
			@Test
			@DisplayName("[204]공개일정삭제성공")
			void deletePublicScheduleSuccess() throws Exception {
				// given
				PublicSchedule publicSchedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(),
					PublicScheduleCreateEventReqDto.builder()
						.author(user.getIntraId())
						.title("Original Title")
						.content("Original Content")
						.link("https://original.com")
						.startTime(LocalDateTime.now())
						.endTime(LocalDateTime.now().plusDays(1))
						.build(), ScheduleStatus.ACTIVATE);
				publicScheduleRepository.save(publicSchedule);
				// when
				mockMvc.perform(patch("/calendar/public/" + publicSchedule.getId()).header("Authorization",
					"Bearer " + accessToken)).andExpect(status().isNoContent());

				// then
				List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
				assertThat(schedules).hasSize(1);
				assertThat(schedules.get(0).getStatus()).isEqualTo(ScheduleStatus.DELETE);
			}

			@Test
			@DisplayName("[403]공개일정삭제실패-작성자가 다를 때")
			void deletePublicScheduleFailNotMatchAuthor() throws Exception {
				// given
				PublicSchedule publicSchedule = PublicScheduleCreateEventReqDto.toEntity("another",
					PublicScheduleCreateEventReqDto.builder()
						.author("another")
						.title("Original Title")
						.content("Original Content")
						.link("https://original.com")
						.startTime(LocalDateTime.now())
						.endTime(LocalDateTime.now().plusDays(1))
						.build(), ScheduleStatus.ACTIVATE);
				publicScheduleRepository.save(publicSchedule);

				//when
				mockMvc.perform(patch("/calendar/public/" + publicSchedule.getId()).header("Authorization",
					"Bearer " + accessToken)).andExpect(status().isForbidden()).andDo(print());

				//then
				List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor("another");
				assertThat(schedules).hasSize(1);
				assertThat(schedules.get(0).getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
			}

			@Test
			@DisplayName("[404]공개일정:삭제실패-없는 삭제 일정일 때")
			void deletePublicScheduleFailFaultId() throws Exception {
				// given
				PublicSchedule publicSchedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(),
					PublicScheduleCreateEventReqDto.builder()
						.author(user.getIntraId())
						.title("Original Title")
						.content("Original Content")
						.link("https://original.com")
						.startTime(LocalDateTime.now())
						.endTime(LocalDateTime.now().plusDays(1))
						.build(), ScheduleStatus.ACTIVATE);
				publicScheduleRepository.save(publicSchedule);
				// when
				mockMvc.perform(patch("/calendar/public/9999").header("Authorization", "Bearer " + accessToken))
					.andExpect(status().isNotFound())
					.andDo(print());

				log.info("public id : {}", publicSchedule.getId());
				// then
				List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
				assertThat(schedules).hasSize(1);
				assertThat(schedules.get(0).getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
			}

			@Test
			@DisplayName("[400]공개일정:삭제실패-잘못된 id가 들어왔을때(숫자가 아닌 문자열)")
			void deletePublicScheduleFailWrongId() throws Exception {
				// given
				PublicSchedule publicSchedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(),
					PublicScheduleCreateEventReqDto.builder()
						.author(user.getIntraId())
						.title("Original Title")
						.content("Original Content")
						.link("https://original.com")
						.startTime(LocalDateTime.now())
						.endTime(LocalDateTime.now().plusDays(1))
						.build(), ScheduleStatus.ACTIVATE);
				publicScheduleRepository.save(publicSchedule);

				// when
				mockMvc.perform(patch("/calendar/public/abc").header("Authorization", "Bearer " + accessToken))
					.andExpect(status().isBadRequest())
					.andDo(print());
				// then
				List<PublicSchedule> schedules = publicScheduleRepository.findByAuthor(user.getIntraId());
				assertThat(schedules).hasSize(1);
				assertThat(schedules.get(0).getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
			}

			@Test
			@DisplayName("[204]공개일정: 공개일정 삭제 시 연관된 개인일정도 삭제")
			void deletePublicScheduleAndPrivateSchedule() throws Exception {
				// given
				User otherUser = testDataUtils.createNewUser();

				PublicSchedule publicSchedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(),
					PublicScheduleCreateEventReqDto.builder()
						.author(user.getIntraId())
						.title("Original Title")
						.content("Original Content")
						.link("https://original.com")
						.startTime(LocalDateTime.now())
						.endTime(LocalDateTime.now().plusDays(1))
						.build(), ScheduleStatus.ACTIVATE);

				publicScheduleRepository.save(publicSchedule);

				PrivateSchedule privateSchedule1 = new PrivateSchedule(user, publicSchedule, false, 1L,
					ScheduleStatus.ACTIVATE);
				PrivateSchedule privateSchedule2 = new PrivateSchedule(otherUser, publicSchedule, true, 2L,
					ScheduleStatus.ACTIVATE);
				privateScheduleRepository.saveAll(Arrays.asList(privateSchedule1, privateSchedule2));

				// when
				mockMvc.perform(patch("/calendar/public/" + publicSchedule.getId()).header("Authorization",
					"Bearer " + accessToken)).andExpect(status().isNoContent()).andDo(print());
				// then
				assertThat(publicScheduleRepository.findById(publicSchedule.getId())
					.map(PublicSchedule::getStatus)
					.orElseThrow()).isEqualTo(ScheduleStatus.DELETE);
			}
		}

		@Nested
		@DisplayName("공개일정:상세조회")
		class RetrievePublicScheduleDetail {
			@Test
			@DisplayName("[200]공개일정상세조회성공")
			void retrievePublicScheduleDetailSuccess() throws Exception {
				// given
				PublicSchedule publicSchedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(),
					PublicScheduleCreateEventReqDto.builder()
						.author(user.getIntraId())
						.title("Original Title")
						.content("Original Content")
						.link("https://original.com")
						.startTime(LocalDateTime.now())
						.endTime(LocalDateTime.now().plusDays(1))
						.build(), ScheduleStatus.ACTIVATE);
				publicScheduleRepository.save(publicSchedule);
				// when
				mockMvc.perform(
						get("/calendar/public/"
							+ publicSchedule.getId()).header("Authorization", "Bearer " + accessToken))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.title").value("Original Title"))
					.andExpect(jsonPath("$.content").value("Original Content"))
					.andExpect(jsonPath("$.link").value("https://original.com"))
					.andDo(print());
			}

			@Test
			@DisplayName("[400]공개일정상세조회실패-잘못된 id일 때(숫자가 아닌 문자열)")
			void retrievePublicScheduleDetailFailWrongId() throws Exception {
				// given
				PublicSchedule publicSchedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(),
					PublicScheduleCreateEventReqDto.builder()
						.author(user.getIntraId())
						.title("Original Title")
						.content("Original Content")
						.link("https://original.com")
						.startTime(LocalDateTime.now())
						.endTime(LocalDateTime.now().plusDays(1))
						.build(), ScheduleStatus.ACTIVATE);
				publicScheduleRepository.save(publicSchedule);

				// when & then
				mockMvc.perform(get("/calendar/public/abc").header("Authorization", "Bearer " + accessToken))
					.andExpect(status().isBadRequest())
					.andDo(print());

			}

			@Test
			@DisplayName("[404]공개일정상세조회실패-없는 일정일 때")
			void retrievePublicScheduleDetailFailNotExist() throws Exception {
				//given
				PublicSchedule publicSchedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(),
					PublicScheduleCreateEventReqDto.builder()
						.author(user.getIntraId())
						.title("Original Title")
						.content("Original Content")
						.link("https://original.com")
						.startTime(LocalDateTime.now())
						.endTime(LocalDateTime.now().plusDays(1))
						.build(), ScheduleStatus.ACTIVATE);
				publicScheduleRepository.save(publicSchedule);

				//when & then
				mockMvc.perform(get("/calendar/public/9999").header("Authorization", "Bearer " + accessToken))
					.andExpect(status().isNotFound())
					.andDo(print());
			}
		}
	}

	@Nested
	@DisplayName("공유일정기간조회")
	class RetrievePublicScheduleByPeriod {
		@Test
		@DisplayName("[200]공개일정 기간조회성공 (비공개일정 제외)")
		void retrievePublicScheduleByEventPeriodSuccess() throws Exception {
			//given
			mockData.createPublicScheduleEvent(7);
			DetailClassification detailClassification = DetailClassification.EVENT;
			LocalDateTime start = LocalDateTime.now().plusDays(0);
			LocalDateTime end = LocalDateTime.now().plusDays(7);
			//when
			mockMvc.perform(
					get("/calendar/public", detailClassification).header("Authorization",
							"Bearer " + accessToken)
						.param("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
						.param("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
				.andExpect(status().isOk())
				.andDo(print());

			//then
			List<PublicSchedule> allSchedules = publicScheduleRepository.findAll();
			assertThat(allSchedules).hasSize(7); // Total of 5 EVENT + 2 OTHER_TYPE

			// Verify classifications of returned schedules
			assertThat(allSchedules).extracting("classification")
				.containsOnly(DetailClassification.EVENT);
		}

		@Test
		@DisplayName("[200]공개일정 job 기간조회성공")
		void retrievePublicScheduleByJobPeriodSuccess() throws Exception {
			//given
			mockData.createPublicScheduleJob(7);
			DetailClassification detailClassification = DetailClassification.JOB_NOTICE;
			LocalDateTime start = LocalDateTime.now().plusDays(0);
			LocalDateTime end = LocalDateTime.now().plusDays(7);
			//when
			mockMvc.perform(
					get("/calendar/public",
						detailClassification).header("Authorization",
							"Bearer " + accessToken).param("start",
							start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
						.param("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
				.andExpect(status().isOk())
				.andDo(print());
			//then
			assertThat(publicScheduleRepository.findAll()).hasSize(7);
			assertThat(publicScheduleRepository.findAll()).extracting("classification")
				.containsOnly(DetailClassification.JOB_NOTICE);
		}

		@Test
		@DisplayName("[400]공개일정조회실패 - 조회기간이 잘못된 경우(종료날짜가 시작날짜보다 빠를 때)")
		void retrievePublicScheduleFailFaultPeriod() throws Exception {
			//given
			mockData.createPublicScheduleEvent(7);
			DetailClassification detailClassification = DetailClassification.EVENT;
			LocalDateTime start = LocalDateTime.now().plusDays(0);
			LocalDateTime end = LocalDateTime.now().minusDays(7);
			//when & then
			mockMvc.perform(
					get("/calendar/public",
						detailClassification).header("Authorization",
							"Bearer " + accessToken).param("start",
							start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
						.param("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
				.andExpect(status().isBadRequest())
				.andDo(print());
		}

		@Test
		@DisplayName("[400]공개일정 조회실패 - 날짜형식이 잘못된 경우")
		void retrievePublicScheduleFaultDateFormat() throws Exception {
			// given
			mockData.createPublicScheduleEvent(7);
			DetailClassification detailClassification = DetailClassification.EVENT;
			LocalDateTime start = LocalDateTime.now().plusDays(0);
			LocalDateTime end = LocalDateTime.now().plusDays(7);
			//when & then
			mockMvc.perform(
					get("/calendar/public/",
						detailClassification).header("Authorization",
							"Bearer " + accessToken).param("start",
							start.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
						.param("end", end.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))))
				.andExpect(status().isBadRequest())
				.andDo(print());
		}
	}

	@Nested
	@DisplayName("가져온 개인일정 조회하기")
	class PublicToPrivate {
		@Autowired
		private ScheduleGroupRepository scheduleGroupRepository;

		@Test
		@DisplayName("[200]공개일정을 개인일정으로 가져오기 성공")
		void addPublicToPrivate() throws Exception {
			// given
			ScheduleGroup scheduleGroup = ScheduleGroup.builder()
				.user(user)
				.title("TEST")
				.backgroundColor("#FFFFFF")
				.build();
			scheduleGroupRepository.save(scheduleGroup);
			PublicSchedule publicSchedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(),
				PublicScheduleCreateEventReqDto.builder()
					.author(user.getIntraId())
					.title("Original Title")
					.content("Original Content")
					.link("https://original.com")
					.startTime(LocalDateTime.now())
					.endTime(LocalDateTime.now().plusDays(1))
					.build(), ScheduleStatus.ACTIVATE);
			publicSchedule = publicScheduleRepository.save(publicSchedule);
			// when
			mockMvc.perform(
				post("/calendar/public/{id}/{groupId}", publicSchedule.getId(), scheduleGroup.getId()).header(
					"Authorization", "Bearer " + accessToken)).andExpect(status().isCreated()).andDo(print());

			// then
			assertThat(privateScheduleRepository.findAll()).hasSize(1);
		}

		@Test
		@DisplayName("[404]공개일정을 개인일정으로 가져오기 실패 - 없는 일정일 때")
		void addPublicToPrivateFailNotExist() throws Exception {
			// given
			ScheduleGroup scheduleGroup = ScheduleGroup.builder()
				.user(user)
				.title("TEST")
				.backgroundColor("#FFFFFF")
				.build();
			scheduleGroupRepository.save(scheduleGroup);
			// when
			mockMvc.perform(
					post("/calendar/public/{id}/{groupId}", 99999, scheduleGroup.getId()).header("Authorization",
						"Bearer " + accessToken))
				.andExpect(status().isNotFound()).andDo(print());

			// then
			assertThat(privateScheduleRepository.findAll()).isEmpty();
			assertThat(publicScheduleRepository.findAll()).isEmpty();

		}

		@Test
		@DisplayName("[404]공개일정을 개인일정으로 가져오기 실패 - 없는 그룹일 때")
		void addPublicToPrivateFailNotExistGroup() throws Exception {
			// given
			PublicSchedule publicSchedule = PublicScheduleCreateEventReqDto.toEntity(user.getIntraId(),
				PublicScheduleCreateEventReqDto.builder()
					.author(user.getIntraId())
					.title("Original Title")
					.content("Original Content")
					.link("https://original.com")
					.startTime(LocalDateTime.now())
					.endTime(LocalDateTime.now().plusDays(1))
					.build(), ScheduleStatus.ACTIVATE);
			publicSchedule = publicScheduleRepository.save(publicSchedule);
			// when
			mockMvc.perform(
					post("/calendar/public/{id}/{groupId}",
						publicSchedule.getId(), 1).header("Authorization",
						"Bearer " + accessToken))
				.andExpect(status().isNotFound()).andDo(print());
			// then
			assertThat(privateScheduleRepository.findAll()).isEmpty();
			assertThat(scheduleGroupRepository.findAll()).isEmpty();
		}
	}
}
