package gg.pingpong.api.user.item.controller;

import static gg.data.pingpong.store.type.ItemType.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.auth.utils.AuthTokenProvider;
import gg.pingpong.api.user.store.controller.response.ItemStoreListResponseDto;
import gg.pingpong.api.user.store.controller.response.ItemStoreResponseDto;
import gg.pingpong.api.user.store.service.ItemService;
import gg.repo.store.ItemRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class ItemStoreListControllerTest {

	@Autowired
	TestDataUtils testDataUtils;

	@Autowired
	AuthTokenProvider tokenProvider;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ItemRepository itemRepository;

	@MockBean
	private ItemService itemService;

	@Test
	@DisplayName("[Get]/pingpong/items/store")
	public void getAllItemsTest() throws Exception {

		//given
		List<ItemStoreResponseDto> testItems = Arrays.asList(
			new ItemStoreResponseDto(1L, "itemName 1", "mainContent 1", "subContent 1", MEGAPHONE, "ImageUrl 1", 1000,
				10, 900),
			new ItemStoreResponseDto(2L, "itemName 2", "mainContent 2", "subContent 2", PROFILE_IMAGE, "ImageUrl 2",
				2000, 20, 1800),
			new ItemStoreResponseDto(3L, "itemName 3", "mainContent 2", "subContent 2", TEXT_COLOR, "ImageUrl 3", 3000,
				30, 2700)
		);
		ItemStoreListResponseDto testResponse = new ItemStoreListResponseDto(testItems);
		when(itemService.getAllItems()).thenReturn(testResponse);

		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		System.out.println(userId);
		String url = "/pingpong/items/store";

		//when
		String contentAsString = mockMvc.perform(get(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		//then
		ItemStoreListResponseDto result = objectMapper.readValue(contentAsString, ItemStoreListResponseDto.class);
		assertThat(result.getItemList()).isNotNull();
		assertThat(result.getItemList()).isNotEmpty();
		assertThat(result.getItemList()).hasSize(testItems.size());

		for (int i = 0; i < result.getItemList().size(); i++) {
			ItemStoreResponseDto actual = result.getItemList().get(i);
			ItemStoreResponseDto expected = testItems.get(i);

			assertThat(actual.getItemId()).isEqualTo(expected.getItemId());
			assertThat(actual.getItemName()).isEqualTo(expected.getItemName());
			assertThat(actual.getMainContent()).isEqualTo(expected.getMainContent());
			assertThat(actual.getSubContent()).isEqualTo(expected.getSubContent());
			assertThat(actual.getItemType()).isEqualTo(expected.getItemType());
			assertThat(actual.getImageUri()).isEqualTo(expected.getImageUri());
			assertThat(actual.getOriginalPrice()).isEqualTo(expected.getOriginalPrice());
			assertThat(actual.getDiscount()).isEqualTo(expected.getDiscount());
			assertThat(actual.getSalePrice()).isEqualTo(expected.getSalePrice());
		}

		System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
	}
}
