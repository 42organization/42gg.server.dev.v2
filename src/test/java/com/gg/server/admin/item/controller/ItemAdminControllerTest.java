package com.gg.server.admin.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.item.data.ItemAdminRepository;
import com.gg.server.admin.item.dto.ItemListResponseDto;
import com.gg.server.admin.item.service.ItemAdminService;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;


import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemAdminControllerTest {
    @Autowired
    ItemAdminService itemAdminService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    AuthTokenProvider tokenProvider;
    @Autowired
    ItemAdminRepository itemAdminRepository;
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("GET /pingpong/admin/items/history")
    public void getAllItemHistoryTest() throws Exception {
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        Integer page = 1;
        Integer size = 20;
        String url = "/pingpong/admin/items/history?page=" + page + "&size=" + size;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        String contentAsString = mockMvc.perform(get(url)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);
        ItemListResponseDto expect = itemAdminService.getAllItemHistory(pageable);
        System.out.println(expect.getHistoryList());
        ItemListResponseDto result = objectMapper.readValue(contentAsString, ItemListResponseDto.class);
        System.out.println(expect.getHistoryList().get(0));
        System.out.println(result.getHistoryList().get(0));
        assertThat(result.getHistoryList().get(0).getItemId());
        assertThat(result.getHistoryList().get(0).getName());
        assertThat(result.getHistoryList().get(0).getMainContent());
        assertThat(result.getHistoryList().get(0).getSubContent());
        assertThat(result.getHistoryList().get(0).getPrice());
    }

    @Test
    @DisplayName("PUT /pingpong/admin/items/history/{itemId}")
    public void updateItemTest() throws Exception {
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        String requestJson = "{\"name\" : \"확성기\", \"content\" : \"testing\", \"imageUri\" : \"https://kakao.com\", \"price\" : 42, \"discount\" : 50, \"creatorIntraId\" : \"cheolee\"}";
        String contentAsString = mockMvc.perform(put("/pingpong/admin/items/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString();
        System.out.println(contentAsString);
    }

    @Test
    @DisplayName("DELETE /pingpong/admin/items/{itemId}")
    public void deleteItemTest() throws Exception {
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        String requestJson = "{\"deleterIntraId\" : \"sishin\"}";
        String contentAsString = mockMvc.perform(delete("/pingpong/admin/items/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString();
        System.out.println(contentAsString);
    }
}