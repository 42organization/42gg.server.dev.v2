//package com.gg.server.domain.item.controller;
//
//import org.springframework.boot.test.context.SpringBootTest;
//
////import javax.transaction.Transactional;
////
////@SpringBootTest
////@Transactional
////public class ItemStoreListControllerTest {
////}
//
//
//import com.gg.server.domain.item.dto.ItemStoreListReponseDto;
//import com.gg.server.domain.item.service.ItemService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import static org.assertj.core.api.Assertions.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.mockito.BDDMockito.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class ItemStoreControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ItemService itemService;
//
//    @Test
//    public void getAllItemsTest() throws Exception {
//        // given
//        ItemStoreListReponseDto itemDto = new ItemStoreListReponseDto(1L, "testItem", "testContent", "testImageUri", 100, 10, 90);
//        List<ItemStoreListReponseDto> items = Arrays.asList(itemDto);
//        given(itemService.getAllItems()).willReturn(items);
//
//        // when
//        String result = this.mockMvc.perform(get("/pingpong/items/store"))
//                .andDo(print())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        // then
//        assertThat(result).isEqualTo("[{\"itemId\":1,\"itemName\":\"testItem\",\"content\":\"testContent\",\"imageUrl\":\"testImageUri\",\"originalPrice\":100,\"discount\":10,\"salePrice\":90}]");
//    }
//}
//
//
//
//
///////////////////////////////////////////////////////////////////////
////
////        import com.fasterxml.jackson.databind.ObjectMapper;
////        import com.gg.server.admin.item.data.ItemAdminRepository;
////        import com.gg.server.admin.item.dto.ItemListResponseDto;
////        import com.gg.server.admin.item.service.ItemAdminService;
////        import com.gg.server.domain.user.data.UserRepository;
////        import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
////        import com.gg.server.utils.TestDataUtils;
////        import org.apache.http.HttpHeaders;
////        import org.junit.jupiter.api.DisplayName;
////        import org.junit.jupiter.api.Test;
////        import org.springframework.beans.factory.annotation.Autowired;
////        import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
////        import org.springframework.boot.test.context.SpringBootTest;
////        import org.springframework.data.domain.PageRequest;
////        import org.springframework.data.domain.Pageable;
////        import org.springframework.data.domain.Sort;
////        import org.springframework.test.web.servlet.MockMvc;
////        import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
////
////
////        import javax.transaction.Transactional;
////
////        import static org.assertj.core.api.Assertions.assertThat;
////        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
////
////@SpringBootTest
////@AutoConfigureMockMvc
////@Transactional
////class ItemAdminControllerTest {
////    @Autowired
////    ItemAdminService itemAdminService;
////    @Autowired
////    UserRepository userRepository;
////    @Autowired
////    TestDataUtils testDataUtils;
////    @Autowired
////    ObjectMapper objectMapper;
////    @Autowired
////    AuthTokenProvider tokenProvider;
////    @Autowired
////    ItemAdminRepository itemAdminRepository;
////    @Autowired
////    MockMvc mockMvc;
////
////    @Test
////    @DisplayName("GET /pingpong/admin/items/history")
////    public void getAllItemHistoryTest() throws Exception {
////        String accessToken = testDataUtils.getAdminLoginAccessToken();
////        Integer page = 1;
////        Integer size = 20;
////        String url = "/pingpong/admin/items/history?page=" + page + "&size=" + size;
////        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
////
////        String contentAsString = mockMvc.perform(get(url)
////                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
////                .andExpect(status().isOk())
////                .andReturn().getResponse().getContentAsString();
////
////        System.out.println(contentAsString);
////        ItemListResponseDto expect = itemAdminService.getAllItemHistory(pageable);
////        ItemListResponseDto result = objectMapper.readValue(contentAsString, ItemListResponseDto.class);
////        System.out.println(expect.getHistoryList());
////        System.out.println(result.getHistoryList());
////        assertThat(result.getHistoryList().get(0).getItemId());
////        assertThat(result.getHistoryList().get(0).getName());
////        assertThat(result.getHistoryList().get(0).getContent());
////        assertThat(result.getHistoryList().get(0).getPrice());
////    }
////}
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
//import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.web.bind.annotation.GetMapping;
import static org.assertj.core.api.Assertions.assertThat;
import javax.transaction.Transactional;

//import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
//import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

