package gg.pingpong.api.admin.feedback.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.pingpong.admin.repo.feedback.FeedbackAdminRepository;
import gg.pingpong.api.admin.feedback.dto.FeedbackListAdminResponseDto;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.utils.TestDataUtils;
import gg.pingpong.api.utils.annotation.IntegrationTest;
import gg.pingpong.data.manage.Feedback;
import gg.pingpong.data.manage.type.FeedbackType;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.user.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class FeedbackAdminControllerTest {
	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	AuthTokenProvider tokenProvider;

	@Autowired
	FeedbackAdminRepository feedbackAdminRepository;
	@Autowired
	UserRepository userRepository;

	@Test
	@DisplayName("[Get]/pingpong/admin/feedback")
	void getFeedback() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();

		for (int i = 0; i < 6; i++) {
			Feedback feedback = Feedback.builder()
				.category(FeedbackType.ETC)
				.content("test" + i)
				.user(testDataUtils.createNewUser())
				.build();
			feedbackAdminRepository.save(feedback);
		}

		Integer currentPage = 1;
		Integer pageSize = 5; //페이지 사이즈 크기가 실제 디비 정보보다 큰지 확인할 것

		String url = "/pingpong/admin/feedback?page=" + currentPage + "&size=" + pageSize;

		String contentAsString = mockMvc.perform(get(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		FeedbackListAdminResponseDto result = objectMapper.readValue(contentAsString,
			FeedbackListAdminResponseDto.class);
		assertThat(result.getFeedbackList().size()).isEqualTo(5);
		System.out.println(result.getFeedbackList().get(0).getId());
		System.out.println(result.getFeedbackList().get(0).getContent());

	}

	@Test
	@DisplayName("[Patch]pingpong/admin/feedback/{id}")
	void patchFeedback() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

		Feedback feedback = Feedback.builder()
			.category(FeedbackType.ETC)
			.content("test1234")
			.user(userRepository.findById(userId).get())
			.build();
		feedbackAdminRepository.save(feedback);

		String url = "/pingpong/admin/feedback/" + feedback.getId().toString();
		Boolean status = feedback.getIsSolved();

		String contentAsString = mockMvc.perform(patch(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isNoContent())
			.andReturn().getResponse().getContentAsString();

	}

	@Test
	@DisplayName("[get]pingpong/admin/feedback?intraId=${intraId}&page=${pageNumber}&size={size}")
	void findFeedbackByIntraId() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

		User user = userRepository.findById(userId).get();

		Feedback feedback = Feedback.builder()
			.category(FeedbackType.ETC)
			.content("test1234")
			.user(user)
			.build();
		feedbackAdminRepository.save(feedback);

		Integer currentPage = 1;
		Integer pageSize = 5; //페이지 사이즈 크기가 실제 디비 정보보다 큰지 확인할 것

		String url =
			"/pingpong/admin/feedback?intraId=" + user.getIntraId() + "&page=" + currentPage + "&size=" + pageSize;
		Boolean status = feedback.getIsSolved();

		String contentAsString = mockMvc.perform(get(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		FeedbackListAdminResponseDto result = objectMapper.readValue(contentAsString,
			FeedbackListAdminResponseDto.class);
		assertThat(result.getFeedbackList().size()).isBetween(0, 5);
		assertThat(result.getFeedbackList().get(0).getIntraId()).isEqualTo(user.getIntraId());
		assertThat(result.getFeedbackList().get(0).getContent()).isEqualTo("test1234");
		assertThat(result.getFeedbackList().get(0).getIntraId()).isEqualTo(user.getIntraId());
	}
}
