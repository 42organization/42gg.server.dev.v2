package com.gg.server.domain.noti.controller;

import com.gg.server.domain.noti.dto.NotiDto;
import com.gg.server.domain.noti.dto.NotiResponseDto;
import com.gg.server.domain.noti.service.NotiService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.argumentresolver.Login;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/pingpong")
public class NotiController {
    private final NotiService notiService;

    @GetMapping(value = "/notifications")
    public NotiResponseDto notiFindByUser(@Login UserDto user) {
        List<NotiDto> notiDtos = notiService.findNotiByUser(user);
        return new NotiResponseDto(notiDtos);
    }

    @PutMapping(value = "/notifications/check")
    public ResponseEntity checkNotiByUser(@Login UserDto user) {
        notiService.modifyNotiCheckedByUser(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/notifications")
    public void notiRemoveAll(@Login UserDto user) {
        notiService.removeAllNotisByUser(user);
    }
}
