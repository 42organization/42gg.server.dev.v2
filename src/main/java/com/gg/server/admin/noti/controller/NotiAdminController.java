package com.gg.server.admin.noti.controller;


import com.gg.server.admin.noti.dto.NotiListAdminRequestDto;
import com.gg.server.admin.noti.dto.NotiListAdminResponseDto;
import com.gg.server.admin.noti.dto.SendNotiAdminRequestDto;
import com.gg.server.admin.noti.service.NotiAdminService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/pingpong/admin/notifications")
public class NotiAdminController {

    private final NotiAdminService notiAdminService;

    @GetMapping
    public NotiListAdminResponseDto getAllNoti(@ModelAttribute NotiListAdminRequestDto requestDto) {
        int page = requestDto.getPage();
        int size = requestDto.getSize();
        String intraId = requestDto.getIntraId();

        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by("createdAt").descending().and(Sort.by("user.intraId").ascending()));
        if (intraId == null)
            return notiAdminService.getAllNoti(pageable);
        else
            return notiAdminService.getFilteredNotifications(pageable, intraId);
    }
    @PostMapping
    public ResponseEntity sendNotiToUser(@RequestBody SendNotiAdminRequestDto sendNotiAdminRequestDto) {
        notiAdminService.sendAnnounceNotiToUser(sendNotiAdminRequestDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
