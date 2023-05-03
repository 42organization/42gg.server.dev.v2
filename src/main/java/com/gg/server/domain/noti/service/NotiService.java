package com.gg.server.domain.noti.service;

import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.noti.dto.NotiDto;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotiService {
    private final NotiRepository notiRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<NotiDto> findNotiByUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElse(null); //.orElseThrow(() -> new BusinessException("E0001")); 에러코드!
        List<Noti> notiList = notiRepository.findAllByUserOrderByIdDesc(user);
        List<NotiDto> notiDtoList = notiList.stream().map(NotiDto::from).collect(Collectors.toList());
        return notiDtoList;
    }

    @Transactional
    public NotiDto findNotiByIdAndUser(UserDto userDto, Long notiId) {
        User user = userRepository.findById(userDto.getId()).orElse(null); //.orElseThrow(() -> new BusinessException("E0001")); 에러코드!
        Noti noti = notiRepository.findByIdAndUser(notiId, user);
        return NotiDto.from(noti);
    }

    @Transactional
    public void modifyNotiCheckedByUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElse(null); //.orElseThrow(() -> new BusinessException("E0001")); 에러코드!
        List<Noti> notis = notiRepository.findAllByUser(user);
        notis.forEach(noti -> {noti.modifyIsChecked(true);});
    }

    @Transactional
    public void removeNotiById(Long notiId) {
        notiRepository.deleteById(notiId);
    }

    @Transactional
    public void removeAllNotisByUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElse(null); //.orElseThrow(() -> new BusinessException("E0001")); 에러코드!
        notiRepository.deleteAllByUser(user);
    }
}
