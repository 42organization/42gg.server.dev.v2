package com.gg.server.admin.megaphone.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import com.gg.server.utils.annotation.IntegrationTest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class MegaphoneAdminControllerTest {
	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	AuthTokenProvider tokenProvider;

	@Test
	@DisplayName("[GET] /pingpong/admin/megaphones/history?page={page}&size={pageSize}&intraId={intraId}")
	void getMegaphoneListTest() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		tokenProvider.getUserIdFromAccessToken(accessToken);
		String url = "/pingpong/admin/megaphones/history?page=1&size=30";

		String contentAsString = mockMvc.perform(get(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		System.out.println(contentAsString);
	}
}
