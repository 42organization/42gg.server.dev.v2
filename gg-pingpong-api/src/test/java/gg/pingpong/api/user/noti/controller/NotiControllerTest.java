package gg.pingpong.api.user.noti.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.data.noti.Noti;
import gg.data.noti.type.NotiType;
import gg.data.user.User;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.user.noti.controller.response.NotiListResponseDto;
import gg.pingpong.api.user.noti.service.NotiService;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.repo.noti.NotiRepository;
import gg.repo.user.UserRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class NotiControllerTest {

	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	AuthTokenProvider tokenProvider;
	@Autowired
	UserRepository userRepository;
	@Autowired
	NotiRepository notiRepository;
	@Autowired
	private NotiService notiService;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("GET /pingpong/notifications")
	@Transactional
	public void notiFindByUserTest() throws Exception {
		//given
		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		String url = "/pingpong/notifications";

		UserDto userDto = UserDto.from(userRepository.getById(userId));
		NotiListResponseDto expectedResponse = new NotiListResponseDto(notiService.findNotiByUser(userDto));
		//when
		String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
		NotiListResponseDto actureResponse = objectMapper.readValue(contentAsString, NotiListResponseDto.class);

		//then
		assertThat(actureResponse).isEqualTo(expectedResponse);
	}

	@Test
	@DisplayName("PUT /pingpong/notifications/check")
	@Transactional
	public void checkNotiByUserTest() throws Exception {
		//given
		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		String url = "/pingpong/notifications/check";
		User user = userRepository.findById(userId).get();

		notiRepository.save(new Noti(user, NotiType.ANNOUNCE, "announce", false));
		notiRepository.save(new Noti(user, NotiType.MATCHED, "matched", false));
		notiRepository.save(new Noti(user, NotiType.IMMINENT, "imminent", true));
		notiRepository.save(new Noti(user, NotiType.CANCELEDBYMAN, "canceledbyman", false));
		notiRepository.save(new Noti(user, NotiType.CANCELEDBYTIME, "canceledbytime", false));
		//when
		String contentAsString = mockMvc.perform(put(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().is2xxSuccessful())
			.andReturn().getResponse().getContentAsString();

		//then
		List<Noti> notiList = notiRepository.findByUser(user);
		for (Noti noti : notiList) {
			assertThat(noti.getIsChecked()).isTrue();
		}
	}

	@Test
	@DisplayName("DELETE /notifications")
	@Transactional
	public void notiRemoveAll() throws Exception {
		//given
		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		String url = "/pingpong/notifications";
		User user = userRepository.findById(userId).get();

		notiRepository.save(new Noti(user, NotiType.ANNOUNCE, "announce", false));
		notiRepository.save(new Noti(user, NotiType.MATCHED, "matched", false));
		notiRepository.save(new Noti(user, NotiType.IMMINENT, "imminent", true));
		notiRepository.save(new Noti(user, NotiType.CANCELEDBYMAN, "canceledbyman", false));
		notiRepository.save(new Noti(user, NotiType.CANCELEDBYTIME, "canceledbytime", false));

		//when
		String contentAsString = mockMvc.perform(delete(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().is2xxSuccessful())
			.andReturn().getResponse().getContentAsString();

		//then
		List<Noti> notiList = notiRepository.findByUser(user);
		assertThat(notiList.size()).isEqualTo(0);
	}
}
