package gg.pingpong.api.admin.slotmanagement.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.pingpong.admin.repo.slotmanagement.AdminSlotManagementsRepository;
import gg.pingpong.api.admin.slotmanagement.controller.request.SlotCreateRequestDto;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.data.manage.SlotManagement;
import gg.pingpong.repo.slotmanagement.SlotManagementRepository;
import gg.pingpong.utils.TestDataUtils;
import gg.pingpong.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class SlotAdminControllerFailTest {
	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	AuthTokenProvider tokenProvider;

	@Autowired
	AdminSlotManagementsRepository adminSlotManagementRepository;

	@Autowired
	SlotManagementRepository slotManagementRepository;

	@Test
	@DisplayName("fail[Post]/pingpong/admin/slot-management")
	void failModifySlotSetting() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		SlotCreateRequestDto test = new SlotCreateRequestDto(4, 1, 20, null, LocalDateTime.now().plusDays(2));
		String content = objectMapper.writeValueAsString(test);

		String contentAsString = mockMvc.perform(post("/pingpong/admin/slot-management")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isBadRequest())
			.andReturn().getResponse().getContentAsString();

		System.out.println(contentAsString);
	}

	@Test
	@DisplayName("fail[Post]/pingpong/admin/slot-management")
	void endTimeCloseThenFuture() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		SlotCreateRequestDto test = new SlotCreateRequestDto(4, 1, 20, 1, LocalDateTime.now().plusHours(1));
		String content = objectMapper.writeValueAsString(test);

		String contentAsString = mockMvc.perform(post("/pingpong/admin/slot-management")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().is4xxClientError())
			.andReturn().getResponse().getContentAsString();

		System.out.println(contentAsString);
	}

	@Test
	@DisplayName("fail[Post]/pingpong/admin/slot-management")
	void intervalIsSeven() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		SlotCreateRequestDto test = new SlotCreateRequestDto(4, 1, 7, 1, LocalDateTime.now().plusHours(1));
		String content = objectMapper.writeValueAsString(test);

		String contentAsString = mockMvc.perform(post("/pingpong/admin/slot-management")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().is4xxClientError())
			.andReturn().getResponse().getContentAsString();

		System.out.println(contentAsString);
	}

	@Test
	@DisplayName("상대방공개시간이게임시간보다클떄")
	void enemyOpenCloseThenGameTime() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		SlotCreateRequestDto test = new SlotCreateRequestDto(4, 1, 10, 15,
			LocalDateTime.now().plusHours(1));
		String content = objectMapper.writeValueAsString(test);

		String contentAsString = mockMvc.perform(post("/pingpong/admin/slot-management")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().is4xxClientError())
			.andReturn().getResponse().getContentAsString();

		System.out.println(contentAsString);
	}

	@Test
	@DisplayName("슬롯정보가현재적용중인경우")
	void slotAlreadyApply() throws Exception {
		SlotManagement preSlot = SlotManagement.builder()
			.futureSlotTime(12)
			.pastSlotTime(0)
			.openMinute(5)
			.gameInterval(15)
			.startTime(LocalDateTime.now().minusDays(1))
			.build();
		slotManagementRepository.save(preSlot);

		String accessToken = testDataUtils.getAdminLoginAccessToken();

		List<SlotManagement> slotManagements = adminSlotManagementRepository.findAllByOrderByCreatedAtDesc();
		for (SlotManagement slot : slotManagements) {
			System.out.println("-----------------------");
			System.out.println(slot.getFutureSlotTime());
			System.out.println(slot.getPastSlotTime());
			System.out.println(slot.getOpenMinute());
			System.out.println(slot.getStartTime());
			System.out.println(slot.getEndTime());
			System.out.println(slot.getId());
		}

		String contentAsString = mockMvc.perform(delete("/pingpong/admin/slot-management")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().is4xxClientError())
			.andReturn().getResponse().getContentAsString();

		System.out.println(contentAsString);

		List<SlotManagement> slotManagements2 = adminSlotManagementRepository.findAllByOrderByCreatedAtDesc();
		for (SlotManagement slot : slotManagements2) {
			System.out.println("-----------------------");
			System.out.println(slot.getFutureSlotTime());
			System.out.println(slot.getPastSlotTime());
			System.out.println(slot.getOpenMinute());
			System.out.println(slot.getStartTime());
			System.out.println(slot.getEndTime());
			System.out.println(slot.getId());
		}
	}
}
