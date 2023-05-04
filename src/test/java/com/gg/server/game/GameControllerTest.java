package com.gg.server.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.game.GameService;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class GameControllerTest {
    @Autowired
    GameRepository gameRepository;
    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    GameService gameService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;
    private String accessToken;

    @BeforeEach
    public void init() {
        accessToken = testDataUtils.getLoginAccessToken();
    }

    @Test
    public void 일반게임목록조회() throws Exception {
        //given
        String url = "/pingpong/games/normal?count=0&pageSize=10";
        GameListResDto expect = gameService.normalGameList(0, 10);
        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(contentAsString);
        GameListResDto result = objectMapper.readValue(contentAsString, GameListResDto.class);
        //then
        System.out.println(result.getGames().size() +", " + result.getIsLast());
        System.out.println(expect.getGames().size() + ", " + expect.getIsLast());
        assertThat(result.getGames().size()).isEqualTo(expect.getGames().size());
        assertThat(result.getIsLast()).isEqualTo(expect.getIsLast());
    }

    @Test
    public void 랭크게임목록조회() throws Exception {
        //given
        String url = "/pingpong/games/rank?count=0&pageSize=10&seasonId=3";
        GameListResDto expect = gameService.normalGameList(0, 10);
        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(contentAsString);
        GameListResDto result = objectMapper.readValue(contentAsString, GameListResDto.class);
        //then
        System.out.println(result.getGames().size() +", " + result.getIsLast());
        System.out.println(expect.getGames().size() + ", " + expect.getIsLast());
        assertThat(result.getGames().size()).isEqualTo(expect.getGames().size());
        assertThat(result.getIsLast()).isEqualTo(expect.getIsLast());
    }

    @Test
    public void 랭크게임목록error조회() throws Exception {
        //given
        String url = "/pingpong/games/rank?count=0&pageSize=0";
        //then
        mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();
    }
    @Test
    public void 전체게임목록조회() throws Exception {
        //given
        String url = "/pingpong/games?count=0&pageSize=10";
        GameListResDto expect = gameService.allGameList(0, 10);
        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(contentAsString);
        GameListResDto result = objectMapper.readValue(contentAsString, GameListResDto.class);
        //then
        System.out.println(result.getGames().size() +", " + result.getIsLast());
        System.out.println(expect.getGames().size() + ", " + expect.getIsLast());
        assertThat(result.getGames().size()).isEqualTo(expect.getGames().size());
        assertThat(result.getIsLast()).isEqualTo(expect.getIsLast());
    }
}
