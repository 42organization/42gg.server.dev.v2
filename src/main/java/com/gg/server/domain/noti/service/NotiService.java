package com.gg.server.domain.noti.service;

import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.noti.dto.NotiDto;
import com.gg.server.domain.noti.dto.NotiResponseDto;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotiService {
    private final NotiRepository notiRepository;
    private final UserRepository userRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @Transactional(readOnly = true)
    public List<NotiResponseDto> findNotiByUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new UsernameNotFoundException("User" + userDto.getId()));
        List<Noti> notiList = notiRepository.findAllByUserOrderByIdDesc(user);
        List<NotiResponseDto> notiResponseDtoList = notiList.stream().map(NotiResponseDto::from).collect(Collectors.toList());
        return notiResponseDtoList;
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

    @Transactional
    public Noti createMatched(User user, LocalDateTime startTime) {
        String notiMessage = startTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "에 신청한 매칭이 성사되었습니다.";
        Noti noti = new Noti(user, NotiType.MATCHED, notiMessage, false);
        notiRepository.save(noti);
        return noti;
    }

    @Transactional
    public Noti createMatchCancel(User user, LocalDateTime startTime) {
        String notiMessage = startTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "에 신청한 매칭이 상대에 의해 취소되었습니다.";
        Noti noti = new Noti(user, NotiType.CANCELEDBYMAN, notiMessage, false);
        notiRepository.save(noti);
        return noti;
    }

    @Transactional
    public Noti createGiftNoti(User ownerUser, User payUser, String itemName) {
        String notiMessage = "ଘ(੭ˊᵕˋ)੭* ੈ✩ " + payUser.getIntraId() + "님에게 " + itemName + " 아이템을 선물받았어요!";
        Noti noti = new Noti(ownerUser, NotiType.GIFT, notiMessage, false);
        notiRepository.save(noti);
        return noti;
    }

    public Noti createImminentNoti(User user, String enemyIntra, NotiType notiType, Integer gameOpenMinute) {
        String msg = "<intraId::" + enemyIntra + ">님과 경기 " + gameOpenMinute + "분 전 입니다. 서두르세요!";
        return notiRepository.save(new Noti(user, notiType, msg, false));
    }

    public String getMessage(Noti noti) {
        String message;
        if (noti.getType() != NotiType.ANNOUNCE) {
            message = notiMsg(noti.getType());
        } else {
            message = "🧚: \"새로운 알림이 도착했핑.\"\n" + "🧚: \"" + noti.getType().getMessage() + "\"\n\n공지사항: "
                    + noti.getMessage() + "\n\n 🏓42GG와 함께하는 행복한 탁구생활🏓" + "\n$$지금 즉시 접속$$ ----> https://42gg.kr";
        }
        return message;
    }
    public String notiMsg(NotiType notiType) {
        return "🧚: \"새로운 알림이 도착했핑.\"\n" + "🧚: \"" + notiType.getMessage() + "\"\n\n 🏓42GG와 함께하는 행복한 탁구생활🏓" +
                "\n$$지금 즉시 접속$$ ----> https://42gg.kr";
    }
}
