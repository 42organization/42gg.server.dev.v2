package com.gg.server.season;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.season.SeasonService;
import com.gg.server.domain.season.data.*;
import com.gg.server.domain.season.dto.*;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@WebMvcTest(SeasonController.class)
//@MockBeans({
//        @MockBean(UserRepository.class)
//})
@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
public class SeasonTestController {

    @Autowired
    MockMvc mvc;
    @Autowired
    private SeasonService seasonService;
    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    AuthTokenProvider tokenProvider;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private SeasonRepository seasonRepository;


    @BeforeEach
    @Transactional
    public void init() {
        System.out.println("before each");
        Season s1 = new Season("test1 시즌", LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), 1000, 100);
        seasonRepository.save(s1);
        seasonRepository.save(new Season("test2 season", LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), 1000, 100));
        seasonRepository.flush();
        System.out.println(seasonRepository.findAll());
    }

    @Test
    @DisplayName("시즌 조회 Test")
    @Transactional
    void season_list_test() throws Exception {
        //given
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);
        String url = "/pingpong/seasons";
        List<SeasonResDto> list = seasonService.seasonList();
        //when
        String contentAsString = mvc.perform(RestDocumentationRequestBuilders.get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        //then
        SeasonListResDto result = objectMapper.readValue(contentAsString, SeasonListResDto.class);
        assertThat(list.size()).isEqualTo(result.getSeasonList().size());
    }
}
