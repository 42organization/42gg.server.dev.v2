package com.gg.server.admin.tournament.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.tournament.dto.TournamentAdminUpdateRequestDto;
import com.gg.server.admin.tournament.service.TournamentAdminService;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import java.time.LocalDateTime;
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

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
@Transactional
class TournamentAdminControllerTest {

    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;

    @Autowired
    TournamentAdminService tournamentAdminService;

    @Test
    @DisplayName("[Patch] /pingpong/admin/tournament/{tournamentId}")
    void 토너먼트_업데이트_성공() throws Exception {
        // given
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        tokenProvider.getUserIdFromAccessToken(accessToken);

        Tournament tournament = testDataUtils.createTournament(
            LocalDateTime.now().plusDays(2).plusHours(1),
            LocalDateTime.now().plusDays(2).plusHours(3),
            TournamentStatus.BEFORE);

        TournamentAdminUpdateRequestDto updateDto = testDataUtils.createUpdateRequestDto (
            LocalDateTime.now().plusDays(2).plusHours(2),
            LocalDateTime.now().plusDays(2).plusHours(4),
            TournamentType.MASTER);

        String url = "/pingpong/admin/tournament/" + tournament.getId();

        String content = objectMapper.writeValueAsString(updateDto);

        // when, then
        String contentAsString = mockMvc.perform(patch(url)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isNoContent())
            .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);
    }

    @Test
    void 토너먼트_없는_경우() throws Exception {
        // given
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        tokenProvider.getUserIdFromAccessToken(accessToken);

        TournamentAdminUpdateRequestDto updateDto = testDataUtils.createUpdateRequestDto(
            LocalDateTime.now().plusDays(2).plusHours(2),
            LocalDateTime.now().plusDays(2).plusHours(4),
            TournamentType.MASTER);

        String url = "/pingpong/admin/tournament/" + 1111;

        String content = objectMapper.writeValueAsString(updateDto);
        // when, then
        String contentAsString = mockMvc.perform(patch(url)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isNotFound())
            .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);
    }

    @Test
    void 토너먼트_업데이트_기간_겹치는_경우() throws Exception {
        // given
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        tokenProvider.getUserIdFromAccessToken(accessToken);

        Tournament tournamentAlreadyExist = testDataUtils.createTournament(
            LocalDateTime.now().plusDays(2).plusHours(2),
            LocalDateTime.now().plusDays(2).plusHours(4),
            TournamentStatus.BEFORE);

        Tournament tournamentToChange = testDataUtils.createTournament(
            LocalDateTime.now().plusDays(2).plusHours(5),
            LocalDateTime.now().plusDays(2).plusHours(7),
            TournamentStatus.BEFORE);

        // 겹치는 시간 조절
        TournamentAdminUpdateRequestDto updateDto = testDataUtils.createUpdateRequestDto(
            tournamentAlreadyExist.getStartTime().plusMinutes(1),
            tournamentAlreadyExist.getEndTime().plusMinutes(2),
            TournamentType.MASTER);

        String url = "/pingpong/admin/tournament/" + tournamentToChange.getId();

        String content = objectMapper.writeValueAsString(updateDto);

        // when, then
        String contentAsString = mockMvc.perform(patch(url)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isConflict())
            .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);
    }

    @Test
    void 이미_시작했거나_종료된_토너먼트_수정() throws Exception {
        // given
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        tokenProvider.getUserIdFromAccessToken(accessToken);

        Tournament liveTournament = testDataUtils.createTournament(
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now().plusHours(2),
            TournamentStatus.LIVE);

        Tournament endedTournament = testDataUtils.createTournament(
            LocalDateTime.now().minusHours(3),
            LocalDateTime.now().minusHours(1),
            TournamentStatus.END);

        TournamentAdminUpdateRequestDto updateTournamentDto = testDataUtils.createUpdateRequestDto(
            LocalDateTime.now().plusDays(2).plusHours(1),
            LocalDateTime.now().plusDays(2).plusHours(3),
            TournamentType.MASTER);

        String url = "/pingpong/admin/tournament/" + liveTournament.getId();

        String content = objectMapper.writeValueAsString(updateTournamentDto);

        // when live tournament test, then
        String contentAsString = mockMvc.perform(patch(url)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);

        url = "/pingpong/admin/tournament/" + endedTournament.getId();

        // when ended tournament test, then
        contentAsString = mockMvc.perform(patch(url)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);
    }

    @Test
    void 토너먼트_잘못된_기간() throws Exception {
        // given
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        tokenProvider.getUserIdFromAccessToken(accessToken);

        Tournament tournamentToChange = testDataUtils.createTournament(
            LocalDateTime.now().plusDays(2).plusHours(1),
            LocalDateTime.now().plusDays(2).plusHours(3),
            TournamentStatus.BEFORE);

        TournamentAdminUpdateRequestDto updateDto1 = testDataUtils.createUpdateRequestDto(
            tournamentToChange.getStartTime(),
            tournamentToChange.getStartTime(),
            TournamentType.MASTER);

        TournamentAdminUpdateRequestDto updateDto2 = testDataUtils.createUpdateRequestDto(
            tournamentToChange.getEndTime(),
            tournamentToChange.getStartTime(),
            TournamentType.MASTER);

        String url = "/pingpong/admin/tournament/" + tournamentToChange.getId();
        // when startTime == endTime, then
        String content = objectMapper.writeValueAsString(updateDto1);

        String contentAsString = mockMvc.perform(patch(url)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);

        // when startTime > endTime test, then
        content = objectMapper.writeValueAsString(updateDto2);

        contentAsString = mockMvc.perform(patch(url)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);
    }

    @Test
    void 잘못된_dto() throws Exception {
        // given
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        tokenProvider.getUserIdFromAccessToken(accessToken);

        Tournament tournamentToChange = testDataUtils.createTournament(
            LocalDateTime.now().plusDays(2).plusHours(1),
            LocalDateTime.now().plusDays(2).plusHours(3),
            TournamentStatus.BEFORE);

        TournamentAdminUpdateRequestDto updateDto1 = testDataUtils.createUpdateRequestDto(
            tournamentToChange.getStartTime(),
            tournamentToChange.getStartTime(),
            null);

        String url = "/pingpong/admin/tournament/" + tournamentToChange.getId();
        // when startTime == endTime, then
        String content = objectMapper.writeValueAsString(updateDto1);

        String contentAsString = mockMvc.perform(patch(url)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        System.out.println(contentAsString);
    }



}