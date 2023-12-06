package com.gg.server.domain.tournament.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentUserRepository;
import com.gg.server.domain.tournament.dto.TournamentListResponseDto;
import com.gg.server.domain.tournament.dto.TournamentResponseDto;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.CustomRuntimeException;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TournamentFindControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    AuthTokenProvider tokenProvider;
    @Autowired
    TournamentUserRepository tournamentUserRepository;

    List<TournamentResponseDto> tournamentList;
    String accessToken;

    User tester;



    @Nested
    @DisplayName("토너먼트_리스트_조회")
    class findTournamentListTest {
        @BeforeEach
        void beforeEach() {
            tester = testDataUtils.createNewUser("findControllerTester", "findControllerTester", RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
            accessToken = tokenProvider.createToken(tester.getId());
            tournamentList = testDataUtils.makeTournamentList();
        }
        @Test
        @DisplayName("전체_조회")
        public void getTournamentList() throws Exception {
            // given
            int page = 2;
            int size = 20;
            String url = "/pingpong/tournaments/?page=" + page + "&size=" + size;

            // when
            String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            TournamentListResponseDto resp = objectMapper.readValue(contentAsString, TournamentListResponseDto.class);

            // then
            List<TournamentResponseDto> tournamentInfoList = resp.getTournaments();
            for (int i = 0; i < tournamentInfoList.size(); i++) {
                Long tournamentId = tournamentInfoList.get(i).getTournamentId();
                TournamentResponseDto tournamentResponseDto = tournamentList.stream().filter(t -> t.getTournamentId().equals(tournamentId)).findFirst().orElse(null);
                if (tournamentResponseDto != null) {
                    assertThat(tournamentInfoList.get(i).getTitle()).isEqualTo(tournamentResponseDto.getTitle());
                    assertThat(tournamentInfoList.get(i).getContents()).isEqualTo(tournamentResponseDto.getContents());
                    assertThat(tournamentInfoList.get(i).getType()).isEqualTo(tournamentResponseDto.getType());
                    assertThat(tournamentInfoList.get(i).getStatus()).isEqualTo(tournamentResponseDto.getStatus());
                    assertThat(tournamentInfoList.get(i).getWinnerIntraId()).isEqualTo(tournamentResponseDto.getWinnerIntraId());
                    assertThat(tournamentInfoList.get(i).getWinnerImageUrl()).isEqualTo(tournamentResponseDto.getWinnerImageUrl());
                    assertThat(tournamentInfoList.get(i).getPlayer_cnt()).isEqualTo(tournamentResponseDto.getPlayer_cnt());
                }
            }
        }

        @Test
        @DisplayName("status별_조회")
        public void getTournamentListByStatus() throws Exception {

            // given
            int page = 1;
            int size = 10;
            String url = "/pingpong/tournaments/?page=" + page + "&size=" + size + "&status=" + TournamentStatus.BEFORE.getCode();

            // when
            String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            TournamentListResponseDto resp = objectMapper.readValue(contentAsString, TournamentListResponseDto.class);

            // then
            List<TournamentResponseDto> tournamentInfoList = resp.getTournaments();
            for (TournamentResponseDto responseDto : tournamentInfoList) {
                assertThat(responseDto.getStatus()).isEqualTo(TournamentStatus.BEFORE);
            }
        }

        @Test
        @DisplayName("type별_조회")
        public void getTournamentListByType() throws Exception {

            // given
            int page = 1;
            int size = 10;
            String url = "/pingpong/tournaments/?page=" + page + "&size=" + size + "&type=" + TournamentType.ROOKIE.getCode();

            // when
            String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            TournamentListResponseDto resp = objectMapper.readValue(contentAsString, TournamentListResponseDto.class);

            // then
            List<TournamentResponseDto> tournamentInfoList = resp.getTournaments();
            for (TournamentResponseDto responseDto : tournamentInfoList) {
                assertThat(responseDto.getType()).isEqualTo(TournamentType.ROOKIE);
            }
        }

        @Test
        @DisplayName("type과 status 별 조회")
        public void getTournamentListByTypeAndStatus() throws Exception {
            // given
            int page = 1;
            int size = 10;
            String url = "/pingpong/tournaments/?page=" + page + "&size=" + size + "&type=" + TournamentType.ROOKIE.getCode() + "&status=" + TournamentStatus.BEFORE.getCode();

            // when
            String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            TournamentListResponseDto resp = objectMapper.readValue(contentAsString, TournamentListResponseDto.class);

            // then
            List<TournamentResponseDto> tournamentInfoList = resp.getTournaments();
            for (TournamentResponseDto responseDto : tournamentInfoList) {
                assertThat(responseDto.getType()).isEqualTo(TournamentType.ROOKIE);
                assertThat(responseDto.getStatus()).isEqualTo(TournamentStatus.BEFORE);
            }
        }

        @Test
        @DisplayName("잘못된 type")
        public void wrongType() throws Exception {
            // given
            int page = 1;
            int size = 10;
            String url = "/pingpong/tournaments/?page=" + page + "&size=" + size + "&type=" + "rookie123" + "&status=" + TournamentStatus.BEFORE.getCode();

            // when
            String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            // then
            log.info(contentAsString);
        }

        @Test
        @DisplayName("잘못된 status")
        public void wrongStatus() throws Exception {
            // given
            int page = 1;
            int size = 10;
            String url = "/pingpong/tournaments/?page=" + page + "&size=" + size + "&type=" + TournamentType.ROOKIE.getCode() + "&status=" + "wrongStatus";

            // when
            String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            // then
            log.info(contentAsString);
        }
    }

    @Nested
    @DisplayName("토너먼트_단일_조회")
    class findTournamentTest {
        @BeforeEach
        void beforeEach() {
            tester = testDataUtils.createNewUser("findControllerTester", "findControllerTester", RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
            accessToken = tokenProvider.createToken(tester.getId());
        }
        @Test
        @DisplayName("조회_성공")
        public void success() throws Exception {
            //given
            Tournament tournament = testDataUtils.createTournament("string1", "string",
                    LocalDateTime.now().plusDays(2).plusHours(1), LocalDateTime.now().plusDays(2).plusHours(3),
                    TournamentType.ROOKIE, TournamentStatus.BEFORE);
            User user = testDataUtils.createNewUser("test");
            testDataUtils.createTournamentUser(user, tournament, true);
            tournament.update_winner(user);

            Long tournamentId = tournament.getId();
            String url = "/pingpong/tournaments/" + tournamentId;

            //when
            String contentAsString = mockMvc.perform(get(url)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            TournamentResponseDto responseDto = objectMapper.readValue(contentAsString, TournamentResponseDto.class);

            //then
            assertThat(tournament.getTitle()).isEqualTo(responseDto.getTitle());
            assertThat(tournament.getContents()).isEqualTo(responseDto.getContents());
            assertThat(tournament.getType()).isEqualTo(responseDto.getType());
            assertThat(tournament.getStatus()).isEqualTo(responseDto.getStatus());
            if (tournament.getWinner() == null) {
                assertThat(responseDto.getWinnerIntraId()).isEqualTo(null);
                assertThat(responseDto.getWinnerImageUrl()).isEqualTo(null);
            }
            else {
                assertThat(tournament.getWinner().getIntraId()).isEqualTo(responseDto.getWinnerIntraId());
                assertThat(tournament.getWinner().getImageUri()).isEqualTo(responseDto.getWinnerImageUrl());
            }
            assertThat(tournament.getTournamentUsers().size()).isEqualTo(responseDto.getPlayer_cnt());
        }

        @Test
        @DisplayName("잘못된_토너먼트_ID")
        public void tournamentNotExist() throws Exception {
            //given
            Long tournamentId = 1L;
            String url = "/pingpong/tournaments/" + tournamentId;

            //when
            String contentAsString = mockMvc.perform(get(url)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            //then
            System.out.println(contentAsString);
        }
    }

    @Nested
    @DisplayName("토너먼트_유저_참가_상태_조회")
    class UserStatusInTournamentTest {
        @BeforeEach
        void beforeEach() {
            tester = testDataUtils.createNewUser("findControllerTester", "findControllerTester", RacketType.DUAL, SnsType.SLACK, RoleType.ADMIN);
            accessToken = tokenProvider.createToken(tester.getId());
        }
        @Test
        @DisplayName("유저_상태_조회_성공")
        void success() throws Exception {
            // given 1
            Tournament tournament = testDataUtils.createTournament(LocalDateTime.now(), LocalDateTime.now(), TournamentStatus.BEFORE);
            String url = "/pingpong/tournaments/" + tournament.getId() + "/users";
            String expected1 = "{\"status\":\"BEFORE\"}";

            // when 1
            String contentAsString = mockMvc.perform(get(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            // then 1
            if (expected1.compareTo(contentAsString) != 0) {
                throw new CustomRuntimeException("상태 오류", ErrorCode.BAD_REQUEST);
            }

            // given 2
            testDataUtils.createTournamentUser(tester, tournament, false);
            String expected2 = "{\"status\":\"WAIT\"}";

            // when 2
            contentAsString = mockMvc.perform(get(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            // then 2
            if (expected2.compareTo(contentAsString) != 0) {
                throw new CustomRuntimeException("상태 오류", ErrorCode.BAD_REQUEST);
            }

            // given 3
            tournamentUserRepository.findByTournamentIdAndUserId(tournament.getId(), tester.getId())
                .get().updateIsJoined(true);
            String expected3 = "{\"status\":\"PLAYER\"}";

            // when 3
            contentAsString = mockMvc.perform(get(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            // then 3
            if (expected3.compareTo(contentAsString) != 0) {
                throw new CustomRuntimeException("상태 오류", ErrorCode.BAD_REQUEST);
            }
        }

        @Test
        @DisplayName("토너먼트_없음")
        void tournamentNotFound() throws Exception {
            // given
            String url = "/pingpong/tournaments/" + 9999 + "/users";

            // when, then
            String contentAsString = mockMvc.perform(get(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

            System.out.println(contentAsString);
        }

    }
}
