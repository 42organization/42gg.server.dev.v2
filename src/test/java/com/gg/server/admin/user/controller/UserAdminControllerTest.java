package com.gg.server.admin.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.noti.dto.SendNotiAdminRequestDto;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.admin.user.dto.UserSearchAdminDto;
import com.gg.server.admin.user.dto.UserSearchAdminResponseDto;
import com.gg.server.admin.user.service.UserAdminService;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.dto.NotiResponseDto;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.apache.http.HttpHeaders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
class UserAdminControllerTest {

    @Autowired
    UserAdminService userAdminService;
    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    AuthTokenProvider tokenProvider;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserAdminRepository userAdminRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /pingpong/admin/users")
    @Transactional
    public void userSearchAllTest() throws Exception{
        //given
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromToken(accessToken);
        User user = userRepository.findById(userId).get();
        int page = 1;
        int size = 20;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("intraId").ascending());
        String url = "/pingpong/admin/users?page=1";
        String url2 = "/pingpong/admin/users?page=1&intraId=" + user.getIntraId();
        //when
        //200 성공
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserSearchAdminResponseDto actureResponse1 = objectMapper.readValue(contentAsString, UserSearchAdminResponseDto.class);
        //200 성공
        contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserSearchAdminResponseDto actureResponse2 = objectMapper.readValue(contentAsString, UserSearchAdminResponseDto.class);

        //then
        List<User> userList1 = userAdminRepository.findAll(pageable).getContent();
        List<UserSearchAdminDto> actureUserList1 = actureResponse1.getUserSearchAdminDtos();
        for (int i = 0; i < userList1.size(); i++) {
            Assertions.assertThat(userList1.get(i).getIntraId()).isEqualTo(actureUserList1.get(i).getIntraId());
        }

        List<User> userList2 = userAdminRepository.findAll(pageable).getContent();
        List<UserSearchAdminDto> actureUserList2 = actureResponse2.getUserSearchAdminDtos();
        for (int i = 0; i < userList1.size(); i++) {
            Assertions.assertThat(userList2.get(i).getIntraId()).isEqualTo(actureUserList2.get(i).getIntraId());
        }
    }
}