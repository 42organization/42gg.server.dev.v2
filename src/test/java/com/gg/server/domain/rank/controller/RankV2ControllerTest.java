package com.gg.server.domain.rank.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.dto.ExpRankPageResponseDto;
import com.gg.server.domain.rank.dto.RankPageResponseDto;
import com.gg.server.domain.rank.service.RankService;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.dto.PageRequestDto;
import com.gg.server.global.security.config.SecurityConfig;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.global.security.jwt.utils.TokenAuthenticationFilter;
import com.gg.server.global.security.service.CustomUserDetailsService;
import com.gg.server.global.utils.querytracker.ApiQueryCounter;
import com.gg.server.global.utils.querytracker.ApiQueryCounterAop;
import com.gg.server.global.utils.querytracker.LoggingInterceptor;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.core.parameters.P;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.*;

@WebMvcTest(value = RankV2Controller.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = TokenAuthenticationFilter.class)
    }
)
@ExtendWith(MockitoExtension.class)
class RankV2ControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RankService rankService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private RankRepository rankRepository;
    @MockBean
    private AuthTokenProvider authTokenProvider;
    @MockBean
    private ApiQueryCounter apiQueryCounter;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @WithMockUser(username = "TestUser")
    void getExpRankPage() throws Exception {
        given(userRepository.findById(0L))
                .willReturn(Optional.of(User.builder().intraId("TestUser").build()));
        given(rankService.getExpRankPage(PageRequest.of(1, 10),
                UserDto
                .builder()
                        .intraId("TestUser")
                        .id(1L)
                        .build())
        ).willReturn(new ExpRankPageResponseDto(
                        -1,
                        1,
                        1,
                        new ArrayList<>()
                ));
        mockMvc.perform(get("/pingpong/v2/exp")
                        .queryParam("size", "10")
                        .queryParam("page", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "TestUser")
    void getRankPage() throws Exception{
        given(userRepository.findById(0L))
                .willReturn(Optional.of(User.builder().intraId("TestUser").build()));
        given(rankService.getRankPageV2(PageRequest.of(1, 10),
                UserDto
                        .builder()
                        .intraId("TestUser")
                        .id(1L)
                        .build(), 1L)
        ).willReturn(new RankPageResponseDto(-1, 1, 1, new ArrayList<>()));
        mockMvc.perform(get("/pingpong/v2/ranks/single")
                        .queryParam("size", "10")
                        .queryParam("page", "1")
                        .queryParam("season", "1"))
                .andExpect(status().isOk());

    }
}