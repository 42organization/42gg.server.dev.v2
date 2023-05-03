package com.gg.server.admin.season;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.season.controller.SeasonAdminController;
import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.season.dto.SeasonListAdminResponseDto;
import com.gg.server.admin.season.repository.SeasonAdminRepository;
import com.gg.server.domain.season.Season;

import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
class SeasonAdminControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private SeasonAdminRepository seasonAdminRepository;

    @Autowired
    AuthTokenProvider tokenProvider;

    SeasonListAdminResponseDto responseDto;

    @BeforeEach
    void setUp() {
        Season test1 = Season.builder()
                .seasonName("test1")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .startPpp(1000)
                .pppGap(500)
                .build();
        Season test2 = Season.builder()
                .seasonName("test2")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().minusMinutes(5))
                .startPpp(1000)
                .pppGap(500)
                .build();

        seasonAdminRepository.save(test1);
        seasonAdminRepository.save(test2);

        List<Season> seasons =  seasonAdminRepository.findAll();
        List<SeasonAdminDto> dtoList = new ArrayList<>();
        for (Season season : seasons) {
            SeasonAdminDto dto = new SeasonAdminDto(season);
            dtoList.add(dto);
        }
        responseDto = SeasonListAdminResponseDto.builder()
                .seasonList(dtoList)
                .build();
    }

    @Test
    @DisplayName("[GET]pingpong/admin/seasons")
    void getAdminSeasons() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);


        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get("/pingpong/admin/seasons"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        SeasonListAdminResponseDto seasonListAdminResponseDto = objectMapper.readValue(contentAsString, SeasonListAdminResponseDto.class);
        assertThat(seasonListAdminResponseDto.getSeasonList()).isEqualTo(responseDto.getSeasonList());

    }





    //@AfterEach
    //void tearDown() {
    //}
}