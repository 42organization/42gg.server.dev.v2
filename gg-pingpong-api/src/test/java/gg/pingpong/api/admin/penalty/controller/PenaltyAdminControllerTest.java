package gg.pingpong.api.admin.penalty.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.http.HttpHeaders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.pingpong.admin.repo.penalty.PenaltyAdminRepository;
import gg.pingpong.admin.repo.penalty.PenaltyUserAdminRedisRepository;
import gg.pingpong.api.admin.penalty.dto.PenaltyListResponseDto;
import gg.pingpong.api.admin.penalty.dto.PenaltyRequestDto;
import gg.pingpong.api.admin.penalty.service.PenaltyAdminService;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.data.manage.Penalty;
import gg.pingpong.data.manage.redis.RedisPenaltyUser;
import gg.pingpong.data.manage.type.PenaltyType;
import gg.pingpong.data.user.User;
import gg.pingpong.data.user.type.RacketType;
import gg.pingpong.data.user.type.RoleType;
import gg.pingpong.data.user.type.SnsType;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.TestDataUtils;
import gg.pingpong.utils.annotation.IntegrationTest;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class PenaltyAdminControllerTest {
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	UserRepository userRepository;
	@Autowired
	PenaltyUserAdminRedisRepository penaltyUserAdminRedisRepository;
	@Autowired
	PenaltyAdminRepository penaltyAdminRepository;
	@Autowired
	AuthTokenProvider tokenProvider;
	@Autowired
	RedisConnectionFactory redisConnectionFactory;
	@Autowired
	PenaltyAdminService penaltyAdminService;

	private final String headUrl = "/pingpong/admin/";

	@AfterEach
	void clear() {
		RedisConnection connection = redisConnectionFactory.getConnection();
		connection.flushDb();
		connection.close();
	}

	@Test
	@DisplayName("POST : penalty를 부여받지 않은 유효한 intraId에 penalty 부여")
	public void giveUserPenaltyforFirstTimeWithValidIntraId() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		tokenProvider.getUserIdFromAccessToken(accessToken);
		User newUser = testDataUtils.createNewUser();
		String intraId = newUser.getIntraId();
		String url = "/pingpong/admin/penalty";
		mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new PenaltyRequestDto(intraId, 3, "test1"))))
			.andExpect(status().isCreated());
		Optional<RedisPenaltyUser> penaltyUser = penaltyUserAdminRedisRepository.findByIntraId(intraId);
		//redis
		Assertions.assertThat(penaltyUser).isPresent();
		Assertions.assertThat(penaltyUser.get().getPenaltyTime()).isEqualTo(3 * 60);
		Assertions.assertThat(
			Duration.between(penaltyUser.get().getStartTime(),
				penaltyUser.get().getReleaseTime()).getSeconds()).isEqualTo(3 * 60 * 60);
		Assertions.assertThat(penaltyUser.get().getReason());
		//mySQL
		List<Penalty> penalties = penaltyAdminRepository.findAll();
		Assertions.assertThat(penalties.stream().anyMatch(ele -> ele.getUser().getIntraId().equals(intraId)
			&& ele.getPenaltyTime().equals(3 * 60))).isEqualTo(true);
	}

	@Test
	@DisplayName("POST : penalty를 부여받은 유효한 intraId에 penalty 부여")
	public void giveUserPenaltyRepeatablyWithValidIntraId() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		tokenProvider.getUserIdFromAccessToken(accessToken);
		User newUser = testDataUtils.createNewUser();
		String intraId = newUser.getIntraId();
		String url = "/pingpong/admin/penalty";
		//패널티 두번 부여
		mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new PenaltyRequestDto(intraId, 3, "test1"))))
			.andExpect(status().isCreated());
		mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new PenaltyRequestDto(intraId, 2, "test2"))))
			.andExpect(status().isCreated());
		Optional<RedisPenaltyUser> penaltyUser = penaltyUserAdminRedisRepository.findByIntraId(intraId);
		//redis 확인
		Assertions.assertThat(penaltyUser).isPresent();
		Assertions.assertThat(penaltyUser.get().getPenaltyTime()).isEqualTo(5 * 60);
		Assertions.assertThat(
			Duration.between(penaltyUser.get().getStartTime(),
				penaltyUser.get().getReleaseTime()).getSeconds()).isEqualTo(5 * 60 * 60);
		Assertions.assertThat(penaltyUser.get().getReason());
		//mySQL 확인
		List<Penalty> penalties = penaltyAdminRepository.findAll();
		List<Penalty> userPenalties = penalties.stream().filter(ele -> ele.getUser().getIntraId().equals(intraId))
			.collect(Collectors.toList());
		Assertions.assertThat(userPenalties.size()).isEqualTo(2);
		Duration duration = Duration.between(userPenalties.get(0).getStartTime(), userPenalties.get(1).getStartTime());
		Assertions.assertThat(duration.getSeconds()).isEqualTo(3 * 60 * 60);
	}

	@Test
	@DisplayName("POST 유효하지 않은 intraId에 penalty 부여")
	public void giveUserPenaltyWithInvalidIntraId() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		tokenProvider.getUserIdFromAccessToken(accessToken);
		String intraId = "invalid!";
		String url = "/pingpong/admin/penalty";
		mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new PenaltyRequestDto(intraId, 3, "test1"))))
			.andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("GET pagination 유효성 검사")
	public void checkPagination() throws Exception {
		List<User> users = new ArrayList<User>();
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		tokenProvider.getUserIdFromAccessToken(accessToken);
		//penalty user 20명 넣고 테스트
		for (int i = 0; i < 20; i++) {
			User newUser = testDataUtils.createNewUser();
			users.add(newUser);
			penaltyAdminService.givePenalty(newUser.getIntraId(), 3, "test" + String.valueOf(i));
		}
		List<Integer> sizeCounts = new ArrayList<Integer>();
		Integer totalPages = -1;
		for (int i = 1; i <= 3; i++) {
			String url = "/pingpong/admin/penalty?page=" + String.valueOf(i) + "&size=10&current=true";
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			PenaltyListResponseDto penaltyListResponseDto = objectMapper.readValue(contentAsString,
				PenaltyListResponseDto.class);
			sizeCounts.add(penaltyListResponseDto.getPenaltyList().size());
			totalPages = penaltyListResponseDto.getTotalPage();
		}
		Assertions.assertThat(sizeCounts).isEqualTo(List.of(10, 10, 0));
		Assertions.assertThat(totalPages).isEqualTo(2);
	}

	@Test
	@DisplayName("GET parameter 유효성 검사")
	public void checkInputException() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		tokenProvider.getUserIdFromAccessToken(accessToken);
		String url = "/pingpong/admin/penalty?page=-1&size=10&current=false";
		String contentAsString = mockMvc.perform(
				get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		String url2 = "/pingpong/admin/penalty?page=2&size=0&current=false";
		String contentAsString2 = mockMvc.perform(
				get(url2).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
	}

	@Test
	@DisplayName("GET pagination keyword 유효성 검사")
	public void checkPaginationWithKeyword() throws Exception {
		List<User> users = new ArrayList<User>();
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		tokenProvider.getUserIdFromAccessToken(accessToken);
		//penalty user 40명 넣고 테스트
		//그중 20명만 intraId에 test포함
		for (int i = 0; i < 20; i++) {
			String intraId = UUID.randomUUID().toString().substring(0, 4) + "test" + UUID.randomUUID().toString()
				.substring(0, 4);
			User newUser = testDataUtils.createNewUser(intraId, "test", RacketType.NONE, SnsType.EMAIL,
				RoleType.USER);
			users.add(newUser);
			penaltyAdminService.givePenalty(newUser.getIntraId(), 3, "test" + String.valueOf(i));
		}
		for (int i = 0; i < 20; i++) {
			String intraId = "dummy" + String.valueOf(i);
			User newUser = testDataUtils.createNewUser(intraId, "test", RacketType.NONE, SnsType.EMAIL,
				RoleType.USER);
			users.add(newUser);
			penaltyAdminService.givePenalty(newUser.getIntraId(), 3, "test" + String.valueOf(i));
		}
		List<Integer> sizeCounts = new ArrayList<Integer>();
		Integer totalPages = -1;
		for (int i = 1; i <= 3; i++) {
			String url = "/pingpong/admin/penalty?page=" + String.valueOf(i) + "&size=10&current=true&intraId=test";
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			PenaltyListResponseDto penaltyListResponseDto = objectMapper.readValue(contentAsString,
				PenaltyListResponseDto.class);
			sizeCounts.add(penaltyListResponseDto.getPenaltyList().size());
			totalPages = penaltyListResponseDto.getTotalPage();
		}
		Assertions.assertThat(sizeCounts).isEqualTo(List.of(10, 10, 0));
		Assertions.assertThat(totalPages).isEqualTo(2);
	}

	@Test
	@DisplayName("DELETE 패널티 삭제 - 유저 패널티가 1번만 부과된 경우")
	public void deleteExistPenaltyUser() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		tokenProvider.getUserIdFromAccessToken(accessToken);
		User newUser = testDataUtils.createNewUser();
		String intraId = newUser.getIntraId();
		penaltyAdminService.givePenalty(intraId, 3, "test!");
		List<Penalty> penalties = penaltyAdminRepository.findAll();
		List<Penalty> userPenalties = penalties.stream().filter(ele -> ele.getUser().getIntraId().equals(intraId))
			.collect(Collectors.toList());
		Long penaltyId = userPenalties.get(0).getId();
		String url = "/pingpong/admin/penalty/" + penaltyId.toString();
		mockMvc.perform(
				delete(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isNoContent());
		//Redis에 penaltyUser 없는지 확인
		Optional<RedisPenaltyUser> penaltyUser = penaltyUserAdminRedisRepository.findByIntraId(intraId);
		Assertions.assertThat(penaltyUser).isEmpty();
		//MySQL에 penalty 없는지 확인
		List<Penalty> afterPenalties = penaltyAdminRepository.findAll();
		boolean isPresent = afterPenalties.stream().anyMatch(ele -> ele.getId().equals(penaltyId));
		Assertions.assertThat(isPresent).isEqualTo(false);
	}

	@Test
	@DisplayName("DELETE 패널티 삭제 - 유저 패널티가 2번 부과된 경우")
	public void deleteExistPenaltyUserOfTwicePenalty() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		tokenProvider.getUserIdFromAccessToken(accessToken);
		User newUser = testDataUtils.createNewUser();
		String intraId = newUser.getIntraId();
		penaltyAdminService.givePenalty(intraId, 3, "test!");
		penaltyAdminService.givePenalty(intraId, 2, "test2");
		List<Penalty> penalties = penaltyAdminRepository.findAll();
		List<Penalty> userPenalties = penalties.stream().filter(ele -> ele.getUser().getIntraId().equals(intraId))
			.collect(Collectors.toList());
		Long penaltyId = userPenalties.get(0).getId();
		String url = "/pingpong/admin/penalty/" + penaltyId.toString();
		mockMvc.perform(
				delete(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isNoContent());
		//Redis에 penaltyUser 있는지 확인
		Optional<RedisPenaltyUser> penaltyUser = penaltyUserAdminRedisRepository.findByIntraId(intraId);
		Assertions.assertThat(penaltyUser.get().getPenaltyTime()).isEqualTo(2 * 60);

		//MySQL에 penalty 없는지 확인
		List<Penalty> afterPenalties = penaltyAdminRepository.findAll();
		boolean isPresent = afterPenalties.stream().anyMatch(ele -> ele.getId().equals(penaltyId));
		Assertions.assertThat(isPresent).isEqualTo(false);
	}

	@Test
	@DisplayName("DELETE 존재하지 않는 패널티 유저 삭제")
	public void deleteInvalidPenaltyUser() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		tokenProvider.getUserIdFromAccessToken(accessToken);
		//user에 패널티는 부여하지 않는다.
		User newUser = testDataUtils.createNewUser();
		String intraId = newUser.getIntraId();
		String url = "/pingpong/admin/penalty/users/" + intraId;
		mockMvc.perform(
				delete(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("DELETE 존재하지 않는 패널티 유저 삭제")
	public void deleteInvalidIntraId() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		tokenProvider.getUserIdFromAccessToken(accessToken);
		//30자 이상
		String intraId = UUID.randomUUID().toString();
		String url = "/pingpong/admin/penalty/users/" + intraId;
		mockMvc.perform(
				delete(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("get pingpong/admin/penalty?page={page}&size={pageSize}&current=true")
	public void getCurrentPenalties() throws Exception {
		List<User> users = new ArrayList<User>();
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		tokenProvider.getUserIdFromAccessToken(accessToken);
		//penalty user 20명 넣고 테스트
		for (int i = 0; i < 20; i++) {
			User newUser = testDataUtils.createNewUser();
			users.add(newUser);
		}

		//과거 penalty들 db에 저장
		for (int i = 0; i < 20; i++) {
			Penalty penalty = new Penalty(users.get(i), PenaltyType.NOSHOW, "test", LocalDateTime.now().minusHours(3),
				120);
			penaltyAdminRepository.save(penalty);
		}

		//현재 패널티 부여
		for (int i = 0; i < 20; i++) {
			penaltyAdminService.givePenalty(users.get(i).getIntraId(), 3, "test" + String.valueOf(i));
		}

		List<Integer> sizeCounts = new ArrayList<Integer>();
		Integer totalPages = -1;
		for (int i = 1; i <= 3; i++) {
			String url = "/pingpong/admin/penalty?page=" + String.valueOf(i) + "&size=10&current=true";
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			PenaltyListResponseDto penaltyListResponseDto = objectMapper.readValue(contentAsString,
				PenaltyListResponseDto.class);
			sizeCounts.add(penaltyListResponseDto.getPenaltyList().size());
			totalPages = penaltyListResponseDto.getTotalPage();
		}
		Assertions.assertThat(sizeCounts).isEqualTo(List.of(10, 10, 0));
		Assertions.assertThat(totalPages).isEqualTo(2);
	}
}
