package gg.pingpong.api.user.feedback.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.user.feedback.controller.request.FeedbackRequestDto;
import gg.pingpong.data.manage.Feedback;
import gg.pingpong.data.manage.type.FeedbackType;
import gg.pingpong.repo.feedback.FeedbackRepository;
import gg.pingpong.utils.TestDataUtils;
import gg.pingpong.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
class FeedbackControllerTest {
	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	AuthTokenProvider tokenProvider;

	@Autowired
	FeedbackRepository feedbackRepository;

	@Test
	@Transactional
	@DisplayName("[Post]/pingpong/feedback")
	void getAnnouncementList() throws Exception {
		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		System.out.println(userId);

		FeedbackRequestDto addDto = FeedbackRequestDto.builder()
			.content("하나하나둘둘테스트")
			.category(FeedbackType.ETC)
			.build();

		String content = objectMapper.writeValueAsString(addDto);
		String url = "/pingpong/feedback";

		String contentAsString = mockMvc.perform(post(url)
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isCreated())
			.andReturn().getResponse().getContentAsString();

		Feedback result = feedbackRepository.findFirstByOrderByIdDesc();
		assertThat(result.getCategory()).isEqualTo(addDto.getCategory());
		assertThat(result.getContent()).isEqualTo(addDto.getContent());

		System.out.println(result.getId() + ", " + result.getUser().getIntraId());
	}

}
