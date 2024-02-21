package gg.pingpong.api.admin.receipt.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.pingpong.admin.repo.receipt.ReceiptAdminRepository;
import gg.pingpong.api.admin.item.dto.ItemUpdateRequestDto;
import gg.pingpong.api.admin.receipt.dto.ReceiptListResponseDto;
import gg.pingpong.api.admin.receipt.service.ReceiptAdminService;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.utils.ItemTestUtils;
import gg.pingpong.api.utils.TestDataUtils;
import gg.pingpong.api.utils.annotation.IntegrationTest;
import gg.pingpong.data.store.Item;
import gg.pingpong.data.store.type.ItemType;
import gg.pingpong.data.user.User;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class ReceiptAdminControllerTest {
	@Autowired
	ReceiptAdminService receiptAdminService;
	@Autowired
	ReceiptAdminRepository receiptAdminRepository;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AuthTokenProvider tokenProvider;
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ItemTestUtils itemTestUtils;
	Item item;
	User user;

	@BeforeEach
	void setUp() {
		user = testDataUtils.createNewUser();
		ItemUpdateRequestDto dto = new ItemUpdateRequestDto("name", "mainContent", "subContent",
			100, 50, ItemType.EDGE);
		item = itemTestUtils.createItem(testDataUtils.createAdminUserForItem(), dto);
	}

	@Test
	@DisplayName("GET /pingpong/admin/receipt")
	public void getAllReceipt() throws Exception {
		itemTestUtils.purchaseItem(user, user, item);
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Integer page = 1;
		Integer size = 20;
		String url = "/pingpong/admin/receipt?page=" + page + "&size=" + size;
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
		String contentAsString = mockMvc.perform(get(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
		ReceiptListResponseDto expect = receiptAdminService.getAllReceipt(pageable);
		ReceiptListResponseDto result = objectMapper.readValue(contentAsString, ReceiptListResponseDto.class);
		assertThat(result.getReceiptList().get(0).getReceiptId()).isEqualTo(
			expect.getReceiptList().get(0).getReceiptId());
		assertThat(result.getReceiptList().get(0).getCreatedAt()).isEqualTo(
			expect.getReceiptList().get(0).getCreatedAt());
		assertThat(result.getReceiptList().get(0).getItemName()).isEqualTo(
			expect.getReceiptList().get(0).getItemName());
		assertThat(result.getReceiptList().get(0).getItemPrice()).isEqualTo(
			expect.getReceiptList().get(0).getItemPrice());
		assertThat(result.getReceiptList().size()).isEqualTo(expect.getReceiptList().size());
		assertThat(result.getReceiptList().size()).isEqualTo(1);
	}

	@Test
	@DisplayName("GET /pingpong/admin/receipt")
	public void findByIntraId() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Integer page = 1;
		Integer size = 20;
		User targetUser = testDataUtils.createNewUser("david");
		itemTestUtils.purchaseItem(user, user, item);
		itemTestUtils.purchaseItem(targetUser, targetUser, item);

		String url = "/pingpong/admin/receipt?page=" + page + "&size=" + size + "&intraId=" + targetUser.getIntraId();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
		String contentAsString = mockMvc.perform(get(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
		ReceiptListResponseDto expect = receiptAdminService.findByIntraId(targetUser.getIntraId(), pageable);
		ReceiptListResponseDto result = objectMapper.readValue(contentAsString, ReceiptListResponseDto.class);
		assertThat(result.getReceiptList().get(0).getReceiptId()).isEqualTo(
			expect.getReceiptList().get(0).getReceiptId());
		assertThat(result.getReceiptList().get(0).getCreatedAt()).isEqualTo(
			expect.getReceiptList().get(0).getCreatedAt());
		assertThat(result.getReceiptList().get(0).getItemName()).isEqualTo(
			expect.getReceiptList().get(0).getItemName());
		assertThat(result.getReceiptList().get(0).getItemPrice()).isEqualTo(
			expect.getReceiptList().get(0).getItemPrice());
		assertThat(result.getReceiptList().size()).isEqualTo(expect.getReceiptList().size());
		assertThat(result.getReceiptList().size()).isEqualTo(1);
	}
}
