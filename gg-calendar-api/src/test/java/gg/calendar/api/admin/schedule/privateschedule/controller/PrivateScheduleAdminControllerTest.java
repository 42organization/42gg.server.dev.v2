package gg.calendar.api.admin.schedule.privateschedule.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.admin.repo.calendar.PrivateScheduleAdminRepository;
import gg.admin.repo.calendar.PublicScheduleAdminRepository;
import gg.calendar.api.admin.schedule.privateschedule.PrivateScheduleAdminMockData;
import gg.calendar.api.admin.schedule.privateschedule.controller.response.PrivateScheduleAdminDetailResDto;
import gg.data.calendar.PrivateSchedule;
import gg.data.calendar.PublicSchedule;
import gg.data.calendar.type.ScheduleStatus;
import gg.data.user.User;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class PrivateScheduleAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	EntityManager em;

	@Autowired
	private PrivateScheduleAdminMockData privateScheduleAdminMockData;

	@Autowired
	private PublicScheduleAdminRepository publicScheduleAdminRepository;

	@Autowired
	private PrivateScheduleAdminRepository privateScheduleAdminRepository;

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
	@DisplayName("Admin PrivateSchedule 조회 테스트")
	class PrivateScheduleDetailTest {

		@Test
		@DisplayName("Admin PrivateSchedule 상세 조회 테스트 - 성공")
		void getPrivateScheduleAdminDetailTestSuccess() throws Exception {
			// given
			PublicSchedule publicSchedule = privateScheduleAdminMockData.createPublicSchedule("42gg");
			PrivateSchedule privateSchedule = privateScheduleAdminMockData.createPrivateSchedule(publicSchedule,
				privateScheduleAdminMockData.createScheduleGroup(user));

			// when
			String response = mockMvc.perform(
					get("/calendar/admin/private/{id}", privateSchedule.getId()).header("Authorization",
						"Bearer " + accessToken))
				.andDo(print())
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

			PrivateScheduleAdminDetailResDto result = objectMapper.readValue(response,
				PrivateScheduleAdminDetailResDto.class);

			// then
			assertThat(result.getId()).isEqualTo(privateSchedule.getId());
			assertThat(result.getEventTag()).isEqualTo(privateSchedule.getPublicSchedule().getEventTag());
			assertThat(result.getJobTag()).isEqualTo(privateSchedule.getPublicSchedule().getJobTag());
			assertThat(result.getTechTag()).isEqualTo(privateSchedule.getPublicSchedule().getTechTag());
			assertThat(result.getAuthor()).isEqualTo(privateSchedule.getPublicSchedule().getAuthor());
			assertThat(result.getTitle()).isEqualTo(privateSchedule.getPublicSchedule().getTitle());
			assertThat(result.getStartTime()).isEqualTo(privateSchedule.getPublicSchedule().getStartTime());
			assertThat(result.getEndTime()).isEqualTo(privateSchedule.getPublicSchedule().getEndTime());
			assertThat(result.getLink()).isEqualTo(privateSchedule.getPublicSchedule().getLink());
		}

		@Test
		@DisplayName("Admin PrivateSchedule 상세 조회 테스트 - 실패 : 잘못된 id가 들어왔을 경우")
		void getPrivateScheduleAdminDetailTestFailNotCorrectType() throws Exception {
			// given
			PublicSchedule publicSchedule = privateScheduleAdminMockData.createPublicSchedule("42gg");
			PrivateSchedule privateSchedule = privateScheduleAdminMockData.createPrivateSchedule(publicSchedule,
				privateScheduleAdminMockData.createScheduleGroup(user));

			// when
			String response = mockMvc.perform(
					get("/calendar/admin/private/qweksd").header("Authorization",
						"Bearer " + accessToken))
				.andDo(print())
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

			// then
			log.info("response :{}", response);
		}

		@Test
		@DisplayName("Admin PrivateSchedule 상세 조회 테스트 - 실패 : 없는 id가 들어왔을 경우")
		void getPrivateScheduleAdminDetailTestFailNotFound() throws Exception {
			// given
			PublicSchedule publicSchedule = privateScheduleAdminMockData.createPublicSchedule("42gg");
			PrivateSchedule privateSchedule = privateScheduleAdminMockData.createPrivateSchedule(publicSchedule,
				privateScheduleAdminMockData.createScheduleGroup(user));

			// when
			String response = mockMvc.perform(
					get("/calendar/admin/private/500123").header("Authorization",
						"Bearer " + accessToken))
				.andDo(print())
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();

			// then
			log.info("response :{}", response);
		}

		@Test
		@DisplayName("Admin PrivateSchedule 상세 조회 테스트 - 실패 : 없는 groupId가 들어왔을 경우")
		void getPrivateScheduleAdminDetailTestFailNotFoundGroupId() throws Exception {
			// given
			PublicSchedule publicSchedule = privateScheduleAdminMockData.createPublicSchedule("42gg");
			PrivateSchedule privateSchedule = privateScheduleAdminMockData.createPrivateScheduleNoGroup(publicSchedule,
				user);

			// when
			String response = mockMvc.perform(
					get("/calendar/admin/private/{id}", privateSchedule.getId()).header("Authorization",
						"Bearer " + accessToken))
				.andDo(print())
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();

			// then
			log.info("response :{}", response);
		}
	}

	@Nested
	@DisplayName("Admin PrivateSchedule 삭제 테스트")
	class DeletePrivateScheduleAdminTest {

		@Test
		@DisplayName("Admin PrivateSchedule 삭제 테스트 - 성공")
		void deletePrivateScheduleAdminTestSuccess() throws Exception {
			// given
			PublicSchedule publicSchedule = privateScheduleAdminMockData.createPublicPrivateSchedule("42gg");
			PrivateSchedule privateSchedule = privateScheduleAdminMockData.createPrivateSchedule(publicSchedule,
				privateScheduleAdminMockData.createScheduleGroup(user));

			// when
			mockMvc.perform(
					patch("/calendar/admin/private/{id}", privateSchedule.getId()).header("Authorization",
						"Bearer " + accessToken))
				.andDo(print())
				.andExpect(status().isOk());

			// then
			assertThat(publicSchedule.getStatus()).isEqualTo(ScheduleStatus.DELETE);
			assertThat(privateSchedule.getStatus()).isEqualTo(ScheduleStatus.DELETE);
		}

		@Test
		@DisplayName("Admin PrivateSchedule 삭제 테스트 - 실패 : 잘못된 id가 들어왔을 경우")
		void deletePrivateScheduleAdminTestFailNotCorrectType() throws Exception {
			// given
			PublicSchedule publicSchedule = privateScheduleAdminMockData.createPublicPrivateSchedule("42gg");
			PrivateSchedule privateSchedule = privateScheduleAdminMockData.createPrivateSchedule(publicSchedule,
				privateScheduleAdminMockData.createScheduleGroup(user));

			// when
			String response = mockMvc.perform(
					patch("/calendar/admin/private/qweksd").header("Authorization",
						"Bearer " + accessToken))
				.andDo(print())
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

			// then
			log.info("response :{}", response);
		}

		@Test
		@DisplayName("Admin PrivateSchedule 삭제 테스트 - 실패 : 없는 id가 들어왔을 경우")
		void deletePrivateScheduleAdminTestFailNotFound() throws Exception {
			// given
			PublicSchedule publicSchedule = privateScheduleAdminMockData.createPublicPrivateSchedule("42gg");
			PrivateSchedule privateSchedule = privateScheduleAdminMockData.createPrivateSchedule(publicSchedule,
				privateScheduleAdminMockData.createScheduleGroup(user));

			// when
			String response = mockMvc.perform(
					patch("/calendar/admin/private/500123").header("Authorization",
						"Bearer " + accessToken))
				.andDo(print())
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();

			// then
			log.info("response :{}", response);
		}

		@Test
		@DisplayName("Admin PrivateSchedule 삭제 테스트 - 실패 : 상세분류가 개인일정이 아닌 경우")
		void deletePrivateScheduleAdminTestFailNotPrivateSchedule() throws Exception {
			// given
			PublicSchedule publicSchedule = privateScheduleAdminMockData.createPublicSchedule("42gg");
			publicScheduleAdminRepository.save(publicSchedule);
			PrivateSchedule privateSchedule = privateScheduleAdminMockData.createPrivateSchedule(publicSchedule,
				privateScheduleAdminMockData.createScheduleGroup(user));

			// when
			String response = mockMvc.perform(
					patch("/calendar/admin/private/{id}", privateSchedule.getId()).header("Authorization",
						"Bearer " + accessToken))
				.andDo(print())
				.andExpect(status().isForbidden()).andReturn().getResponse().getContentAsString();

			// then
			log.info("response :{}", response);
		}
	}
}
