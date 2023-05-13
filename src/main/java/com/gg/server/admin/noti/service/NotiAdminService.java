package com.gg.server.admin.noti.service;


import com.gg.server.admin.noti.data.NotiAdminRepository;
import com.gg.server.admin.noti.dto.SendNotiAdminRequestDto;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.user.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class NotiAdminService {
    private final NotiAdminRepository notiAdminRepository;
    private final UserAdminRepository userAdminRepository;

    @Transactional
    public void sendAnnounceNotiToUser(SendNotiAdminRequestDto sendNotiAdminRequestDto) {
        String intraId = sendNotiAdminRequestDto.getIntraId();
        String message = sendNotiAdminRequestDto.getMessage();

        User user = userAdminRepository.findByIntraId(intraId)
                .orElseThrow(() -> new UsernameNotFoundException("User " + intraId));
        notiAdminRepository.save(new Noti(user, NotiType.ANNOUNCE, message, false));
    }
}
