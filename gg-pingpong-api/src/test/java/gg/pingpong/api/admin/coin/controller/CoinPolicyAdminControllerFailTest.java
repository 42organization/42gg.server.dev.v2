package gg.pingpong.api.admin.coin.controller;

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

import gg.pingpong.admin.repo.coin.CoinPolicyAdminRepository;
import gg.pingpong.api.admin.coin.dto.CoinPolicyAdminAddDto;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.utils.TestDataUtils;
import gg.pingpong.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class CoinPolicyAdminControllerFailTest {
	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	AuthTokenProvider tokenProvider;

	@Autowired
	CoinPolicyAdminRepository coinPolicyAdminRepository;

	@Test
	@DisplayName("[Post FAIL]/pingpong/admin/coinpolicy")
	void addAnnouncement() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

		CoinPolicyAdminAddDto addDto = new CoinPolicyAdminAddDto(1, 2, 5, -1);

		String content = objectMapper.writeValueAsString(addDto);
		String url = "/pingpong/admin/coinpolicy";

		String contentAsString = mockMvc.perform(post(url)
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isBadRequest())
			.andReturn().getResponse().getContentAsString();
	}
}
