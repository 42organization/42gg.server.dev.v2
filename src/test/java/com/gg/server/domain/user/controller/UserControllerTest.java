package com.gg.server.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.*;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RankRedisRepository redisRepository;

    @Test
    @DisplayName("live")
    public void userLiveTest() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);
        String url = "/pingpong/users/live";
        String event = "game";
        int notiCnt = 2;
        String currentMatchMode = "RANK";
        testDataUtils.addMockDataUserLiveApi(event, notiCnt, currentMatchMode, userId);
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserLiveResponseDto userLiveResponseDto = objectMapper.readValue(contentAsString, UserLiveResponseDto.class);
        assertThat(userLiveResponseDto.getEvent()).isEqualTo(event);
        assertThat(userLiveResponseDto.getNotiCount()).isEqualTo(notiCnt);
        assertThat(userLiveResponseDto.getCurrentMatchMode()).isEqualTo(currentMatchMode);
    }
    
    @Test
    @DisplayName("/")
    public void userNormalDetail () throws Exception {
        //given
        String url = "/pingpong/users";
        String intraId = "intra";
        String email = "email";
        String imageUrl = "imageUrl";
        User newUser = testDataUtils.createNewUser(intraId, email, imageUrl, RacketType.PENHOLDER,
                SnsType.BOTH, RoleType.ADMIN);
        String accessToken = tokenProvider.createToken(newUser.getId());

        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserNormalDetailResponseDto responseDto = objectMapper.readValue(contentAsString, UserNormalDetailResponseDto.class);

        //then
        assertThat(responseDto.getIntraId()).isEqualTo(intraId);
        assertThat(responseDto.getUserImageUrl()).isEqualTo(imageUrl);
        assertThat(responseDto.getIsAdmin()).isTrue();
    }

    @Test
    @DisplayName("searches?inquiringString=${IntraId}")
    public void searchUser() throws Exception
    {
        //given
        String intraId[] = {"intraId", "2intra2", "2intra", "aaaa", "bbbb"};
        String email = "email";
        String imageUrl = "imageUrl";
        User user = null;
        for (String intra : intraId) {
            user = testDataUtils.createNewUser(intra, email, imageUrl, RacketType.PENHOLDER,
                    SnsType.BOTH, RoleType.ADMIN);
        }
        String accessToken = tokenProvider.createToken(user.getId());
        String keyWord = "intra";
        String url = "/pingpong/users/searches?inquiringString=" + keyWord;

        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserSearchResponseDto userSearchResponseDto = objectMapper.readValue(contentAsString, UserSearchResponseDto.class);

        //then
        assertThat(userSearchResponseDto.getUsers().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("{targetId}/detail")
    public void getUserDetail () throws Exception
    {
        //given
        Season season = testDataUtils.createSeason();
        String intraId = "intraId";
        String email = "email";
        String imageUrl = "imageUrl";
        String statusMessage = "statusMessage";
        User newUser = testDataUtils.createNewUser(intraId, email, imageUrl, RacketType.PENHOLDER,
                SnsType.BOTH, RoleType.ADMIN);
        String accessToken = tokenProvider.createToken(newUser.getId());
        testDataUtils.createUserRank(newUser, statusMessage, season);
        String url = "/pingpong/users/" + newUser.getIntraId() + "/detail";

        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        UserDetailResponseDto responseDto = objectMapper.readValue(contentAsString, UserDetailResponseDto.class);

        //then
        Assertions.assertThat(responseDto.getIntraId()).isEqualTo(intraId);
        Assertions.assertThat(responseDto.getUserImageUri()).isEqualTo(imageUrl);
        Assertions.assertThat(responseDto.getStatusMessage()).isEqualTo(statusMessage);
        Assertions.assertThat(responseDto.getLevel()).isEqualTo(1);
        Assertions.assertThat(responseDto.getCurrentExp()).isEqualTo(0);
        System.out.println(responseDto);
    }

    @Test
    @DisplayName("/{targetId}/rank")
    public void userRankDetail () throws Exception
    {
        //given
        Season season = testDataUtils.createSeason();
        User newUser = testDataUtils.createNewUser();
        String accessToken = tokenProvider.createToken(newUser.getId());
        testDataUtils.createUserRank(newUser, "statusMessage", season);

        //when
        String url = "/pingpong/users/" + newUser.getIntraId() + "/rank";
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        UserRankResponseDto responseDto = objectMapper.readValue(contentAsString, UserRankResponseDto.class);

        //then
        Assertions.assertThat(responseDto.getRank()).isEqualTo(1);
        Assertions.assertThat(responseDto.getWins()).isEqualTo(0);
        Assertions.assertThat(responseDto.getLosses()).isEqualTo(0);
        Assertions.assertThat(responseDto.getPpp()).isEqualTo(season.getStartPpp());
        System.out.println(responseDto);
    }

    @Test
    @DisplayName("/{userId}/historics")
    public void getUserHistory () throws Exception
    {
        //given
        Season season = testDataUtils.createSeason();
        User newUser = testDataUtils.createNewUser();
        String accessToken = tokenProvider.createToken(newUser.getId());

        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = startTime.plusMinutes(15);
        testDataUtils.createMockMatch(newUser, season, startTime, endTime);

        LocalDateTime startTime1 = LocalDateTime.now().minusDays(2);
        LocalDateTime endTime1 = startTime1.plusMinutes(15);
        testDataUtils.createMockMatch(newUser, season, startTime1, endTime1);

        LocalDateTime startTime2 = LocalDateTime.now().minusDays(3);
        LocalDateTime endTime2 = startTime2.plusMinutes(15);
        testDataUtils.createMockMatch(newUser, season, startTime2, endTime2);

        String url = "/pingpong/users/" + newUser.getId() + "/historics";

        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        UserHistoryResponseDto responseDto = objectMapper.readValue(contentAsString, UserHistoryResponseDto.class);


        //then
        List<UserHistoryData> historics = responseDto.getHistorics();
        Assertions.assertThat(responseDto.getHistorics().size()).isEqualTo(3);
        Assertions.assertThat(historics)
                .isSortedAccordingTo(Comparator.comparing(UserHistoryData::getDate));

    }

    @Test
    @DisplayName("/detail")
    public void  updateUser() throws Exception
    {
        //given
        Season season = testDataUtils.createSeason();
        String intraId = "intraId";
        String email = "email";
        String imageUrl = "imageUrl";
        User newUser = testDataUtils.createNewUser(intraId, email, imageUrl, RacketType.PENHOLDER,
                SnsType.BOTH, RoleType.ADMIN);
        String accessToken = tokenProvider.createToken(newUser.getId());
        String url = "/pingpong/users/detail";

        String newStatusMessage = "newStatusMessage";
        String newRacketType = "SHAKEHAND";
        String newSnsType = "SLACK";

        //when
        mockMvc.perform(put(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UserModifyRequestDto(newRacketType, newStatusMessage, newSnsType))))
                .andExpect(status().isOk());
        //then
        String hashKey = RedisKeyManager.getHashKey(season.getId());
        RankRedis rank = redisRepository.findRankByUserId(hashKey, newUser.getId());
        userRepository.findById(newUser.getId()).ifPresentOrElse(user -> {
            Assertions.assertThat(user.getRacketType()).isEqualTo(RacketType.valueOf(newRacketType));
            Assertions.assertThat(user.getSnsNotiOpt()).isEqualTo(SnsType.valueOf(newSnsType));
            Assertions.assertThat(rank.getStatusMessage()).isEqualTo(newStatusMessage);
        }, () -> {
            Assertions.fail("유저 업데이트 실패");
        });
    }
}