package com.gg.server.admin.receipt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.receipt.data.ReceiptAdminRepository;
import com.gg.server.admin.receipt.dto.ReceiptListResponseDto;
import com.gg.server.admin.receipt.service.ReceiptAdminService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReceiptAdminControllerTest {
    @Autowired
    ReceiptAdminService receiptAdminService;
    @Autowired
    ReceiptAdminRepository receiptAdminRepository;
    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    AuthTokenProvider tokenProvider;
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("GET /pingpong/admin/receipt/list")
    public void getAllReceipt() throws Exception {
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        Integer page = 1;
        Integer size = 20;
        String url = "/pingpong/admin/receipt/list?page=" + page + "&size=" + size;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        String contentAsString = mockMvc.perform(get(url)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ReceiptListResponseDto expect = receiptAdminService.getAllReceipt(pageable);
        ReceiptListResponseDto result = objectMapper.readValue(contentAsString, ReceiptListResponseDto.class);
        assertThat(result.getReceiptList().get(0).getReceiptId()).isEqualTo(expect.getReceiptList().get(0).getReceiptId());
        assertThat(result.getReceiptList().get(0).getCreatedAt()).isEqualTo(expect.getReceiptList().get(0).getCreatedAt());
        assertThat(result.getReceiptList().get(0).getItemName()).isEqualTo(expect.getReceiptList().get(0).getItemName());
        assertThat(result.getReceiptList().get(0).getItemPrice()).isEqualTo(expect.getReceiptList().get(0).getItemPrice());
    }

    @Test
    @DisplayName("GET /pingpong/admin/receipt/list")
    public void findByIntraId() throws Exception {
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        Integer page = 1;
        Integer size = 20;
        String intraId = "sishin";
        String url = "/pingpong/admin/receipt/list?page=" + page + "&size=" + size + "&intraId=" + intraId;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        String contentAsString = mockMvc.perform(get(url)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ReceiptListResponseDto expect = receiptAdminService.findByIntraId(intraId, pageable);
        ReceiptListResponseDto result = objectMapper.readValue(contentAsString, ReceiptListResponseDto.class);
        assertThat(result.getReceiptList().get(0).getReceiptId()).isEqualTo(expect.getReceiptList().get(0).getReceiptId());
        assertThat(result.getReceiptList().get(0).getCreatedAt()).isEqualTo(expect.getReceiptList().get(0).getCreatedAt());
        assertThat(result.getReceiptList().get(0).getItemName()).isEqualTo(expect.getReceiptList().get(0).getItemName());
        assertThat(result.getReceiptList().get(0).getItemPrice()).isEqualTo(expect.getReceiptList().get(0).getItemPrice());
    }
}