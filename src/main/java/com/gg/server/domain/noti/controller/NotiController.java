package com.gg.server.domain.noti.controller;

import com.gg.server.domain.noti.dto.NotiDto;
import com.gg.server.domain.noti.dto.NotiResponseDto;
import com.gg.server.domain.noti.service.NotiService;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.argumentresolver.Login;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/pingpong")
public class NotiController {
    private final NotiService notiService;

    @GetMapping(value = "/notifications")
    public NotiResponseDto notiFindByUser(@Login User user) {
        List<NotiDto> notiDtos = notiService.findNotCheckedNotiByUser(UserDto.from(user));
        return new NotiResponseDto(notiDtos);
    }

//    @Override
//    @DeleteMapping(value = "/notifications/{notiId}")
//    public void notiRemoveOne(Integer notiId, HttpServletRequest request) {
//        UserDto user = tokenService.findUserByAccessToken(HeaderUtil.getAccessToken(request));
//        notiService.findNotiByIdAndUser(NotiFindDto.builder().notiId(notiId).user(user).build());
//        NotiDeleteDto deleteDto = NotiDeleteDto.builder()
//                .notiId(notiId)
//                .build();
//        notiService.removeNotiById(deleteDto);
//    }
//
//    @Override
//    @DeleteMapping(value = "/notifications")
//    public void notiRemoveAll(HttpServletRequest request) {
//        UserDto user = tokenService.findUserByAccessToken(HeaderUtil.getAccessToken(request));
//        NotiDeleteDto deleteDto = NotiDeleteDto.builder()
//                        .user(user)
//                        .build();
//        notiService.removeAllNotisByUser(deleteDto);
//    }
}
