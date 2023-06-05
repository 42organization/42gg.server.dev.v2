package com.gg.server.admin.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.admin.user.dto.UserDetailAdminResponseDto;
import com.gg.server.admin.user.dto.UserSearchAdminDto;
import com.gg.server.admin.user.dto.UserSearchAdminResponseDto;
import com.gg.server.admin.user.service.UserAdminService;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
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
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import java.util.List;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
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

    @Test
    @DisplayName("유저 필터링 조회 테스트")
    @Transactional
    public void 유저필터링조회테스트() throws Exception {
        //given
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
        User user = userRepository.findById(userId).get();
        int page = 1;
        int size = 20;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("intraId").ascending());
        String url = "/pingpong/admin/users?page=1&userFilter=\"" + user.getIntraId() + "\"";

        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserSearchAdminResponseDto actureResponse = objectMapper.readValue(contentAsString, UserSearchAdminResponseDto.class);

        List<UserSearchAdminDto> actureUserList = actureResponse.getUserSearchAdminDtos();
        for (UserSearchAdminDto userDto : actureUserList)
            Assertions.assertThat(userDto.getIntraId()).isEqualTo(user.getIntraId());
    }

    @Test
    @DisplayName("GET /pingpong/admin/users/{intraId}")
    @Transactional
    public void userGetDetailTest() throws Exception{
        //given
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
        User user = userRepository.findByIntraId("nheo").get();
        String url = "/pingpong/admin/users/" + user.getIntraId();
        UserDetailAdminResponseDto expectedResponse = userAdminService.getUserDetailByIntraId(user.getIntraId());

        //when
        //200 성공
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserDetailAdminResponseDto actureResponse = objectMapper.readValue(contentAsString, UserDetailAdminResponseDto.class);

        //then
        Assertions.assertThat(actureResponse.getUserId()).isEqualTo(expectedResponse.getUserId());
        Assertions.assertThat(actureResponse.getIntraId()).isEqualTo(expectedResponse.getIntraId());
        Assertions.assertThat(actureResponse.getUserImageUri()).isEqualTo(expectedResponse.getUserImageUri());
        Assertions.assertThat(actureResponse.getRacketType()).isEqualTo(expectedResponse.getRacketType());
//        Assertions.assertThat(actureResponse.getStatusMessage()).isEqualTo(expecxtedResponse.getStatusMessage());
        Assertions.assertThat(actureResponse.getWins()).isEqualTo(expectedResponse.getWins());
        Assertions.assertThat(actureResponse.getLosses()).isEqualTo(expectedResponse.getLosses());
        Assertions.assertThat(actureResponse.getPpp()).isEqualTo(expectedResponse.getPpp());
        Assertions.assertThat(actureResponse.getEmail()).isEqualTo(expectedResponse.getEmail());
        Assertions.assertThat(actureResponse.getRoleType()).isEqualTo(expectedResponse.getRoleType());
        Assertions.assertThat(actureResponse.getExp()).isEqualTo(expectedResponse.getExp());
    }
}