package com.gg.server.domain.noti.service;

import com.gg.server.domain.noti.Noti;
import com.gg.server.domain.noti.NotiRepository;
import com.gg.server.domain.noti.dto.NotiDto;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotiService {
    private final NotiRepository notiRepository;

    private final UserRepository userRepository;
    public List<NotiDto> findNotCheckedNotiByUser(UserDto userDto)
    {
        User user = userRepository.findById(userDto.getId()).orElse(null); // 에러코드!
        List<Noti> notis = notiRepository.findByUserAndIsCheckedFalse(user);

        List<NotiDto> notiDtos = notis.stream()
                .map(noti -> {
                    NotiDto notiDto = NotiDto.from(noti);
                    return notiDto;
                })
                .collect(Collectors.toList());
        return notiDtos;
    }
}
