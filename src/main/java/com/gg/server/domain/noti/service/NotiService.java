package com.gg.server.domain.noti.service;

import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.noti.dto.NotiDto;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new UsernameNotFoundException("User" + userDto.getId()));
        List<Noti> notiList = notiRepository.findAllByUserOrderByIdDesc(user);
        List<NotiDto> notiDtoList = notiList.stream().map(NotiDto::from).collect(Collectors.toList());
        return notiDtoList;
    }

    @Transactional
    public NotiDto findNotiByIdAndUser(UserDto userDto, Long notiId) {
        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new UsernameNotFoundException("User" + userDto.getId()));
        Noti noti = notiRepository.findByIdAndUser(notiId, user).orElseThrow(() -> new NotExistException("요청한 알림을 찾을 수 없습니다.", ErrorCode.NOT_FOUND));
        return NotiDto.from(noti);
    }

    @Transactional
    public void modifyNotiCheckedByUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new UsernameNotFoundException("User" + userDto.getId()));
        List<Noti> notis = notiRepository.findAllByUser(user);
        notis.forEach(noti -> {noti.modifyIsChecked(true);});
    }

    @Transactional
    public void removeNotiById(Long notiId) {
        notiRepository.deleteById(notiId);
    }

    @Transactional
    public void removeAllNotisByUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new UsernameNotFoundException("User" + userDto.getId()));
        notiRepository.deleteAllByUser(user);
    }

    public String getMessage(Noti noti) {
        String message;
        if (noti.getType() != NotiType.ANNOUNCE) {
            message = "🧚: \"새로운 알림이 도착했핑.\"\n" + "🧚: \"" + noti.getType().getMessage() + "\"\n\n 🏓42GG와 함께하는 행복한 탁구생활🏓" +
                    "\n$$지금 즉시 접속$$ ----> https://42gg.kr";
        } else {
            message = "🧚: \"새로운 알림이 도착했핑.\"\n" + "🧚: \"" + noti.getType().getMessage() + "\"\n\n공지사항: "
                    + noti.getMessage() + "\n\n 🏓42GG와 함께하는 행복한 탁구생활🏓" + "\n$$지금 즉시 접속$$ ----> https://42gg.kr";
        }
        return message;
    }
}
