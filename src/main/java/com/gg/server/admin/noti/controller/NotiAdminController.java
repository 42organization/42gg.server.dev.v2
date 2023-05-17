package com.gg.server.admin.noti.controller;


import com.gg.server.admin.noti.dto.SendNotiAdminRequestDto;
import com.gg.server.admin.noti.service.NotiAdminService;
import com.gg.server.domain.noti.dto.NotiResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/pingpong/admin/notifications")
public class NotiAdminController {

    private final NotiAdminService notiAdminService;

    @GetMapping
    public ResponseEntity<NotiResponseDto> getAllNoti(@RequestParam @Size(min=1, max=30) int page,
                                                             @RequestParam(defaultValue = "20") int size,
                                                             @RequestParam(required = false) String q) {
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by("createdAt").descending().and(Sort.by("user.intraId").ascending()));
        if (q == null)
            return new ResponseEntity(notiAdminService.getAllNoti(pageable), HttpStatus.OK);
        else
            return new ResponseEntity(notiAdminService.getFilteredNotifications(pageable, q), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity sendNotiToUser(@RequestBody SendNotiAdminRequestDto sendNotiAdminRequestDto) {
        notiAdminService.sendAnnounceNotiToUser(sendNotiAdminRequestDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
