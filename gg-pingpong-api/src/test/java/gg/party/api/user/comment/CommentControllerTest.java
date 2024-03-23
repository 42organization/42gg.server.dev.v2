package gg.party.api.user.comment;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.EnumSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.auth.utils.AuthTokenProvider;
import gg.data.party.Category;
import gg.data.party.PartyPenalty;
import gg.data.party.Room;
import gg.data.party.UserRoom;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.party.api.user.comment.controller.request.CommentCreateReqDto;
import gg.repo.party.CommentRepository;
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
public class CommentControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private TestDataUtils testDataUtils;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private AuthTokenProvider tokenProvider;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private RoomRepository roomRepository;

	private User userTester;
	User reportedTester;
	String userAccessToken;
	String reportedAccessToken;
	Room openRoom;

	@BeforeEach
	void beforeEach() {
		userTester = testDataUtils.createNewUser("commentUserTester", "emailTester",
			RacketType.DUAL, SnsType.SLACK, RoleType.USER);
		reportedTester = testDataUtils.createNewUser("reportedTester", "reportedTester",
			RacketType.DUAL, SnsType.SLACK, RoleType.USER);
		PartyPenalty testPenalty = testDataUtils.createNewPenalty(reportedTester, "penaltytype", "becauseTest",
			LocalDateTime.now(), 60);
		userAccessToken = tokenProvider.createToken(userTester.getId());
		reportedAccessToken = tokenProvider.createToken(reportedTester.getId());
		Category category = testDataUtils.createNewCategory("tstcategry");
		openRoom = testDataUtils.createNewRoom(userTester, userTester, category, 1, 1, 10, 2, 120, RoomType.OPEN);
		UserRoom userRoom = testDataUtils.createNewUserRoom(userTester, openRoom, "닉네임테스트", true);
	}

	@Nested
	@DisplayName("댓글 생성 테스트")
	class CreateCommentTest {

		/**
		 * 댓글 생성 성공시 201 반환
		 * 유저가 방에 참여 한 상태일때만 댓글 생성 가능 (참여 안하면 댓글 생성 불가)
		 */
		@Test
		@DisplayName("댓글 생성 성공 201")
		void createCommentSuccess() throws Exception {
			String uri = "/party/rooms/{room_id}/comments";
			CommentCreateReqDto reqDto = new CommentCreateReqDto();
			reqDto.saveContent("이것은 댓글, 나는 success해야한다");

			String content = objectMapper.writeValueAsString(reqDto);

			mockMvc.perform(post(uri, openRoom.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.content(content)
					.header("Authorization", "Bearer " + userAccessToken))
				.andExpect(status().isCreated());

			assertThat(commentRepository.findByRoomId(openRoom.getId())).hasSize(1);
		}

		/**
		 * 신고된 유저일 경우 댓글 생성 실패하는지 테스트(403나오면 정상)
		 */
		@Test
		@DisplayName("신고된 사용자 댓글 생성 실패")
		void createCommentFailForReportedUser() throws Exception {
			String uri = "/party/rooms/{room_id}/comments";
			CommentCreateReqDto reqDto = new CommentCreateReqDto();
			reqDto.saveContent("나는 신고된 유저, 댓글 생성 should fail");

			String content = objectMapper.writeValueAsString(reqDto);

			mockMvc.perform(post(uri, openRoom.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.content(content)
					.header("Authorization", "Bearer " + reportedAccessToken))
				.andExpect(status().isForbidden());

			assertThat(commentRepository.findByRoomId(openRoom.getId())).isEmpty();
		}

		/**
		 * 방 상태가 시작전(OPEN)이 아닐때 댓글 생성 실패하는지 테스트
		 * 오직 OPEN일때만 댓글 생성 가능
		 */
		@Test
		@DisplayName("방 시작 전의 상황이 아닐때(START, FINISH, HIDDEN, FAIl) 댓글 생성 실패하는지")
		void createCommentFailWhenRoomNotOpen() throws Exception {
			String uri = "/party/rooms/{room_id}/comments";
			EnumSet<RoomType> notOpenStatuses = EnumSet.of(RoomType.START, RoomType.FINISH, RoomType.HIDDEN,
				RoomType.FAIL);

			for (RoomType status : notOpenStatuses) {
				openRoom.updateRoomStatus(status);
				roomRepository.save(openRoom);

				CommentCreateReqDto reqDto = new CommentCreateReqDto();
				reqDto.saveContent("This is a test comment.");

				String content = objectMapper.writeValueAsString(reqDto);

				mockMvc.perform(post(uri, openRoom.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(content)
						.header("Authorization", "Bearer " + userAccessToken))
					.andExpect(
						status().isBadRequest());

				assertThat(commentRepository.findByRoomId(openRoom.getId())).isEmpty();
			}
		}
	}
}

