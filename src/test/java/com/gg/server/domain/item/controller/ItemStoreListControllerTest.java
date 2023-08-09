package com.gg.server.domain.item.controller;

import com.gg.server.domain.item.data.ItemRepository;
import com.gg.server.domain.item.dto.ItemStoreListResponseDto;
import com.gg.server.domain.item.dto.ItemStoreResponseDto;
import com.gg.server.domain.item.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
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
                new ItemStoreResponseDto(1L, "itemName 1", "Content 1", "ImageUrl 1", 1000, 10, 900),
                new ItemStoreResponseDto(2L, "itemName 2", "Content 2", "ImageUrl 2", 2000, 20, 1800),
                new ItemStoreResponseDto(3L, "itemName 3", "Content 3", "ImageUrl 3", 3000, 30, 2700)
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
            assertThat(actual.getContent()).isEqualTo(expected.getContent());
            assertThat(actual.getImageUrl()).isEqualTo(expected.getImageUrl());
            assertThat(actual.getOriginalPrice()).isEqualTo(expected.getOriginalPrice());
            assertThat(actual.getDiscount()).isEqualTo(expected.getDiscount());
            assertThat(actual.getSalePrice()).isEqualTo(expected.getSalePrice());
        }

        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
    }
}
