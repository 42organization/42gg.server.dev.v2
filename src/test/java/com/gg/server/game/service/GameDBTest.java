package com.gg.server.game.service;


import com.gg.server.admin.game.service.GameAdminService;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.service.GameFindService;
import com.gg.server.domain.pchange.data.PChangeRepository;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamRepository;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RequiredArgsConstructor
@AutoConfigureMockMvc
@Transactional
@Slf4j
public class GameDBTest {

    @Autowired
    GameFindService gameFindService;
    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    AuthTokenProvider tokenProvider;
    @Autowired
    RankRedisRepository rankRedisRepository;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    GameAdminService gameAdminService;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TeamUserRepository teamUserRepository;
    @Autowired
    PChangeRepository pChangeRepository;
    @Autowired
    EntityManager em;
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName(value = "Cascade 종속삭제테스트")
    @Transactional
    public void Cascade종속삭제테스트() throws Exception {
        pChangeRepository.deleteAll();
        gameRepository.deleteAll();
        em.flush();
        List<Game> gameList = gameRepository.findAll();
        List<Team> teamList = teamRepository.findAll();
        List<TeamUser> teamUserList = teamUserRepository.findAll();
        log.info("GAME LIST SIZE : " + Integer.toString(gameList.size()));
        log.info("TEAM LIST SIZE: " + Integer.toString(teamList.size()));
        log.info("TEAM_USER LIST SIZE: " + Integer.toString(teamUserList.size()));
        Assertions.assertThat(teamList.size()).isEqualTo(0);
        Assertions.assertThat(teamUserList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName(value = "game 전적조회 쿼리 수 테스트")
    @Transactional
    public void 게임전적조회쿼리테스트() throws Exception {
        //given
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
        String url = "/pingpong/admin/games?page=1&seasonId=4";

        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }
}
