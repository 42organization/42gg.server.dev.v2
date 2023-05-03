package com.gg.server.domain.noti.controller;

import com.gg.server.domain.noti.dto.NotiDto;
import com.gg.server.domain.noti.dto.NotiResponseDto;
import com.gg.server.domain.noti.service.NotiService;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.argumentresolver.Login;
import lombok.AllArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/pingpong")
public class NotiController {
    private final NotiService notiService;

    @GetMapping(value = "/notifications")
    public NotiResponseDto notiFindByUser(@Login User user) {
        List<NotiDto> notiDtos = notiService.findNotiByUser(UserDto.from(user));
        return new NotiResponseDto(notiDtos);
    }

    @PutMapping(value = "/notifications/check")
    public void checkNotiByUser(@Login User user) {
        notiService.modifyNotiCheckedByUser(UserDto.from(user));
    }

    @DeleteMapping(value = "/notifications")
    public void notiRemoveAll(@Login User user) {
        notiService.removeAllNotisByUser(UserDto.from(user));
    }
}
