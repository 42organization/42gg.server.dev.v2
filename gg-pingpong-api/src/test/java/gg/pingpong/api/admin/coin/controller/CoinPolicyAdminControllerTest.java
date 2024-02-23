package gg.pingpong.api.admin.coin.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.stream.IntStream;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.pingpong.admin.repo.coin.CoinPolicyAdminRepository;
import gg.pingpong.api.admin.store.controller.response.CoinPolicyAdminListResponseDto;
import gg.pingpong.api.admin.store.dto.CoinPolicyAdminAddDto;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.data.manage.CoinPolicy;
import gg.pingpong.data.user.User;
import gg.pingpong.utils.TestDataUtils;
import gg.pingpong.utils.annotation.IntegrationTest;
import gg.pingpong.utils.exception.coin.CoinPolicyNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class CoinPolicyAdminControllerTest {
	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	AuthTokenProvider tokenProvider;

	@Autowired
	CoinPolicyAdminRepository coinPolicyAdminRepository;

	@Test
	@DisplayName("[Get]/pingpong/admin/coinpolicy")
	void getCoinPolicy() throws Exception {
		User admin = testDataUtils.createAdminUser();
		IntStream.range(0, 3).forEach(i -> testDataUtils
			.createCoinPolicy(admin, 1, 2, 3, 4));
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

		Integer currentPage = 1;
		Integer pageSize = 5; //페이지 사이즈 크기가 실제 디비 정보보다 큰지 확인할 것

		String url = "/pingpong/admin/coinpolicy?page=" + currentPage + "&size=" + pageSize;
		String contentAsString = mockMvc.perform(get(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		CoinPolicyAdminListResponseDto result = objectMapper.readValue(contentAsString,
			CoinPolicyAdminListResponseDto.class);
		assertThat(result.getCoinPolicyList().size()).isEqualTo(3);
		System.out.println(result.getCoinPolicyList().get(0).getCoinPolicyId());
		System.out.println(result.getCoinPolicyList().get(0).getCreateUserId());
		System.out.println(result.getCoinPolicyList().get(0).getAttendance());
		System.out.println(result.getCoinPolicyList().get(0).getNormal());
		System.out.println(result.getCoinPolicyList().get(0).getRankWin());
		System.out.println(result.getCoinPolicyList().get(0).getRankLose());

	}

	@Test
	@DisplayName("[Post]/pingpong/admin/coinpolicy")
	void addCoinPolicy() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

		CoinPolicyAdminAddDto addDto = new CoinPolicyAdminAddDto(1, 2, 5, 0);

		String content = objectMapper.writeValueAsString(addDto);
		String url = "/pingpong/admin/coinpolicy";

		String contentAsString = mockMvc.perform(post(url)
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isCreated())
			.andReturn().getResponse().getContentAsString();

		CoinPolicy result = coinPolicyAdminRepository.findFirstByOrderByIdDesc()
			.orElseThrow(CoinPolicyNotFoundException::new);

		assertThat(result.getAttendance()).isEqualTo(addDto.getAttendance());
		assertThat(result.getNormal()).isEqualTo(addDto.getNormal());
		assertThat(result.getRankWin()).isEqualTo(addDto.getRankWin());
		assertThat(result.getRankLose()).isEqualTo(addDto.getRankLose());
	}
}
