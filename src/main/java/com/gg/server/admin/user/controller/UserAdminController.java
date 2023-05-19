package com.gg.server.admin.user.controller;

import com.gg.server.admin.user.dto.UserSearchResponseAdminDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Size;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/pingpong/admin/users")
public class UserAdminController {

    @GetMapping
    public UserSearchResponseAdminDto searchUser(@RequestParam @Size(min=1, max=30) int page,
                                                 @RequestParam(defaultValue = "20") int size,
                                                 @RequestParam(required = false) String intraId) {

        return null;
    }
}
