package com.gg.server.admin.tournament.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.tournament.dto.TournamentAdminCreateRequestDto;
import com.gg.server.admin.tournament.dto.TournamentAdminAddUserRequestDto;
import com.gg.server.admin.tournament.dto.TournamentAdminUpdateRequestDto;
import com.gg.server.admin.tournament.service.TournamentAdminService;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentUser;
import com.gg.server.domain.tournament.data.TournamentUserRepository;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.domain.user.data.User;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.CustomRuntimeException;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    TournamentUserRepository tournamentUserRepository;

    @Nested
    @DisplayName("토너먼트_관리_수정_컨트롤러_테스트")
    class TournamentAdminControllerUpdateTest {
        @Test
        @DisplayName("토너먼트_업데이트_성공")
        void success() throws Exception {
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

            String url = "/pingpong/admin/tournaments/" + tournament.getId();

            String content = objectMapper.writeValueAsString(updateDto);

            // when
            String contentAsString = mockMvc.perform(patch(url)
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString();

            System.out.println(contentAsString);

            // then
            Tournament result = tournamentRepository.findById(tournament.getId()).get();
            assertThat(result.getTitle()).isEqualTo(tournament.getTitle());
            assertThat(result.getContents()).isEqualTo(tournament.getContents());
            assertThat(result.getStartTime()).isEqualTo(updateDto.getStartTime());
            assertThat(result.getEndTime()).isEqualTo(updateDto.getEndTime());
            assertThat(result.getType()).isEqualTo(updateDto.getType());
            assertThat(result.getStatus()).isEqualTo(tournament.getStatus());
        }

        @Test
        @DisplayName("토너먼트_없는_경우")
        void tournamentNotFound() throws Exception {
            // given
            String accessToken = testDataUtils.getAdminLoginAccessToken();
            tokenProvider.getUserIdFromAccessToken(accessToken);

            TournamentAdminUpdateRequestDto updateDto = testDataUtils.createUpdateRequestDto(
                LocalDateTime.now().plusDays(2).plusHours(2),
                LocalDateTime.now().plusDays(2).plusHours(4),
                TournamentType.MASTER);

            String url = "/pingpong/admin/tournaments/" + 1111;

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
        @DisplayName("토너먼트_업데이트_기간_겹치는_경우")
        void tournamentConflicted() throws Exception {
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

            String url = "/pingpong/admin/tournaments/" + tournamentToChange.getId();

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
        @DisplayName("이미_시작했거나_종료된_토너먼트_수정")
        void canNotUpdate() throws Exception {
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

            String url = "/pingpong/admin/tournaments/" + liveTournament.getId();

            String content = objectMapper.writeValueAsString(updateTournamentDto);

            // when live tournament test, then
            String contentAsString = mockMvc.perform(patch(url)
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

            System.out.println(contentAsString);

            url = "/pingpong/admin/tournaments/" + endedTournament.getId();

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
        @DisplayName("토너먼트_잘못된_기간")
        void wrongTournamentTime() throws Exception {
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

            String url = "/pingpong/admin/tournaments/" + tournamentToChange.getId();
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
        @DisplayName("잘못된_dto")
        void wrongDto() throws Exception {
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

            String url = "/pingpong/admin/tournaments/" + tournamentToChange.getId();
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

    @Nested
    @DisplayName("토너먼트_관리_삭제_컨트롤러_테스트")
    class TournamentAdminControllerDeleteTest {
        @Test
        @DisplayName("토너먼트_삭제_성공")
        void success() throws Exception {
            // given
            String accessToken = testDataUtils.getAdminLoginAccessToken();
            tokenProvider.getUserIdFromAccessToken(accessToken);

            Tournament tournament = testDataUtils.createTournament(
                LocalDateTime.now().plusDays(2).plusHours(1),
                LocalDateTime.now().plusDays(2).plusHours(3),
                TournamentStatus.BEFORE);

            List<TournamentGame> tournamentGameList = testDataUtils.createTournamentGameList(tournament, 7);

            String url = "/pingpong/admin/tournaments/" + tournament.getId();

            // when
            String contentAsString = mockMvc.perform(delete(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString();

            System.out.println(contentAsString);

            // then
            tournamentRepository.findById(tournament.getId()).ifPresent(
                a-> {throw new CustomRuntimeException("삭제되지 않았습니다.", ErrorCode.BAD_REQUEST);});
        }

        @Test
        @DisplayName("토너먼트_없는_경우")
        void tournamentNotFound() throws Exception {
            // given
            String accessToken = testDataUtils.getAdminLoginAccessToken();
            tokenProvider.getUserIdFromAccessToken(accessToken);

            String url = "/pingpong/admin/tournaments/" + 1111;

            // when, then
            String contentAsString = mockMvc.perform(delete(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

            System.out.println(contentAsString);
        }

        @Test
        @DisplayName("이미_시작했거나_종료된_토너먼트_수정")
        void canNotDelete() throws Exception {
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

            String url = "/pingpong/admin/tournaments/" + liveTournament.getId();

            // when live tournament test, then
            String contentAsString = mockMvc.perform(delete(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

            System.out.println(contentAsString);

            url = "/pingpong/admin/tournaments/" + endedTournament.getId();

            // when ended tournament test, then
            contentAsString = mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

            System.out.println(contentAsString);
        }
    }

    @Nested
    @DisplayName("토너먼트 관리 생성 컨트롤러 테스트")
    class TournamentAdminControllerCreateTest {
        @Test
        @DisplayName("토너먼트 생성 성공")
        void success() throws Exception {
            //given
            String accessToken = testDataUtils.getAdminLoginAccessToken();

            TournamentAdminCreateRequestDto createDto = testDataUtils.createRequestDto(
                    LocalDateTime.now().plusDays(10).plusHours(3),
                    LocalDateTime.now().plusDays(10).plusHours(5),
                    TournamentType.ROOKIE);

            String url = "/pingpong/admin/tournaments";
            String content = objectMapper.writeValueAsString(createDto);

            //when
            String contentAsString = mockMvc.perform(post(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            System.out.println(contentAsString);

            // then
            tournamentRepository.findByTitle(createDto.getTitle()).orElseThrow(()->
                    new CustomRuntimeException("토너먼트 생성 안 됨", ErrorCode.BAD_REQUEST));
        }

        @Test
        @DisplayName("토너먼트 제목 중복")
        void titleDup() throws Exception {
            //given
            String accessToken = testDataUtils.getAdminLoginAccessToken();

            TournamentAdminCreateRequestDto createDto = testDataUtils.createRequestDto(
                    LocalDateTime.now().plusDays(10).plusHours(3),
                    LocalDateTime.now().plusDays(10).plusHours(5),
                    TournamentType.ROOKIE);

            testDataUtils.createTournament(createDto.getTitle(), LocalDateTime.now(),
                    LocalDateTime.now().plusHours(2), TournamentStatus.BEFORE);

            String url = "/pingpong/admin/tournaments";
            String content = objectMapper.writeValueAsString(createDto);

            //when, then
            String contentAsString = mockMvc.perform(post(url)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isConflict())
                    .andReturn().getResponse().getContentAsString();

            System.out.println(contentAsString);
        }
    }

    @Nested
    @DisplayName("관리자_토너먼트_유저_추가_컨트롤러_테스트")
    class TournamentAdminControllerAddUserTest {
        @Test
        @DisplayName("유저_추가_성공")
        void success() throws Exception {
            // given
            String accessToken = testDataUtils.getAdminLoginAccessToken();
            tokenProvider.getUserIdFromAccessToken(accessToken);

            Tournament tournament1 = testDataUtils.createTournament(
                LocalDateTime.now().plusDays(2).plusHours(1),
                LocalDateTime.now().plusDays(2).plusHours(3),
                TournamentStatus.BEFORE);
            Tournament tournament2 = testDataUtils.createTournament(
                LocalDateTime.now().plusDays(3).plusHours(1),
                LocalDateTime.now().plusDays(3).plusHours(3),
                TournamentStatus.BEFORE);
            User user = testDataUtils.createNewUser("testUser");
            testDataUtils.createTournamentUser(user, tournament2, true);

            TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto(user.getIntraId());

            String url = "/pingpong/admin/tournaments/" + tournament1.getId() + "/users";
            String content = objectMapper.writeValueAsString(requestDto);

            // when
            String contentAsString = mockMvc.perform(post(url)
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

            // then
            System.out.println(contentAsString);
            tournament1.getTournamentUsers().stream().filter(tu->tu.getUser().equals(user)).findAny()
                .orElseThrow(()->new CustomRuntimeException("토너먼트 유저 리스트에 추가 안됨", ErrorCode.BAD_REQUEST));
            tournamentUserRepository.findAllByTournamentId(tournament1.getId())
                .stream().filter(tu-> tu.getUser().getIntraId().equals(requestDto.getIntraId()))
                .findAny().orElseThrow(()->new CustomRuntimeException("토너먼트 유저 테이블에 추가 안됨", ErrorCode.BAD_REQUEST));
        }

        @Test
        @DisplayName("토너먼트_없는_경우")
        void tournamentNotFound() throws Exception {
            // given
            String accessToken = testDataUtils.getAdminLoginAccessToken();
            tokenProvider.getUserIdFromAccessToken(accessToken);

            User user = testDataUtils.createNewUser("test");

            TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto(user.getIntraId());

            String url = "/pingpong/admin/tournaments/" + 9999 + "/users";

            String content = objectMapper.writeValueAsString(requestDto);

            // when, then
            String contentAsString = mockMvc.perform(post(url)
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        }

        @Test
        @DisplayName("이미_시작했거나_종료된_토너먼트_수정")
        void canNotUpdate() throws Exception {
            // given
            String accessToken = testDataUtils.getAdminLoginAccessToken();
            tokenProvider.getUserIdFromAccessToken(accessToken);

            Tournament tournament = testDataUtils.createTournament(
                LocalDateTime.now().plusDays(0).plusHours(-1),
                LocalDateTime.now().plusDays(0).plusHours(1),
                TournamentStatus.LIVE);
            User user = testDataUtils.createNewUser("test");

            TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto(user.getIntraId());

            String url = "/pingpong/admin/tournaments/" + tournament.getId() + "/users";

            String content = objectMapper.writeValueAsString(requestDto);

            // when, then
            String contentAsString = mockMvc.perform(post(url)
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        }

        @Test
        @DisplayName("찾을_수_없는_유저")
        void userNotFound() throws Exception {
            // given
            String accessToken = testDataUtils.getAdminLoginAccessToken();
            tokenProvider.getUserIdFromAccessToken(accessToken);

            Tournament tournament = testDataUtils.createTournament(
                LocalDateTime.now().plusDays(2).plusHours(1),
                LocalDateTime.now().plusDays(2).plusHours(3),
                TournamentStatus.BEFORE);

            TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto("nobody");

            String url = "/pingpong/admin/tournaments/" + tournament.getId() + "/users";

            String content = objectMapper.writeValueAsString(requestDto);

            // when, then
            String contentAsString = mockMvc.perform(post(url)
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        }

        @Test
        @DisplayName("해당_토너먼트_참가자인_경우")
        void alreadyTournamentParticipant() throws Exception {
            // given
            String accessToken = testDataUtils.getAdminLoginAccessToken();
            tokenProvider.getUserIdFromAccessToken(accessToken);

            Tournament tournament = testDataUtils.createTournament(
                LocalDateTime.now().plusDays(2).plusHours(1),
                LocalDateTime.now().plusDays(2).plusHours(3),
                TournamentStatus.BEFORE);

            User user = testDataUtils.createNewUser("test");
            TournamentUser participant = testDataUtils.createTournamentUser(user, tournament, false);
            TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto(user.getIntraId());

            String url = "/pingpong/admin/tournaments/" + tournament.getId() + "/users";
            String content = objectMapper.writeValueAsString(requestDto);

            // when, then
            String contentAsString = mockMvc.perform(post(url)
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isConflict())
                .andReturn().getResponse().getContentAsString();
        }

        @Test
        @DisplayName("토너먼트_대기자_신청")
        void waitUserTest() throws Exception {
            // given
            String accessToken = testDataUtils.getAdminLoginAccessToken();
            tokenProvider.getUserIdFromAccessToken(accessToken);

            Tournament tournament = testDataUtils.createTournament(
                LocalDateTime.now().plusDays(2).plusHours(1),
                LocalDateTime.now().plusDays(2).plusHours(3),
                TournamentStatus.BEFORE);

            User user = testDataUtils.createNewUser("testUser0");
            for (int i=1; i<=8; i++) {
                testDataUtils.createTournamentUser(testDataUtils.createNewUser("testUser" + i), tournament, true);
            }
            TournamentAdminAddUserRequestDto requestDto = new TournamentAdminAddUserRequestDto(user.getIntraId());

            String url = "/pingpong/admin/tournaments/" + tournament.getId() + "/users";
            String content = objectMapper.writeValueAsString(requestDto);

            // when
            String contentAsString = mockMvc.perform(post(url)
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

            // then
            tournamentUserRepository.findAllByTournamentId(tournament.getId()).stream()
                .filter(tu->tu.getUser().getIntraId().equals(user.getIntraId())).findAny()
                .filter(tu->!tu.isJoined()).orElseThrow(()->new CustomRuntimeException("waitlist 제대로 등록 안됨", ErrorCode.BAD_REQUEST));
        }
    }
}