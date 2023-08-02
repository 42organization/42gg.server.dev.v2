package com.gg.server.admin.coin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.announcement.dto.AnnouncementAdminAddDto;
import com.gg.server.admin.coin.data.CoinPolicyAdminRepository;
import com.gg.server.admin.coin.dto.CoinPolicyAdminAddDto;
import com.gg.server.domain.announcement.data.Announcement;
import com.gg.server.domain.announcement.exception.AnnounceNotFoundException;
import com.gg.server.domain.coin.data.CoinPolicy;
import com.gg.server.domain.coin.exception.CoinPolicyNotFoundException;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CoinPolicyAdminControllerTest {
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
    @DisplayName("[Post]/pingpong/admin/coinpolicy")
    void addAnnouncement() throws Exception {
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

        CoinPolicyAdminAddDto addDto = new CoinPolicyAdminAddDto(1,2,5,0);

        String content = objectMapper.writeValueAsString(addDto);
        String url = "/pingpong/admin/coinpolicy";

        String contentAsString = mockMvc.perform(post(url)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        CoinPolicy result = coinPolicyAdminRepository.findFirstByOrderByIdDesc().orElseThrow(()-> new CoinPolicyNotFoundException());

        assertThat(result.getAttendance()).isEqualTo(addDto.getAttendance());
        assertThat(result.getNormal()).isEqualTo(addDto.getNormal());
        assertThat(result.getRankWin()).isEqualTo(addDto.getRankWin());
        assertThat(result.getRankLose()).isEqualTo(addDto.getRankLose());
    }
}