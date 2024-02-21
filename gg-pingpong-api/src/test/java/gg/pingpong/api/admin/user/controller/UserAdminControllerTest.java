package gg.pingpong.api.admin.user.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.http.HttpHeaders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.pingpong.admin.repo.user.UserAdminRepository;
import gg.pingpong.admin.repo.user.UserImageAdminRepository;
import gg.pingpong.api.admin.user.dto.UserDetailAdminResponseDto;
import gg.pingpong.api.admin.user.dto.UserImageAdminDto;
import gg.pingpong.api.admin.user.dto.UserImageListAdminResponseDto;
import gg.pingpong.api.admin.user.dto.UserSearchAdminDto;
import gg.pingpong.api.admin.user.dto.UserSearchAdminResponseDto;
import gg.pingpong.api.admin.user.service.UserAdminService;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.utils.TestDataUtils;
import gg.pingpong.data.user.User;
import gg.pingpong.data.user.UserImage;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.annotation.IntegrationTest;
import gg.pingpong.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@IntegrationTest
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
	@Autowired
	UserImageAdminRepository userImageAdminRepository;

	@BeforeEach
	public void setUp() {
		testDataUtils.createTierSystem("pingpong");
		testDataUtils.createSeason();
	}

	@Test
	@DisplayName("GET /pingpong/admin/users")
	@Transactional
	public void userSearchAllTest() throws Exception {
		//given
		String accessToken = testDataUtils.getAdminLoginAccessToken();
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
		UserSearchAdminResponseDto actureResponse1 = objectMapper.readValue(contentAsString,
			UserSearchAdminResponseDto.class);
		//200 성공
		contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
		UserSearchAdminResponseDto actureResponse2 = objectMapper.readValue(contentAsString,
			UserSearchAdminResponseDto.class);

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
	public void userFiltering() throws Exception {
		//given
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User user = userRepository.findById(userId).get();
		int page = 1;
		int size = 20;
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("intraId").ascending());
		String url = "/pingpong/admin/users?page=1&userFilter=\"" + user.getIntraId() + "\"";

		//when
		String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION,
				"Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
		UserSearchAdminResponseDto actureResponse = objectMapper.readValue(contentAsString,
			UserSearchAdminResponseDto.class);

		List<UserSearchAdminDto> actureUserList = actureResponse.getUserSearchAdminDtos();
		for (UserSearchAdminDto userDto : actureUserList) {
			Assertions.assertThat(userDto.getIntraId()).isEqualTo(user.getIntraId());
		}
	}

	@Test
	@DisplayName("GET /pingpong/admin/users/{intraId}")
	@Transactional
	public void userGetDetailTest() throws Exception {
		//given
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User user = testDataUtils.createNewUser("nheo");
		User target = userRepository.findByIntraId(user.getIntraId()).get();
		String url = "/pingpong/admin/users/" + user.getIntraId();
		UserDetailAdminResponseDto expectedResponse = userAdminService.getUserDetailByIntraId(user.getIntraId());

		//when
		//200 성공
		String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
		UserDetailAdminResponseDto actureResponse = objectMapper.readValue(contentAsString,
			UserDetailAdminResponseDto.class);

		//then
		Assertions.assertThat(actureResponse.getUserId()).isEqualTo(expectedResponse.getUserId());
		Assertions.assertThat(actureResponse.getIntraId()).isEqualTo(expectedResponse.getIntraId());
		Assertions.assertThat(actureResponse.getUserImageUri()).isEqualTo(expectedResponse.getUserImageUri());
		Assertions.assertThat(actureResponse.getRacketType()).isEqualTo(expectedResponse.getRacketType());
		Assertions.assertThat(actureResponse.getWins()).isEqualTo(expectedResponse.getWins());
		Assertions.assertThat(actureResponse.getLosses()).isEqualTo(expectedResponse.getLosses());
		Assertions.assertThat(actureResponse.getPpp()).isEqualTo(expectedResponse.getPpp());
		Assertions.assertThat(actureResponse.getEmail()).isEqualTo(expectedResponse.getEmail());
		Assertions.assertThat(actureResponse.getRoleType()).isEqualTo(expectedResponse.getRoleType());
		Assertions.assertThat(actureResponse.getExp()).isEqualTo(expectedResponse.getExp());
		Assertions.assertThat(actureResponse.getCoin()).isEqualTo(expectedResponse.getCoin());
	}

	/**
	 * 추가적으로 의도 확인이 필요한 테스트
	 * @throws Exception
	 */
	@Test
	@DisplayName("DELETE /pingpong/admin/users/{intraId}")
	@Transactional
	public void deleteUserProfileImageTest() throws Exception {
		//given
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User klew = testDataUtils.createNewUser("klew");
		User user = userRepository.findByIntraId(klew.getIntraId()).get();
		testDataUtils.createUserImages(user, 2);
		String url = "/pingpong/admin/users/images/" + user.getIntraId();
		UserImage prevUserImage = userImageAdminRepository.findTopByUserAndIsCurrentIsTrueOrderByCreatedAtDesc(user)
			.orElseThrow(UserNotFoundException::new);
		//when
		//200 성공
		mockMvc.perform(delete(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("GET /pingpong/admin/users/delete-list")
	@Transactional
	public void getUserImageDeleteListTest() throws Exception {
		//given
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		int page = 1;
		int size = 30;
		String url = "/pingpong/admin/users/delete-list?page=1";

		//when
		//200 성공
		String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
		UserImageListAdminResponseDto actureResponse = objectMapper.readValue(contentAsString,
			UserImageListAdminResponseDto.class);

		//then
		//각 유저의 이미지가 삭제된 이미지인지 확인
		List<UserImageAdminDto> actureUserImageList = actureResponse.getUserImageList();
		for (UserImageAdminDto userImageDto : actureUserImageList) {
			Assertions.assertThat(userImageDto.getDeletedAt()).isNotEqualTo(null);
		}
	}

	@Test
	@DisplayName("GET /pingpong/admin/users/images")
	@Transactional
	public void getUserImageListTest() throws Exception {
		//given
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		String url = "/pingpong/admin/users/images?page=1";
		testDataUtils.createUserImages(testDataUtils.createNewUser(), 4);

		//when
		//200 성공
		String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		//then
		//각 유저의 이미지가 삭제된 이미지인지 확인
		UserImageListAdminResponseDto actureResponse = objectMapper.readValue(contentAsString,
			UserImageListAdminResponseDto.class);
		assertThat(actureResponse.getUserImageList().size()).isEqualTo(3);
	}

	@Test
	@DisplayName("GET /pingpong/admin/users/images/{intraId}")
	@Transactional
	public void getUserImageListByIntraIdTest() throws Exception {
		//given
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		User user = testDataUtils.createNewUser("klew");
		String url = "/pingpong/admin/users/images/" + user.getIntraId() + "?page=1";
		testDataUtils.createUserImages(user, 3);

		//when
		//200 성공
		String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		//then
		//각 유저의 이미지가 삭제된 이미지인지 확인
		UserImageListAdminResponseDto actureResponse = objectMapper.readValue(contentAsString,
			UserImageListAdminResponseDto.class);
		assertThat(actureResponse.getUserImageList().size()).isEqualTo(2);
	}
}
