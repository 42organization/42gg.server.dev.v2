package com.gg.server.admin.user.controller;

import com.gg.server.admin.user.dto.UserDetailAdminResponseDto;
import com.gg.server.admin.user.dto.UserSearchAdminRequestDto;
import com.gg.server.admin.user.dto.UserSearchAdminResponseDto;
import com.gg.server.admin.user.dto.UserUpdateAdminRequestDto;
import com.gg.server.admin.user.service.UserAdminService;
import com.gg.server.domain.rank.exception.RankUpdateException;
import com.gg.server.domain.user.exception.UserImageLargeException;
import com.gg.server.domain.user.exception.UserImageTypeException;
import com.gg.server.global.dto.PageRequestDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/pingpong/admin/users")
@Slf4j
public class UserAdminController {

    private final UserAdminService userAdminService;

    @GetMapping
    public UserSearchAdminResponseDto userSearchAll(@ModelAttribute @Valid UserSearchAdminRequestDto searchRequestDto) {
        Pageable pageable = PageRequest.of(searchRequestDto.getPage() - 1,
                searchRequestDto.getSize(),
                Sort.by("intraId").ascending());
        if (searchRequestDto.getUserFilter() != null)
            return userAdminService.searchByIntraId(pageable, searchRequestDto.getUserFilter());
        else if (searchRequestDto.getIntraId() != null)
            return userAdminService.findByPartsOfIntraId(searchRequestDto.getIntraId(), pageable);
        else
            return userAdminService.searchAll(pageable);
    }

    @GetMapping("/{intraId}")
    public UserDetailAdminResponseDto userGetDetail(@PathVariable String intraId) {
        return userAdminService.getUserDetailByIntraId(intraId);
    }

    @PutMapping("/{intraId}")
    public ResponseEntity userUpdateDetail(@PathVariable String intraId,
                                           @RequestPart UserUpdateAdminRequestDto updateRequestDto,
                                           @RequestPart(required = false) MultipartFile multipartFile) throws IOException {
        if (multipartFile != null) {
            if (multipartFile.getSize() > 50000) {
                throw new UserImageLargeException();
            } else if (multipartFile.getContentType() == null || !multipartFile.getContentType().equals("image/jpeg")) {
                throw new UserImageTypeException();
            }
        }
        userAdminService.updateUserDetail(intraId, updateRequestDto, multipartFile);

        return new ResponseEntity(HttpStatus.OK);
    }
}
