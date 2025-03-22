package gg.calendar.api.user.schedule.publicschedule.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.calendar.api.user.schedule.publicschedule.PublicScheduleMockData;
import gg.data.calendar.type.DetailClassification;
import gg.data.calendar.type.EventTag;
import gg.data.calendar.type.JobTag;
import gg.data.user.User;
import gg.repo.calendar.PrivateScheduleRepository;
import gg.repo.calendar.PublicScheduleRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc
class TotalSheduleControllerTest {
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
	EntityManager em;

	@Autowired
	private PublicScheduleMockData mockData;

	@BeforeEach
	void setUp() {
		user = testDataUtils.createNewUser();
		accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
	}

	@Nested
	@DisplayName("전체일정 조회하기_TotalSchedule")
	class RetrieveTotalSchedule {
		@Test
		@DisplayName("[200]전체일정 42Event 조회성공")
		void retrieveTotalScheduleSuccess() throws Exception {
			// given
			mockData.createPublicScheduleEvent(7);
			LocalDateTime start = LocalDateTime.now().plusDays(0);
			LocalDateTime end = LocalDateTime.now().plusDays(7);

			// when
			mockMvc.perform(get("/calendar").header("Authorization", "Bearer " + accessToken)
				.param("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.param("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).andExpect(status().isOk());
			// then
			assertAll(() -> assertEquals(7, publicScheduleRepository.findAll().size()),
				() -> assertEquals("42GG", publicScheduleRepository.findAll().get(0).getAuthor()),
				() -> assertEquals("Job 0", publicScheduleRepository.findAll().get(0).getTitle()),
				() -> assertEquals("TEST EVENT", publicScheduleRepository.findAll().get(0).getContent()),
				() -> assertEquals("https://gg.42seoul.kr", publicScheduleRepository.findAll().get(0).getLink()),
				() -> assertEquals(DetailClassification.EVENT,
					publicScheduleRepository.findAll().get(0).getClassification()),
				() -> assertEquals(EventTag.JOB_FORUM, publicScheduleRepository.findAll().get(0).getEventTag()));
		}

		@Test
		@DisplayName("[200]전체일정 JOB TAG 조회성공")
		void retrieveJobTagTotalScheduleSuccess() throws Exception {
			// given
			mockData.createPublicScheduleJob(7);
			LocalDateTime start = LocalDateTime.now().plusDays(0);
			LocalDateTime end = LocalDateTime.now().plusDays(7);

			// when
			mockMvc.perform(get("/calendar").header("Authorization", "Bearer " + accessToken)
				.param("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.param("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).andExpect(status().isOk());
			// then
			assertAll(() -> assertEquals(7, publicScheduleRepository.findAll().size()),
				() -> assertEquals("42GG", publicScheduleRepository.findAll().get(0).getAuthor()),
				() -> assertEquals("Job 0", publicScheduleRepository.findAll().get(0).getTitle()),
				() -> assertEquals("TEST JOB", publicScheduleRepository.findAll().get(0).getContent()),
				() -> assertEquals("https://gg.42seoul.kr", publicScheduleRepository.findAll().get(0).getLink()),
				() -> assertEquals(DetailClassification.JOB_NOTICE,
					publicScheduleRepository.findAll().get(0).getClassification()),
				() -> assertEquals(JobTag.EXPERIENCED, publicScheduleRepository.findAll().get(0).getJobTag()));
		}

		@Test
		@DisplayName("[400] 전체일정조회실패 - 조회기간이 잘못된 경우(종료날짜가 시작날짜보다 빠를 때)")
		void retrieveTotalScheduleFailFaultPeriod() throws Exception {
			// given
			mockData.createPublicScheduleEvent(7);
			LocalDateTime start = LocalDateTime.now().plusDays(0);
			LocalDateTime end = LocalDateTime.now().minusDays(7);

			// when & then
			mockMvc.perform(get("/calendar").header("Authorization", "Bearer " + accessToken)
					.param("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
					.param("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("[400] 전체일정조회실패 - 날짜형식이 잘못된 경우)")
		void retrieveTotalScheduleFailFault() throws Exception {
			// given
			mockData.createPublicScheduleEvent(7);
			LocalDateTime start = LocalDateTime.now().plusDays(0);
			LocalDateTime end = LocalDateTime.now().plusDays(7);
			// when & then
			mockMvc.perform(get("/calendar").header("Authorization", "Bearer " + accessToken)
					.param("start", start.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
					.param("end", end.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))))
				.andExpect(status().isBadRequest());
		}
	}
}
