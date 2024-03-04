package gg.pingpong.api.user.item.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.pingpong.api.user.store.service.ItemService;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class UserItemResponseControllerTest {

	@Autowired
	ItemService itemService;

	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	MockMvc mockMvc;

	@Test
	@DisplayName("GET /pingpong/items?page=1&size=20")
	public void getItemByUser() throws Exception {
		String accessToken = testDataUtils.getLoginAccessToken();

		Integer page = 1;
		Integer size = 20;

		String url = "/pingpong/items?page=" + page + "&size=" + size;

		mockMvc.perform(get(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
	}
}
