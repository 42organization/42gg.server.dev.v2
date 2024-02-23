package gg.pingpong.api.user.announcement.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.data.manage.Announcement;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.manage.AnnouncementRepository;
import gg.pingpong.utils.TestDataUtils;
import gg.pingpong.utils.annotation.IntegrationTest;
import gg.pingpong.utils.exception.announcement.AnnounceNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
class AnnouncementControllerTest {
	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	AuthTokenProvider tokenProvider;

	@Autowired
	AnnouncementRepository announcementRepository;

	@BeforeEach
	void setUp() {
		User admin = testDataUtils.createAdminUser();
		testDataUtils.createAnnouncements(admin, 5);
	}

	@Test
	@Transactional
	@DisplayName("[GET]/pingpong/announcement")
	void getAnnouncement() throws Exception {
		String accessToken = testDataUtils.getLoginAccessToken();

		String contentAsString = mockMvc.perform(get("/pingpong/announcement")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		System.out.println(contentAsString);
	}

	@Test
	@Transactional
	@DisplayName("[GET]/pingpong/announcement")
	void getAnnouncementEmpty() throws Exception {
		String accessToken = testDataUtils.getLoginAccessToken();
		Announcement announcement = announcementRepository.findFirstByOrderByIdDesc()
			.orElseThrow(AnnounceNotFoundException::new);

		announcement.update("testId", LocalDateTime.now());

		String contentAsString = mockMvc.perform(get("/pingpong/announcement")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		System.out.println(contentAsString);
	}

}
