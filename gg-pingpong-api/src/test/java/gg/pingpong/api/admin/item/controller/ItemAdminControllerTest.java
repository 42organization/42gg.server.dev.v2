package gg.pingpong.api.admin.item.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URL;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.admin.repo.store.ItemAdminRepository;
import gg.auth.utils.AuthTokenProvider;
import gg.data.pingpong.store.Item;
import gg.data.pingpong.store.type.ItemType;
import gg.pingpong.api.admin.store.controller.request.ItemUpdateRequestDto;
import gg.pingpong.api.admin.store.controller.response.ItemListResponseDto;
import gg.pingpong.api.admin.store.service.ItemAdminService;
import gg.repo.user.UserRepository;
import gg.utils.ItemTestUtils;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.file.handler.AwsImageHandler;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class ItemAdminControllerTest {
	@Autowired
	ItemAdminService itemAdminService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AuthTokenProvider tokenProvider;
	@Autowired
	ItemAdminRepository itemAdminRepository;
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ItemTestUtils itemTestUtils;
	@MockBean
	AwsImageHandler imageHandler;

	@Value("${info.image.defaultUrl}")
	private String defaultImageUrl;

	Item item;

	@BeforeEach
	void setUp() {
		ItemUpdateRequestDto dto = new ItemUpdateRequestDto("name", "content",
			"subContent", 100, 50, ItemType.EDGE);
		item = itemTestUtils.createItem(testDataUtils.createAdminUserForItem(), dto);
	}

	@Test
	@DisplayName("GET /pingpong/admin/items/history")
	public void getAllItemHistoryTest() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Integer page = 1;
		Integer size = 20;
		String url = "/pingpong/admin/items/history?page=" + page + "&size=" + size;
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

		String contentAsString = mockMvc.perform(get(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		System.out.println(contentAsString);
		ItemListResponseDto expect = itemAdminService.getAllItemHistory(pageable);
		System.out.println(expect.getHistoryList());
		ItemListResponseDto result = objectMapper.readValue(contentAsString, ItemListResponseDto.class);
		System.out.println(expect.getHistoryList().get(0));
		System.out.println(result.getHistoryList().get(0));
		assertThat(result.getHistoryList().get(0).getItemId());
		assertThat(result.getHistoryList().get(0).getName());
		assertThat(result.getHistoryList().get(0).getMainContent());
		assertThat(result.getHistoryList().get(0).getSubContent());
		assertThat(result.getHistoryList().get(0).getPrice());
	}

	@Test
	@DisplayName("POST /pingpong/admin/items/history/{itemId}")
	public void updateItemTest() throws Exception {
		URL mockUrl = new URL(defaultImageUrl);
		Mockito.when(imageHandler.uploadImageOrDefault(Mockito.any(), Mockito.anyString(), defaultImageUrl))
			.thenReturn(mockUrl);

		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		String creatorId = userRepository.getById(userId).getIntraId();
		MockMultipartFile image = new MockMultipartFile("imgData", "imagefile.jpeg", "image/jpeg",
			"<<jpeg data>>".getBytes());
		MockMultipartFile jsonFile = new MockMultipartFile("updateItemInfo", "",
			"application/json",
			("{\"name\": \"TEST\", "
				+ "\"mainContent\": \"TESTING\", "
				+ "\"subContent\": \"TESTING\", "
				+ "\"price\": 42, "
				+ "\"discount\": 50, "
				+ "\"itemType\": \"MEGAPHONE\"}").getBytes());
		String contentAsString = mockMvc.perform(multipart("/pingpong/admin/items/{itemId}", item.getId())
				.file(image)
				.file(jsonFile)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isNoContent())
			.andReturn().getResponse().getContentAsString();
	}

	@Test
	@DisplayName("DELETE /pingpong/admin/items/{itemId}")
	public void deleteItemTest() throws Exception {
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		String deleterId = userRepository.getById(userId).getIntraId();

		String contentAsString = mockMvc.perform(delete("/pingpong/admin/items/{itemId}", item.getId())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isNoContent())
			.andReturn().getResponse().getContentAsString();
		List<Item> list = itemAdminRepository.findAll();
		assertThat(list.get(0).getDeleterIntraId()).isEqualTo(deleterId);
	}
}
