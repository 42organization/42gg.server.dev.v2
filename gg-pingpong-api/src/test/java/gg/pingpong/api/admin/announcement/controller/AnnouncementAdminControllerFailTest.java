package gg.pingpong.api.admin.announcement.controller;

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

import gg.admin.repo.manage.AnnouncementAdminRepository;
import gg.auth.utils.AuthTokenProvider;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class AnnouncementAdminControllerFailTest {
	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	AuthTokenProvider tokenProvider;

	@Autowired
	AnnouncementAdminRepository announcementAdminRepository;

	@Test
	@DisplayName("fail currentPage[Get]/pingpong/admin/announcement")
	void failAnnouncementList1() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

		Integer currentPage = 0;
		Integer pageSize = 5; //페이지 사이즈 크기가 실제 디비 정보보다 큰지 확인할 것

		String url = "/pingpong/admin/announcement?page=" + currentPage + "&size=" + pageSize;

		String contentAsString = mockMvc.perform(get(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isBadRequest())
			.andReturn().getResponse().getContentAsString();
	}

	@Test
	@DisplayName("fail pageSize[Get]/pingpong/admin/announcement")
	void failAnnouncementList2() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

		Integer currentPage = 2;
		Integer pageSize = 0; //페이지 사이즈 크기가 실제 디비 정보보다 큰지 확인할 것

		String url = "/pingpong/admin/announcement?page=" + currentPage + "&size=" + pageSize;

		String contentAsString = mockMvc.perform(get(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isBadRequest())
			.andReturn().getResponse().getContentAsString();
	}
}
