package com.gg.server.domain.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.utils.annotation.IntegrationTest;
import com.gg.server.domain.item.service.ItemService;
import com.gg.server.utils.TestDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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