//import java.util.Arrays;
//import java.util.List;

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.mockito.BDDMockito.*;
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

        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
        System.out.println(userId);


        List<ItemStoreResponseDto> testItems = Arrays.asList(
                new ItemStoreResponseDto(1L, "Expected Name 1", "Expected Content 1", "Expected ImageUrl 1", 1000, 10, 900),
                new ItemStoreResponseDto(2L, "Expected Name 2", "Expected Content 2", "Expected ImageUrl 2", 2000, 20, 1800),
                new ItemStoreResponseDto(3L, "Expected Name 3", "Expected Content 3", "Expected ImageUrl 3", 3000, 30, 2700)
        );

        ItemStoreListResponseDto testResponse = new ItemStoreListResponseDto(testItems);
        when(itemService.getAllItems()).thenReturn(testResponse);

        String url = "/pingpong/items/store";
        String contentAsString = mockMvc.perform(get(url)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ItemStoreListResponseDto result = objectMapper.readValue(contentAsString, ItemStoreListResponseDto.class);
        //List<ItemStoreReponseDto> result = objectMapper.readValue(contentAsString, new TypeReference<List<ItemStoreReponseDto>>() {
        //        }
        // ItemStoreListReponseDto result = objectMapper.readValue(contentAsString, ItemStoreListReponseDto);

//        for (ItemStoreResponseDto item : result.getItemStoreReponseDtos()) {
//            System.out.println(item);
//        }


        assertThat(result.getItems()).isNotNull();

//        for (ItemStoreResponseDto item : result.getItems()) {  // 수정된 부분
//            System.out.println(item);
//        }


        assertThat(result.getItems()).hasSize(testItems.size());
        for (int i = 0; i < result.getItems().size(); i++) {
            ItemStoreResponseDto actual = result.getItems().get(i);
            ItemStoreResponseDto expected = testItems.get(i);

            assertThat(actual.getItemId()).isEqualTo(expected.getItemId());
            assertThat(actual.getItemName()).isEqualTo(expected.getItemName());
            assertThat(actual.getContent()).isEqualTo(expected.getContent());
//            assertThat(actual.getImageUrl()).isEqualTo(expected.getImageUrl());
            assertThat(actual.getOriginalPrice()).isEqualTo(expected.getOriginalPrice());
            assertThat(actual.getDiscount()).isEqualTo(expected.getDiscount());
            assertThat(actual.getSalePrice()).isEqualTo(expected.getSalePrice());
        }

        System.out.println(objectMapper.writeValueAsString(result));
    }
}

//        assertThat(result.size()).isBetween(0, 5);
//
//        ItemStoreListReponseDto first = result.get(0);
//        assertThat(first.getItemId()).isEqualTo(expectedItemId);
//        assertThat(first.getItemName()).isEqualTo(expectedItemName);
//        assertThat(first.getContent()).isEqualTo(expectedContent);
//        assertThat(first.getImageUrl()).isEqualTo(expectedImageUrl);
//        assertThat(first.getOriginalPrice()).isEqualTo(expectedOriginalPrice);
//        assertThat(first.getDiscount()).isEqualTo(expectedDiscount);
//        assertThat(first.getSalePrice()).isEqualTo(expectedSalePrice);
//
//
//        for (ItemStoreListReponseDto item : result) {
//            assertThat(item.getItemId()).isNotNull();
//            assertThat(item.getItemName()).isNotEmpty();
//            assertThat(item.getContent()).isNotEmpty();
//            assertThat(item.getImageUrl()).isNotEmpty();
//            assertThat(item.getOriginalPrice()).isGreaterThan(0);
//            assertThat(item.getDiscount()).isBetween(0, 100);
//            assertThat(item.getSalePrice()).isLessThanOrEqualTo(item.getOriginalPrice());
//        }

//        JavaType = List<ItemStoreListReponseDto>;
//        List<ItemStoreListReponseDto> result = objectMapper.readValue(contentAsString, JavaType);
//        for (ItemStoreListReponseDto item : result) {
//            System.out.println(item);
//        }
//        List<Student> studentList
//                = objectMapper.readValue(jsonArrStr, new TypeReference<List<Student>>() {})
//        assertThat(result.size()).isEqualTo(1);
//        assertThat(result.get(0).getItemId()).isEqualTo(itemDto.getItemId());
//        assertThat(result.get(0).getItemName()).isEqualTo(itemDto.getItemName());

        //ItemStoreListReponseDto
//    }
//        // given
//        ItemStoreListReponseDto itemDto = new ItemStoreListReponseDto(1L, "testItem", "testContent", "testImageUri", 100, 10, 90);
//        List<ItemStoreListReponseDto> items = Arrays.asList(itemDto);
//        given(itemService.getAllItems()).willReturn(items);
//
//        // when
//        String result = this.mockMvc.perform(get("/pingpong/items/store"))
//                .andDo(print())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        // then
//        assertThat(result).isEqualTo("[{\"itemId\":1,\"itemName\":\"testItem\",\"content\":\"testContent\",\"imageUrl\":\"testImageUri\",\"originalPrice\":100,\"discount\":10,\"salePrice\":90}]");
//    }

