package com.gg.server.admin.user.controller;

import com.gg.server.admin.user.dto.UserDetailAdminResponseDto;
import com.gg.server.admin.user.dto.UserSearchAdminRequestDto;
import com.gg.server.admin.user.dto.UserSearchAdminResponseDto;
import com.gg.server.admin.user.service.UserAdminService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/pingpong/admin/users")
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
}
