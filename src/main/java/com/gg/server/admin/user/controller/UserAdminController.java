package com.gg.server.admin.user.controller;

import com.gg.server.admin.user.dto.UserSearchAdminResponseDto;
import com.gg.server.admin.user.service.UserAdminService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/pingpong/admin/users")
public class UserAdminController {

    private final UserAdminService userAdminService;

    @GetMapping
    public UserSearchAdminResponseDto userSearchAll(@RequestParam @Min(1) int page,
                                              @RequestParam(defaultValue = "20") int size,
                                              @RequestParam(required = false) String intraId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("intraId").ascending());
        if (intraId == null)
            return userAdminService.searchAll(pageable);
        else
            return userAdminService.findByPartsOfIntraId(intraId, pageable);
    }
}
