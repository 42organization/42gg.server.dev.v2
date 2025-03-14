package gg.calendar.api.admin.schedule.publicschedule.controller;

import static gg.data.calendar.type.JobTag.*;
import static gg.data.calendar.type.TechTag.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.admin.repo.calendar.PrivateScheduleAdminRepository;
import gg.admin.repo.calendar.PublicScheduleAdminRepository;
import gg.admin.repo.calendar.ScheduleGroupAdminRepository;
import gg.calendar.api.admin.schedule.publicschedule.PublicScheduleAdminMockData;
import gg.calendar.api.admin.schedule.publicschedule.controller.request.PublicScheduleAdminCreateEventReqDto;
import gg.calendar.api.admin.schedule.publicschedule.controller.request.PublicScheduleAdminCreateJobReqDto;
import gg.calendar.api.admin.schedule.publicschedule.controller.request.PublicScheduleAdminUpdateReqDto;
import gg.calendar.api.admin.schedule.publicschedule.controller.response.PublicScheduleAdminResDto;
import gg.calendar.api.admin.schedule.publicschedule.controller.response.PublicScheduleAdminUpdateResDto;
import gg.calendar.api.admin.schedule.publicschedule.service.PublicScheduleAdminService;
import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.ScheduleGroup;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.EventTag;
import gg.data.calendar.type.JobTag;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.calendar.type.TechTag;
import gg.data.user.User;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class PublicScheduleAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	EntityManager em;

	@Autowired
	private PublicScheduleAdminMockData publicScheduleAdminMockData;

	@Autowired
	private PublicScheduleAdminRepository publicScheduleAdminRepository;

	@Autowired
	private PrivateScheduleAdminRepository privateScheduleAdminRepository;

	@Autowired
	private ScheduleGroupAdminRepository scheduleGroupAdminRepository;

	@Autowired
	private TestDataUtils testDataUtils;

	private User user;

	private String accessToken;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		user = testDataUtils.createAdminUser();
		accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
	}

	@Nested
	@DisplayName("Admin PublicSchedule 등록 테스트")
		// mockMvc.perform()로 테스트 -> 실제 API 호출
	class CreatePublicScheduleTest {

		@Autowired
		private PublicScheduleAdminService publicScheduleAdminService;

		@Test
		@DisplayName("Admin PublicScheduleJob 등록 테스트 - 성공")
		void createPublicScheduleEventTestSuccess() throws Exception {
			// given
			PublicScheduleAdminCreateEventReqDto publicScheduleReqDto = PublicScheduleAdminCreateEventReqDto.builder()
				.eventTag(EventTag.JOB_FORUM)
				.title("취업설명회")
				.content("취업설명회입니다.")
				.link("https://gg.42seoul.kr")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(10))
				.build();

			// when
			mockMvc.perform(post("/admin/calendar/public/event").header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(publicScheduleReqDto)))
				.andDo(print())
				.andExpect(status().isCreated());

			// then
			List<PublicSchedule> schedules = publicScheduleAdminRepository.findByAuthor("42GG");
			assertThat(schedules).hasSize(1);
			assertThat(schedules.get(0).getTitle()).isEqualTo(publicScheduleReqDto.getTitle());
		}

		@Test
		@DisplayName("Admin PublicScheduleEvent 등록 테스트 - 실패 : 종료날짜가 시작날짜보다 빠른경우")
		public void createPublicScheduleEventWrongDateTime() throws Exception {
			try {
				PublicScheduleAdminCreateEventReqDto requestDto = PublicScheduleAdminCreateEventReqDto.builder()
					.eventTag(EventTag.JOB_FORUM)
					.title("취업설명회")
					.content("취업설명회입니다.")
					.status(ScheduleStatus.ACTIVATE)
					.link("https://gg.42seoul.kr")
					.startTime(LocalDateTime.now().plusDays(10))
					.endTime(LocalDateTime.now())
					.build();
				publicScheduleAdminService.createPublicScheduleEvent(requestDto);
			} catch (Exception e) {
				assertThat(e.getMessage()).isEqualTo("종료 시간이 시작 시간보다 빠를 수 없습니다.");
			}
		}

		@Test
		@DisplayName("Admin PublicScheduleEvent 등록 테스트 - 실패 : 제목이 50자가 넘는경우")
		public void createPublicScheduleEventTitleMax() throws Exception {

			PublicScheduleAdminCreateEventReqDto requestDto = PublicScheduleAdminCreateEventReqDto.builder()
				.eventTag(EventTag.JOB_FORUM)
				.title("TEST".repeat(13))
				.content("취업설명회입니다.")
				.status(ScheduleStatus.ACTIVATE)
				.link("https://gg.42seoul.kr")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(10))
				.build();

			mockMvc.perform(post("/admin/calendar/public/event").header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto)))
				.andDo(print())
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin PublicScheduleEvent 등록 테스트 - 실패 : 내용이 2000자가 넘는경우")
		public void createPublicScheduleEventContentMax() throws Exception {

			PublicScheduleAdminCreateEventReqDto requestDto = PublicScheduleAdminCreateEventReqDto.builder()
				.eventTag(EventTag.JOB_FORUM)
				.title("취업설명회")
				.content("취업설명회".repeat(401))
				.status(ScheduleStatus.ACTIVATE)
				.link("https://gg.42seoul.kr")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(10))
				.build();

			mockMvc.perform(post("/admin/calendar/public/event").header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto)))
				.andDo(print())
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin PublicScheduleEvent 등록 테스트 - 실패 : EventTag가 NULL인 경우")
		public void createPublicScheduleEventFailNoEventTag() throws Exception {

			PublicScheduleAdminCreateEventReqDto requestDto = PublicScheduleAdminCreateEventReqDto.builder()
				.title("취업설명회")
				.content("취업설명회")
				.status(ScheduleStatus.ACTIVATE)
				.link("https://gg.42seoul.kr")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(10))
				.build();

			mockMvc.perform(post("/admin/calendar/public/event").header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto)))
				.andDo(print())
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin PublicScheduleJob 등록 테스트 - 성공")
		void createPublicScheduleJobTestSuccess() throws Exception {
			// given
			PublicScheduleAdminCreateJobReqDto publicScheduleAdminReqDto = PublicScheduleAdminCreateJobReqDto.builder()
				.jobTag(JobTag.EXPERIENCED)
				.techTag(BACK_END)
				.title("취업설명회")
				.content("취업설명회입니다.")
				.link("https://gg.42seoul.kr")
				.status(ScheduleStatus.ACTIVATE)
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(10))
				.build();

			// when
			mockMvc.perform(post("/admin/calendar/public/job").header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(publicScheduleAdminReqDto)))
				.andDo(print())
				.andExpect(status().isCreated());

			// then
			List<PublicSchedule> schedules = publicScheduleAdminRepository.findByAuthor("42GG");
			assertThat(schedules).hasSize(1);
			assertThat(schedules.get(0).getTitle()).isEqualTo(publicScheduleAdminReqDto.getTitle());
		}

		@Test
		@DisplayName("Admin PublicScheduleJob 등록 테스트 - 실패 : 종료날짜가 시작날짜보다 빠른경우")
		public void createPublicScheduleJobWrongDateTime() throws Exception {
			try {
				PublicScheduleAdminCreateJobReqDto requestDto = PublicScheduleAdminCreateJobReqDto.builder()
					.jobTag(JobTag.EXPERIENCED)
					.techTag(BACK_END)
					.title("취업설명회")
					.content("취업설명회입니다.")
					.status(ScheduleStatus.ACTIVATE)
					.link("https://gg.42seoul.kr")
					.startTime(LocalDateTime.now().plusDays(10))
					.endTime(LocalDateTime.now())
					.build();
				publicScheduleAdminService.createPublicScheduleJob(requestDto);
			} catch (Exception e) {
				assertThat(e.getMessage()).isEqualTo("종료 시간이 시작 시간보다 빠를 수 없습니다.");
			}
		}

		@Test
		@DisplayName("Admin PublicScheduleJob 등록 테스트 - 실패 : 제목이 50자가 넘는경우")
		public void createPublicScheduleJobTitleMax() throws Exception {

			PublicScheduleAdminCreateJobReqDto requestDto = PublicScheduleAdminCreateJobReqDto.builder()
				.jobTag(JobTag.EXPERIENCED)
				.techTag(BACK_END)
				.title("TEST".repeat(13))
				.content("취업설명회입니다.")
				.status(ScheduleStatus.ACTIVATE)
				.link("https://gg.42seoul.kr")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(10))
				.build();

			mockMvc.perform(post("/admin/calendar/public/job").header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto)))
				.andDo(print())
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin PublicScheduleJob 등록 테스트 - 실패 : 내용이 2000자가 넘는경우")
		public void createPublicScheduleJobContentMax() throws Exception {

			PublicScheduleAdminCreateJobReqDto requestDto = PublicScheduleAdminCreateJobReqDto.builder()
				.jobTag(JobTag.EXPERIENCED)
				.techTag(BACK_END)
				.title("취업설명회")
				.content("취업설명회".repeat(401))
				.status(ScheduleStatus.ACTIVATE)
				.link("https://gg.42seoul.kr")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(10))
				.build();

			mockMvc.perform(post("/admin/calendar/public/job").header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto)))
				.andDo(print())
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin PublicScheduleJob 등록 테스트 - 실패 : JobTag가 NULL인 경우")
		public void createPublicScheduleJobFailNoJobTag() throws Exception {

			PublicScheduleAdminCreateJobReqDto requestDto = PublicScheduleAdminCreateJobReqDto.builder()
				.jobTag(JobTag.EXPERIENCED)
				.title("취업설명회")
				.content("취업설명회")
				.status(ScheduleStatus.ACTIVATE)
				.link("https://gg.42seoul.kr")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(10))
				.build();

			mockMvc.perform(post("/admin/calendar/public/job").header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto)))
				.andDo(print())
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin PublicScheduleJob 등록 테스트 - 실패 : TechTag가 NULL인 경우")
		public void createPublicScheduleJobFailNoTechTag() throws Exception {

			PublicScheduleAdminCreateJobReqDto requestDto = PublicScheduleAdminCreateJobReqDto.builder()
				.techTag(BACK_END)
				.title("취업설명회")
				.content("취업설명회")
				.status(ScheduleStatus.ACTIVATE)
				.link("https://gg.42seoul.kr")
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(10))
				.build();

			mockMvc.perform(post("/admin/calendar/public/job").header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto)))
				.andDo(print())
				.andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("Admin PublicSchedule 조회 테스트")
	class GetPublicScheduleAdminTest {

		@Test
		@DisplayName("Admin PublicSchedule 상세 조회 테스트 - 성공")
		void getPublicScheduleAdminDetailTestSuccess() throws Exception {
			// given
			PublicSchedule publicSchedule = publicScheduleAdminMockData.createPublicSchedule();

			// when
			String response = mockMvc.perform(
					get("/admin/calendar/public/{id}", publicSchedule.getId()).header("Authorization",
						"Bearer " + accessToken))
				.andDo(print())
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

			PublicScheduleAdminResDto result = objectMapper.readValue(response, PublicScheduleAdminResDto.class);

			// then
			assertThat(result.getId()).isEqualTo(publicSchedule.getId());
			assertThat(result.getClassification()).isEqualTo(publicSchedule.getClassification());
			assertThat(result.getEventTag()).isEqualTo(publicSchedule.getEventTag());
			assertThat(result.getJobTag()).isEqualTo(publicSchedule.getJobTag());
			assertThat(result.getTechTag()).isEqualTo(publicSchedule.getTechTag());
			assertThat(result.getAuthor()).isEqualTo(publicSchedule.getAuthor());
			assertThat(result.getTitle()).isEqualTo(publicSchedule.getTitle());
			assertThat(result.getStartTime()).isEqualTo(publicSchedule.getStartTime());
			assertThat(result.getEndTime()).isEqualTo(publicSchedule.getEndTime());
			assertThat(result.getLink()).isEqualTo(publicSchedule.getLink());
			assertThat(result.getSharedCount()).isEqualTo(publicSchedule.getSharedCount());
			assertThat(result.getStatus()).isEqualTo(publicSchedule.getStatus());
		}

		@Test
		@DisplayName("Admin PublicSchedule 상세 조회 테스트 - 실패 : 잘못된 id가 들어왔을 경우")
		void getPublicScheduleAdminDetailTestFailNotCorrectType() throws Exception {
			// given
			PublicSchedule publicSchedule = publicScheduleAdminMockData.createPublicSchedule();

			// when
			String response = mockMvc.perform(
					get("/admin/calendar/public/qweksd").header("Authorization",
						"Bearer " + accessToken))
				.andDo(print())
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

			// then
			log.info("response :{}", response);
		}

		@Test
		@DisplayName("Admin PublicSchedule 상세 조회 테스트 - 실패 : 없는 id가 들어왔을 경우")
		void getPublicScheduleAdminDetailTestFailNotFound() throws Exception {
			// given
			PublicSchedule publicSchedule = publicScheduleAdminMockData.createPublicSchedule();

			// when
			String response = mockMvc.perform(
					get("/admin/calendar/public/500123").header("Authorization",
						"Bearer " + accessToken))
				.andDo(print())
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();

			// then
			log.info("response :{}", response);
		}
	}

	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	@Nested
	@DisplayName("Admin PublicSchedule 수정 테스트")
	class UpdatePublicScheduleAdminTest {

		private Stream<Arguments> inputParams() {
			return Stream.of(
				Arguments.of(DetailClassification.EVENT, null, JobTag.SHORTS_INTERN, null), // JobTag should be null
				// TechTag should be null
				Arguments.of(DetailClassification.EVENT, EventTag.OFFICIAL_EVENT, null, TechTag.CLOUD),
				Arguments.of(DetailClassification.JOB_NOTICE, EventTag.INSTRUCTION, JobTag.INCRUIT_INTERN,
					TechTag.FRONT_END), // EventTag should be null
				Arguments.of(DetailClassification.JOB_NOTICE, null, null, TechTag.AI), // JobTag should not be null
				Arguments.of(DetailClassification.PRIVATE_SCHEDULE, EventTag.ETC, JobTag.NEW_COMER, TechTag.NETWORK)
				// Always invalid
			);
		}

		@Test
		@DisplayName("Admin PublicSchedule 수정 테스트 - 성공")
		void updatePublicScheduleAdminTestSuccess() throws Exception {
			// given
			PublicSchedule publicSchedule = publicScheduleAdminMockData.createPublicSchedule();
			PublicScheduleAdminUpdateReqDto publicScheduleAdminUpdateReqDto = PublicScheduleAdminUpdateReqDto.builder()
				.classification(DetailClassification.JOB_NOTICE)
				.jobTag(SHORTS_INTERN)
				.techTag(BACK_END)
				.title("Job Notice")
				.content("Job Notice")
				.link("https://gg.42seoul.kr")
				.startTime(LocalDateTime.now().plusDays(1))
				.endTime(LocalDateTime.now().plusDays(15))
				.build();

			// when
			String response = mockMvc.perform(
					put("/admin/calendar/public/{id}", publicSchedule.getId()).header("Authorization",
							"Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(publicScheduleAdminUpdateReqDto)))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

			PublicScheduleAdminUpdateResDto result = objectMapper.readValue(response,
				PublicScheduleAdminUpdateResDto.class);

			List<PublicSchedule> schedules = publicScheduleAdminRepository.findAll();

			// then
			assertThat(schedules.size()).isEqualTo(1);
			assertThat(result.getClassification()).isEqualTo(
				publicScheduleAdminUpdateReqDto.getClassification().name());
			assertThat(result.getJobTag()).isEqualTo(publicScheduleAdminUpdateReqDto.getJobTag());
			assertThat(result.getTechTag()).isEqualTo(publicScheduleAdminUpdateReqDto.getTechTag());
			assertThat(result.getTitle()).isEqualTo(publicScheduleAdminUpdateReqDto.getTitle());
			assertThat(result.getContent()).isEqualTo(publicScheduleAdminUpdateReqDto.getContent());
			assertThat(result.getLink()).isEqualTo(publicScheduleAdminUpdateReqDto.getLink());
			assertThat(result.getStartTime()).isEqualTo(publicScheduleAdminUpdateReqDto.getStartTime());
			assertThat(result.getEndTime()).isEqualTo(publicScheduleAdminUpdateReqDto.getEndTime());
		}

		@Test
		@DisplayName("Admin PublicSchedule 수정 테스트 - 실패 : 종료날짜가 시작날짜보다 빠른경우")
		void updatePublicScheduleAdminTestFailEndBeforeStart() throws Exception {
			// given
			PublicSchedule publicSchedule = publicScheduleAdminMockData.createPublicSchedule();
			PublicScheduleAdminUpdateReqDto publicScheduleAdminUpdateReqDto = PublicScheduleAdminUpdateReqDto.builder()
				.classification(DetailClassification.JOB_NOTICE)
				.jobTag(SHORTS_INTERN)
				.techTag(BACK_END)
				.title("Job Notice")
				.content("Job Notice")
				.link("https://gg.42seoul.kr")
				.startTime(LocalDateTime.now().plusDays(15))
				.endTime(LocalDateTime.now().plusDays(1))
				.build();

			// when
			String response = mockMvc.perform(
					put("/admin/calendar/public/{id}", publicSchedule.getId()).header("Authorization",
							"Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(publicScheduleAdminUpdateReqDto)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResponse()
				.getContentAsString();

			List<PublicSchedule> schedules = publicScheduleAdminRepository.findAll();
			PublicSchedule result = schedules.get(0);
			assertThat(schedules.size()).isEqualTo(1);
			assertThat(result.getClassification()).isEqualTo(publicSchedule.getClassification());
			assertThat(result.getEventTag()).isEqualTo(publicSchedule.getEventTag());
			assertThat(result.getTitle()).isEqualTo(publicSchedule.getTitle());
			assertThat(result.getContent()).isEqualTo(publicSchedule.getContent());
			assertThat(result.getLink()).isEqualTo(publicSchedule.getLink());
			assertThat(result.getStartTime()).isEqualTo(publicSchedule.getStartTime());
			assertThat(result.getEndTime()).isEqualTo(publicSchedule.getEndTime());
		}

		@Test
		@DisplayName("Admin PublicSchedule 수정 테스트 - 실패 : 제목이 50자가 넘는 경우")
		void updatePublicScheduleAdminTestFailTitleMax() throws Exception {
			// given
			PublicSchedule publicSchedule = publicScheduleAdminMockData.createPublicSchedule();
			PublicScheduleAdminUpdateReqDto publicScheduleAdminUpdateReqDto = PublicScheduleAdminUpdateReqDto.builder()
				.classification(DetailClassification.JOB_NOTICE)
				.jobTag(SHORTS_INTERN)
				.techTag(BACK_END)
				.title("Job Notice".repeat(10))
				.content("Job Notice")
				.link("https://gg.42seoul.kr")
				.startTime(LocalDateTime.now().plusDays(1))
				.endTime(LocalDateTime.now().plusDays(15))
				.build();

			// when
			String response = mockMvc.perform(
					put("/admin/calendar/public/{id}", publicSchedule.getId()).header("Authorization",
							"Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(publicScheduleAdminUpdateReqDto)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResponse()
				.getContentAsString();

			List<PublicSchedule> schedules = publicScheduleAdminRepository.findAll();
			PublicSchedule result = schedules.get(0);
			assertThat(schedules.size()).isEqualTo(1);
			assertThat(result.getClassification()).isEqualTo(publicSchedule.getClassification());
			assertThat(result.getEventTag()).isEqualTo(publicSchedule.getEventTag());
			assertThat(result.getTitle()).isEqualTo(publicSchedule.getTitle());
			assertThat(result.getContent()).isEqualTo(publicSchedule.getContent());
			assertThat(result.getLink()).isEqualTo(publicSchedule.getLink());
			assertThat(result.getStartTime()).isEqualTo(publicSchedule.getStartTime());
			assertThat(result.getEndTime()).isEqualTo(publicSchedule.getEndTime());
		}

		@Test
		@DisplayName("Admin PublicSchedule 수정 테스트 - 실패 : 내용이 2000자가 넘는 경우")
		void updatePublicScheduleAdminTestFailContentMax() throws Exception {
			// given
			PublicSchedule publicSchedule = publicScheduleAdminMockData.createPublicSchedule();
			PublicScheduleAdminUpdateReqDto publicScheduleAdminUpdateReqDto = PublicScheduleAdminUpdateReqDto.builder()
				.classification(DetailClassification.JOB_NOTICE)
				.jobTag(SHORTS_INTERN)
				.techTag(BACK_END)
				.title("Job Notice")
				.content("Job Notice".repeat(500))
				.link("https://gg.42seoul.kr")
				.startTime(LocalDateTime.now().plusDays(1))
				.endTime(LocalDateTime.now().plusDays(15))
				.build();

			// when
			String response = mockMvc.perform(
					put("/admin/calendar/public/{id}", publicSchedule.getId()).header("Authorization",
							"Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(publicScheduleAdminUpdateReqDto)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResponse()
				.getContentAsString();

			List<PublicSchedule> schedules = publicScheduleAdminRepository.findAll();
			PublicSchedule result = schedules.get(0);
			assertThat(schedules.size()).isEqualTo(1);
			assertThat(result.getClassification()).isEqualTo(publicSchedule.getClassification());
			assertThat(result.getEventTag()).isEqualTo(publicSchedule.getEventTag());
			assertThat(result.getTitle()).isEqualTo(publicSchedule.getTitle());
			assertThat(result.getContent()).isEqualTo(publicSchedule.getContent());
			assertThat(result.getLink()).isEqualTo(publicSchedule.getLink());
			assertThat(result.getStartTime()).isEqualTo(publicSchedule.getStartTime());
			assertThat(result.getEndTime()).isEqualTo(publicSchedule.getEndTime());
		}

		@Test
		@DisplayName("Admin PublicSchedule 수정 테스트 - 실패 : 없는 일정을 수정하는 경우")
		void updatePublicScheduleAdminTestFailNotExist() throws Exception {
			// given
			PublicSchedule publicSchedule = publicScheduleAdminMockData.createPublicSchedule();
			PublicScheduleAdminUpdateReqDto publicScheduleAdminUpdateReqDto = PublicScheduleAdminUpdateReqDto.builder()
				.classification(DetailClassification.JOB_NOTICE)
				.jobTag(SHORTS_INTERN)
				.techTag(BACK_END)
				.title("Job Notice")
				.content("Job Notice")
				.link("https://gg.42seoul.kr")
				.startTime(LocalDateTime.now().plusDays(1))
				.endTime(LocalDateTime.now().plusDays(15))
				.build();

			// when
			String response = mockMvc.perform(
					put("/admin/calendar/public/100").header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(publicScheduleAdminUpdateReqDto)))
				.andDo(print())
				.andExpect(status().isNotFound())
				.andReturn()
				.getResponse()
				.getContentAsString();

			List<PublicSchedule> schedules = publicScheduleAdminRepository.findAll();
			PublicSchedule result = schedules.get(0);
			assertThat(schedules.size()).isEqualTo(1);
			assertThat(result.getClassification()).isEqualTo(publicSchedule.getClassification());
			assertThat(result.getEventTag()).isEqualTo(publicSchedule.getEventTag());
			assertThat(result.getTitle()).isEqualTo(publicSchedule.getTitle());
			assertThat(result.getContent()).isEqualTo(publicSchedule.getContent());
			assertThat(result.getLink()).isEqualTo(publicSchedule.getLink());
			assertThat(result.getStartTime()).isEqualTo(publicSchedule.getStartTime());
			assertThat(result.getEndTime()).isEqualTo(publicSchedule.getEndTime());
		}

		@Test
		@DisplayName("Admin PublicSchedule 수정 테스트 - 실패 : 잘못된 id가 들어왔을 경우")
		void updatePublicScheduleAdminTestFailBadArgument() throws Exception {
			// given
			PublicSchedule publicSchedule = publicScheduleAdminMockData.createPublicSchedule();
			PublicScheduleAdminUpdateReqDto publicScheduleAdminUpdateReqDto = PublicScheduleAdminUpdateReqDto.builder()
				.classification(DetailClassification.JOB_NOTICE)
				.jobTag(SHORTS_INTERN)
				.techTag(BACK_END)
				.title("Job Notice")
				.content("Job Notice")
				.link("https://gg.42seoul.kr")
				.startTime(LocalDateTime.now().plusDays(1))
				.endTime(LocalDateTime.now().plusDays(15))
				.build();

			// when
			String response = mockMvc.perform(
					put("/admin/calendar/public/asdasdasd").header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(publicScheduleAdminUpdateReqDto)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResponse()
				.getContentAsString();

			List<PublicSchedule> schedules = publicScheduleAdminRepository.findAll();
			PublicSchedule result = schedules.get(0);
			assertThat(schedules.size()).isEqualTo(1);
			assertThat(result.getClassification()).isEqualTo(publicSchedule.getClassification());
			assertThat(result.getEventTag()).isEqualTo(publicSchedule.getEventTag());
			assertThat(result.getTitle()).isEqualTo(publicSchedule.getTitle());
			assertThat(result.getContent()).isEqualTo(publicSchedule.getContent());
			assertThat(result.getLink()).isEqualTo(publicSchedule.getLink());
			assertThat(result.getStartTime()).isEqualTo(publicSchedule.getStartTime());
			assertThat(result.getEndTime()).isEqualTo(publicSchedule.getEndTime());
		}

		@ParameterizedTest
		@MethodSource("inputParams")
		@DisplayName("Admin PublicSchedule 수정 테스트 - 실패 : 태그가 올바르게 매칭되지 않는 경우")
		void updatePublicScheduleAdminTestFailNotMatchTag(DetailClassification classification, EventTag eventTag,
			JobTag jobTag, TechTag techTag) throws Exception {
			// given
			PublicSchedule publicSchedule = publicScheduleAdminMockData.createPublicSchedule();
			PublicScheduleAdminUpdateReqDto publicScheduleAdminUpdateReqDto = PublicScheduleAdminUpdateReqDto.builder()
				.classification(classification)
				.eventTag(eventTag)
				.jobTag(jobTag)
				.techTag(techTag)
				.title("Job Notice")
				.content("Job Notice")
				.link("https://gg.42seoul.kr")
				.startTime(LocalDateTime.now().plusDays(1))
				.endTime(LocalDateTime.now().plusDays(15))
				.build();

			// when & then
			String response = mockMvc.perform(
					put("/admin/calendar/public/{id}", publicSchedule.getId())
						.header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(publicScheduleAdminUpdateReqDto))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResponse()
				.getContentAsString();

			// 데이터 검증
			List<PublicSchedule> schedules = publicScheduleAdminRepository.findAll();
			PublicSchedule result = schedules.get(0);
			assertThat(schedules.size()).isEqualTo(1);
			assertThat(result.getClassification()).isEqualTo(publicSchedule.getClassification());
			assertThat(result.getEventTag()).isEqualTo(publicSchedule.getEventTag());
			assertThat(result.getTitle()).isEqualTo(publicSchedule.getTitle());
			assertThat(result.getContent()).isEqualTo(publicSchedule.getContent());
			assertThat(result.getLink()).isEqualTo(publicSchedule.getLink());
			assertThat(result.getStartTime()).isEqualTo(publicSchedule.getStartTime());
			assertThat(result.getEndTime()).isEqualTo(publicSchedule.getEndTime());
		}

		@Test
		@DisplayName("Admin PublicSchedule 삭제 테스트 - 성공")
		void deletePublicScheduleAdminTestSuccess() throws Exception {
			// given
			PublicSchedule publicSchedule = publicScheduleAdminMockData.createPublicSchedule();

			// when
			mockMvc.perform(patch("/admin/calendar/public/{id}", publicSchedule.getId()).header("Authorization",
						"Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

			PublicSchedule result = publicScheduleAdminRepository.findById(publicSchedule.getId()).get();
			assertThat(result.getStatus()).isEqualTo(ScheduleStatus.DELETE);
			System.out.println("PublicSchedule Status : " + result.getStatus());
		}

		@Test
		@DisplayName("Admin PublicSchedule 삭제 테스트 - 성공 : 연관된 개인일정들 삭제")
		void deletePublicScheduleAdminTestSuccessPrivateScheduleDelete() throws Exception {
			User other = testDataUtils.createAdminUser();
			PublicSchedule publicSchedule = publicScheduleAdminMockData.createPublicSchedule();
			ScheduleGroup scheduleGroup = ScheduleGroup.builder()
				.user(other)
				.title("TEST")
				.backgroundColor("#FFFFFF")
				.build();
			ScheduleGroup scheduleGroup2 = ScheduleGroup.builder()
				.user(user)
				.title("TEST2")
				.backgroundColor("#FFFFFF")
				.build();
			scheduleGroupAdminRepository.save(scheduleGroup);
			scheduleGroupAdminRepository.save(scheduleGroup2);
			PrivateSchedule privateSchedule1 = new PrivateSchedule(other, publicSchedule, false, scheduleGroup.getId(),
				ScheduleStatus.ACTIVATE);
			PrivateSchedule privateSchedule2 = new PrivateSchedule(user, publicSchedule, false, scheduleGroup2.getId(),
				ScheduleStatus.ACTIVATE);
			privateScheduleAdminRepository.save(privateSchedule1);
			privateScheduleAdminRepository.save(privateSchedule2);
			mockMvc.perform(patch("/admin/calendar/public/{id}", publicSchedule.getId()).header("Authorization",
						"Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

			List<PrivateSchedule> privateSchedules = privateScheduleAdminRepository.findByPublicScheduleId(
				publicSchedule.getId());
			assertThat(privateSchedules.size()).isEqualTo(2);
			for (PrivateSchedule ps : privateSchedules) {
				assertThat(ps.getStatus()).isEqualTo(ScheduleStatus.DELETE);
			}
			assertThat(publicSchedule.getStatus()).isEqualTo(ScheduleStatus.DELETE);
		}

		@Test
		@DisplayName("Admin PublicSchedule 삭제 테스트 - 실패 : 잘못된 id형식이 들어왔을 경우")
		void deletePublicScheduleAdminTestFailNotBadArgument() throws Exception {
			// given
			PublicSchedule publicSchedule = publicScheduleAdminMockData.createPublicSchedule();

			// when
			mockMvc.perform(patch("/admin/calendar/public/qwe1asdv").header("Authorization",
						"Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isBadRequest());

			PublicSchedule result = publicScheduleAdminRepository.findById(publicSchedule.getId()).get();
			assertThat(result.getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
			System.out.println("PublicSchedule Status : " + result.getStatus());
		}

		@Test
		@DisplayName("Admin PublicSchedule 삭제 테스트 - 실패 : 없는 id가 들어왔을 경우")
		void deletePublicScheduleAdminTestFailNotFound() throws Exception {
			// given
			PublicSchedule publicSchedule = publicScheduleAdminMockData.createPublicSchedule();

			// when
			mockMvc.perform(patch("/admin/calendar/public/50123125").header("Authorization",
						"Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound());

			PublicSchedule result = publicScheduleAdminRepository.findById(publicSchedule.getId()).get();
			assertThat(result.getStatus()).isEqualTo(ScheduleStatus.ACTIVATE);
			System.out.println("PublicSchedule Status : " + result.getStatus());
		}

	}
}
