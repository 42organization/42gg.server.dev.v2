package com.gg.server.admin.noti.controller;


import com.gg.server.admin.noti.dto.SendNotiAdminRequestDto;
import com.gg.server.admin.noti.service.NotiAdminService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/pingpong/admin/notifications")
public class NotiAdminController {

    private final NotiAdminService notiAdminService;
    @PostMapping("/{intraId}")
    public ResponseEntity sendNotiToUser(@PathVariable String intraId, @RequestBody SendNotiAdminRequestDto sendNotiAdminRequestDto) {
        notiAdminService.sendAnnounceNotiToUser(sendNotiAdminRequestDto, intraId);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
