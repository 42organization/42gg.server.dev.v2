package com.gg.server.admin.coin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.coin.data.CoinPolicyAdminRepository;
import com.gg.server.admin.coin.dto.CoinPolicyAdminAddDto;
import com.gg.server.utils.annotation.IntegrationTest;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        CoinPolicyAdminAddDto addDto = new CoinPolicyAdminAddDto(1,2,5,-1);

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
