package com.gg.server.admin.announcement.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.announcement.data.AnnouncementAdminRepository;
import com.gg.server.admin.announcement.dto.AnnouncementAdminAddDto;
import com.gg.server.admin.announcement.dto.AnnouncementAdminListResponseDto;
import com.gg.server.admin.announcement.dto.AnnouncementAdminUpdateDto;
import com.gg.server.admin.season.dto.SeasonCreateRequestDto;
import com.gg.server.admin.slot.dto.SlotAdminDto;
import com.gg.server.domain.announcement.Announcement;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AnnouncementAdminControllerTest {
    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;

    @Autowired
    AnnouncementAdminRepository announcementAdminRepository;

    @Test
    @DisplayName("[Get]/pingpong/admin/announcement")
    void getAnnouncementList() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);

        Integer currentPage = 2;
        Integer pageSize = 5;//페이지 사이즈 크기가 실제 디비 정보보다 큰지 확인할 것

        String url = "/pingpong/admin/announcement?page=" + currentPage + "&size=" + pageSize;

        String contentAsString = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //ResponseEntity<AnnouncementAdminListResponseDto> announceListDtoResponse = objectMapper
        //        .readValue(contentAsString, new TypeReference<ResponseEntity<AnnouncementAdminListResponseDto>>() {});
        //AnnouncementAdminListResponseDto announceListDto = announceListDtoResponse.getBody();
        AnnouncementAdminListResponseDto announceListDto = objectMapper.readValue(contentAsString, AnnouncementAdminListResponseDto.class);

    }

    @Test
    @DisplayName("[Post]/pingpong/admin/announcement")
    void addAnnouncement() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);

        //공지사항 1개 정책 때문에 기존 공지사항 지울 것
        Announcement delDto = announcementAdminRepository.findFirstByOrderByIdDesc();
        announcementAdminRepository.delete(delDto);

        AnnouncementAdminAddDto addDto = AnnouncementAdminAddDto.builder()
                .content("하나하나둘둘")
                .creatorIntraId("testId")
                .build();

        String content = objectMapper.writeValueAsString(addDto);
        String url = "/pingpong/admin/announcement";

        String contentAsString = mockMvc.perform(post(url)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Announcement result = announcementAdminRepository.findFirstByOrderByIdDesc();

        assertThat(result.getContent()).isEqualTo(addDto.getContent());
        assertThat(result.getCreatorIntraId()).isEqualTo(addDto.getCreatorIntraId());
        assertThat(result.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("fail[Post]/pingpong/admin/announcement")
    void addAnnouncementFail() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);

        AnnouncementAdminAddDto addDto = AnnouncementAdminAddDto.builder()
                .content("하나하나둘둘")
                .creatorIntraId(null)
                .build();

        String content = objectMapper.writeValueAsString(addDto);
        String url = "/pingpong/admin/announcement";

        String contentAsString = mockMvc.perform(post(url)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    @DisplayName("[Put]/pingpong/admin/announcement")
    void putAnnouncement() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);

        //공지사항 1개 정책 때문에 기존 공지사항 지울 것
//        Announcement delDto = announcementAdminRepository.findFirstByOrderByIdDesc();
//        announcementAdminRepository.delete(delDto);
        //공지사항 없으면 만들어 주는 과정 넣어 줄것

        AnnouncementAdminUpdateDto updateDto = AnnouncementAdminUpdateDto.builder()
                .deleterIntraId("deleterTestId")
                .build();

        String content = objectMapper.writeValueAsString(updateDto);
        String url = "/pingpong/admin/announcement";

        String contentAsString = mockMvc.perform(put(url)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString();

        Announcement result = announcementAdminRepository.findFirstByOrderByIdDesc();

        assertThat(result.getDeleterIntraId()).isEqualTo(updateDto.getDeleterIntraId());
        assertThat(result.getDeletedAt()).isNotNull();
        System.out.println(result.getId());
        System.out.println(result.getDeleterIntraId());
        System.out.println(result.getDeletedAt());
    }

}