package com.gg.server.admin.season;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.season.dto.SeasonAdminDto;
import com.gg.server.admin.season.dto.SeasonListAdminResponseDto;
import com.gg.server.admin.season.data.SeasonAdminRepository;

import com.gg.server.domain.season.data.Season;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Autowired
    TestDataUtils testDataUtils;

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
        responseDto = new SeasonListAdminResponseDto(dtoList);
    }

    @Test
    @DisplayName("[GET]pingpong/admin/seasons")
    void getAdminSeasons() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);


        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get("/pingpong/admin/seasons")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        SeasonListAdminResponseDto seasonListAdminResponseDto = objectMapper.readValue(contentAsString, SeasonListAdminResponseDto.class);
        assertThat(seasonListAdminResponseDto.getSeasonList().size()).isEqualTo(responseDto.getSeasonList().size());
        assertThat(seasonListAdminResponseDto.getSeasonList().get(0).getSeasonName()).isEqualTo(responseDto.getSeasonList().get(0).getSeasonName());
        assertThat(seasonListAdminResponseDto.getSeasonList().get(0).getStartTime()).isEqualTo(responseDto.getSeasonList().get(0).getStartTime());
        assertThat(seasonListAdminResponseDto.getSeasonList().get(0).getEndTime()).isEqualTo(responseDto.getSeasonList().get(0).getEndTime());
        assertThat(seasonListAdminResponseDto.getSeasonList().get(0).getStartPpp()).isEqualTo(responseDto.getSeasonList().get(0).getStartPpp());
        assertThat(seasonListAdminResponseDto.getSeasonList().get(0).getPppGap()).isEqualTo(responseDto.getSeasonList().get(0).getPppGap());

        assertThat(seasonListAdminResponseDto.getSeasonList().get(1).getSeasonName()).isEqualTo(responseDto.getSeasonList().get(1).getSeasonName());
        assertThat(seasonListAdminResponseDto.getSeasonList().get(1).getStartTime()).isEqualTo(responseDto.getSeasonList().get(1).getStartTime());
        assertThat(seasonListAdminResponseDto.getSeasonList().get(1).getEndTime()).isEqualTo(responseDto.getSeasonList().get(1).getEndTime());
        assertThat(seasonListAdminResponseDto.getSeasonList().get(1).getStartPpp()).isEqualTo(responseDto.getSeasonList().get(1).getStartPpp());
        assertThat(seasonListAdminResponseDto.getSeasonList().get(1).getPppGap()).isEqualTo(responseDto.getSeasonList().get(1).getPppGap());
    }





    //@AfterEach
    //void tearDown() {
    //}
}