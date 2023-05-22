package com.gg.server.admin.user.controller;

import com.gg.server.admin.user.dto.UserDetailAdminResponseDto;
import com.gg.server.admin.user.dto.UserSearchAdminResponseDto;
import com.gg.server.admin.user.service.UserAdminService;
import com.gg.server.global.dto.PageRequestDto;
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
    public UserSearchAdminResponseDto userSearchAll(@ModelAttribute @Valid PageRequestDto pageRequestDto,
                                                    @RequestParam(required = false) String intraId) {
        Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1,
                pageRequestDto.getSize(),
                Sort.by("intraId").ascending());
        if (intraId == null)
            return userAdminService.searchAll(pageable);
        else
            return userAdminService.findByPartsOfIntraId(intraId, pageable);
    }

    @GetMapping("/{intraId}")
    public UserDetailAdminResponseDto userGetDetail(@PathVariable String intraId) {
        return userAdminService.getUserDetailByIntraId(intraId);
    }
}
