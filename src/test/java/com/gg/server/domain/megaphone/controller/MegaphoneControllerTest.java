package com.gg.server.domain.megaphone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.megaphone.data.Megaphone;
import com.gg.server.domain.megaphone.data.MegaphoneRepository;
import com.gg.server.domain.megaphone.dto.MegaphoneUseRequestDto;
import com.gg.server.domain.receipt.data.Receipt;
import com.gg.server.domain.receipt.data.ReceiptRepository;
import com.gg.server.domain.receipt.type.ItemStatus;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
class MegaphoneControllerTest {
    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;

    @Autowired
    MegaphoneRepository megaphoneRepository;

    @Autowired
    ReceiptRepository receiptRepository;

    @Test
    @Transactional
    @DisplayName("[Post] /pingpong/megaphones")
    void useMegaphoneTest() throws Exception {
        String intraId = "intra";
        String email = "email";
        User newUser = testDataUtils.createNewUser(intraId, email, RacketType.PENHOLDER,
                SnsType.BOTH, RoleType.ADMIN);
        String accessToken = tokenProvider.createToken(newUser.getId());
        // db에 저장해두고 테스트
        Receipt receipt = receiptRepository.findById(1L).get();
        MegaphoneUseRequestDto megaphoneUseRequestDto = new MegaphoneUseRequestDto(receipt.getId(), "test");
        String content = objectMapper.writeValueAsString(megaphoneUseRequestDto);
        String url = "/pingpong/megaphones";

        mockMvc.perform(post(url)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

        Megaphone result = megaphoneRepository.findFirstByOrderByIdDesc();
        assertThat(result.getContent()).isEqualTo(megaphoneUseRequestDto.getContent());
    }

    @Test
    @Transactional
    @DisplayName("DELETE /pingpong/megaphones/{megaphoneId}")
    public void deleteMegaphoneTest() throws Exception {
        //given
        String intraId = "intra";
        String email = "email";
        String imageUrl = "imageUrl";
        User newUser = testDataUtils.createNewUser(intraId, email, RacketType.PENHOLDER,
                SnsType.BOTH, RoleType.ADMIN);
        String accessToken = tokenProvider.createToken(newUser.getId());
        Receipt receipt = receiptRepository.findById(2L).get();
        String url = "/pingpong/megaphones/2";

        //when
        mockMvc.perform(delete(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse();

        //then
        AssertionsForClassTypes.assertThat(receipt.getStatus()).isEqualTo(ItemStatus.DELETED);
    }

    @Test
    @Transactional
    @DisplayName("[GET] /pingpong/megaphones/receipt/{receiptId}")
    void getMegaphoneDetailTest() throws Exception {
        String intraId = "intra";
        String email = "email";
        User newUser = testDataUtils.createNewUser(intraId, email, RacketType.PENHOLDER,
                SnsType.BOTH, RoleType.ADMIN);
        String accessToken = tokenProvider.createToken(newUser.getId());
        Receipt receipt = receiptRepository.findById(1L).get();
        String url = "/pingpong/megaphones/receipt/" + receipt.getId();

        String contentAsString = mockMvc.perform(get(url)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);
    }

    @Test
    @Transactional
    @DisplayName("[GET] /pingpong/megaphones")
    void getMegaphoneTodayListTest() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        String url = "/pingpong/megaphones";
        mockMvc.perform(get(url)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
    }
}