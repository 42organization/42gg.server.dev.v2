package gg.party.api.user.room;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.auth.utils.AuthTokenProvider;
import gg.data.party.Category;
import gg.data.party.Room;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.user.room.controller.response.RoomListResDto;
import gg.party.api.user.room.controller.response.RoomResDto;
import gg.repo.party.RoomRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RoomControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AuthTokenProvider tokenProvider;
	@Autowired
	RoomRepository roomRepository;
	String accessToken;

	@Nested
	@DisplayName("방 전체 조회 테스트")
	class FindAllActiveRoomList {
		@BeforeEach
		void beforeEach() {
			User userTester = testDataUtils.createNewUser("findControllerTester2", "findControllerTester",
				RacketType.DUAL,
				SnsType.SLACK, RoleType.USER);
			accessToken = tokenProvider.createToken(userTester.getId());
			Category testCategory = testDataUtils.createNewCategory("testCategory");
			Room openRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, LocalDateTime.now().plusHours(3), RoomType.OPEN);
			Room startRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				3, 2, LocalDateTime.now().plusHours(3), RoomType.START);
			Room finishRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 2,
				3, 2, LocalDateTime.now().minusHours(3), RoomType.FINISH);
			Room hiddenRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, LocalDateTime.now().plusHours(3), RoomType.OPEN);
			Room failRoom = testDataUtils.createNewRoom(userTester, userTester, testCategory, 1, 1,
				3, 2, LocalDateTime.now().minusHours(3), RoomType.FAIL);
			roomRepository.save(openRoom);
			roomRepository.save(startRoom);
			roomRepository.save(finishRoom);
			roomRepository.save(hiddenRoom);
			roomRepository.save(failRoom);
		}

		@Test
		@DisplayName("조회 성공")
		public void success() throws Exception {
			String url = "/party/rooms";

			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			RoomListResDto resp = objectMapper.readValue(contentAsString, RoomListResDto.class);

			List<RoomResDto> roomList = resp.getRoomList();
			for (RoomResDto responseDto : roomList) {
				assertThat(responseDto.getStatus()).isIn(RoomType.OPEN, RoomType.START);
			}
		}
	}
}
