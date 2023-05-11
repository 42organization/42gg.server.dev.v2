package com.gg.server.admin.penalty.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.penalty.data.RedisPenaltyUser;
import com.gg.server.admin.penalty.data.RedisPenaltyUserRepository;
import com.gg.server.admin.penalty.dto.PenaltyRequestDto;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.UserModifyRequestDto;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;
import org.apache.http.HttpHeaders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
class PenaltyControllerTest {
    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RedisPenaltyUserRepository redisPenaltyUserRepository;
    @Autowired
    AuthTokenProvider tokenProvider;
    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    private final String headUrl= "/pingpong/admin/";
    @AfterEach
    void clear() {
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.flushDb();
        connection.close();
    }

    @Test
    @DisplayName("POST : penalty를 부여받지 않은 유효한 intraId에 penalty 부여")
    public void giveUserPenaltyforFirstTimeWithValidIntraId() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        tokenProvider.getUserIdFromToken(accessToken);
        User newUser = testDataUtils.createNewUser();
        String intraId = newUser.getIntraId();
        String url = headUrl + "users/" + intraId + "/penalty";
        mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PenaltyRequestDto(intraId, 3, "test1"))))
                .andExpect(status().isCreated());
        Optional<RedisPenaltyUser> penaltyUser = redisPenaltyUserRepository.findByIntraId(intraId);
        Assertions.assertThat(penaltyUser).isPresent();
        Assertions.assertThat(penaltyUser.get().getPenaltyTime()).isEqualTo(3);
        Assertions.assertThat(
                Duration.between(penaltyUser.get().getStartTime(),
                        penaltyUser.get().getReleaseTime()).getSeconds()).isEqualTo(3 * 60 * 60);
        Assertions.assertThat(penaltyUser.get().getReason());
    }

    @Test
    @DisplayName("POST : penalty를 부여받은 유효한 intraId에 penalty 부여")
    public void giveUserPenaltyRepeatablyWithValidIntraId() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        tokenProvider.getUserIdFromToken(accessToken);
        User newUser = testDataUtils.createNewUser();
        String intraId = newUser.getIntraId();
        String url = headUrl + "users/" + intraId + "/penalty";
        //패널티 두번 부여
        mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PenaltyRequestDto(intraId, 3, "test1"))))
                .andExpect(status().isCreated());
        mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PenaltyRequestDto(intraId, 2, "test2"))))
                .andExpect(status().isCreated());
        Optional<RedisPenaltyUser> penaltyUser = redisPenaltyUserRepository.findByIntraId(intraId);
        Assertions.assertThat(penaltyUser).isPresent();
        Assertions.assertThat(penaltyUser.get().getPenaltyTime()).isEqualTo(5);
        Assertions.assertThat(
                Duration.between(penaltyUser.get().getStartTime(),
                        penaltyUser.get().getReleaseTime()).getSeconds()).isEqualTo(5 * 60 * 60);
        Assertions.assertThat(penaltyUser.get().getReason());
    }


    @Test
    @DisplayName("POST 유효하지 않은 intraId에 penalty 부여")
    public void giveUserPenaltyWithInvalidIntraId() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        tokenProvider.getUserIdFromToken(accessToken);
        String intraId = "invalid!";
        String url = headUrl + "users/" + intraId + "/penalty";
        mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PenaltyRequestDto(intraId, 3, "test1"))))
                .andExpect(status().is4xxClientError());
    }

}
