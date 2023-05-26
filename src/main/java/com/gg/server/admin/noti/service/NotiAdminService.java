package com.gg.server.admin.noti.service;


import com.gg.server.admin.noti.data.NotiAdminRepository;
import com.gg.server.admin.noti.dto.NotiAdminDto;
import com.gg.server.admin.noti.dto.NotiListAdminResponseDto;
import com.gg.server.admin.noti.dto.SendNotiAdminRequestDto;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class NotiAdminService {
    private final NotiAdminRepository notiAdminRepository;
    private final UserAdminRepository userAdminRepository;

    @Transactional
    public void sendAnnounceNotiToUser(SendNotiAdminRequestDto sendNotiAdminRequestDto) {
        String message = sendNotiAdminRequestDto.getMessage();
        String intraId = sendNotiAdminRequestDto.getIntraId();

        User user = userAdminRepository.findByIntraId(intraId)
                .orElseThrow(() -> new UserNotFoundException());
        notiAdminRepository.save(new Noti(user, NotiType.ANNOUNCE, message, false));
    }

    @Transactional(readOnly = true)
    public NotiListAdminResponseDto getAllNoti(Pageable pageable) {
        Page<Noti> allNotiPage = notiAdminRepository.findAll(pageable);
        Page<NotiAdminDto> notiAdminDtoPage = allNotiPage.map(NotiAdminDto::new);
        return new NotiListAdminResponseDto(notiAdminDtoPage.getContent(), notiAdminDtoPage.getTotalPages(),
                notiAdminDtoPage.getNumber() + 1);
    }

    @Transactional(readOnly = true)
    public NotiListAdminResponseDto getFilteredNotifications(Pageable pageable, String intraId) {
        Page<Noti> findNotis = notiAdminRepository.findNotisByUserIntraId(pageable, intraId);
        Page<NotiAdminDto> notiResponseDtoPage = findNotis.map(NotiAdminDto::new);
        return new NotiListAdminResponseDto(
                notiResponseDtoPage.getContent(), notiResponseDtoPage.getTotalPages(),
                notiResponseDtoPage.getNumber() + 1
        );
    }
}
