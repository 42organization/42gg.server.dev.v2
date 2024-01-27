package com.gg.server.domain.item.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.item.service.ItemService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import com.gg.server.utils.annotation.IntegrationTest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class ItemPurchaseControllerTest {

	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	AuthTokenProvider tokenProvider;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ItemService itemService;

	@Test
	@DisplayName("[Post]/pingpong/items/purchases/{itemId} - success")
	public void purchaseItemSuccessTest() throws Exception {

		// given
		Long testItemId = 1L;
		UserDto testUser = UserDto.builder()
			.id(1L)
			.intraId("testIntraId")
			.build();

		doNothing().when(itemService).purchaseItem(testItemId, testUser);

		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		System.out.println(userId);
		String url = "/pingpong/items/purchases/" + testItemId;

		// when
		mockMvc.perform(post(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isCreated());

	}
}
