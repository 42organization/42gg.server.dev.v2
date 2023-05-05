package com.gg.server.domain.match.service;

import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.match.data.RedisMatchTime;
import com.gg.server.domain.match.data.RedisMatchUser;
import com.gg.server.domain.match.data.RedisMatchTimeRepository;
import com.gg.server.domain.match.data.RedisMatchUserRepository;
import com.gg.server.domain.match.dto.MatchStatusDto;
import com.gg.server.domain.match.dto.MatchStatusResponseListDto;
import com.gg.server.domain.match.type.MatchStatus;
import com.gg.server.domain.match.type.Option;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchRedisService {
    private final RedisMatchTimeRepository redisMatchTimeRepository;
    private final RedisMatchUserRepository redisMatchUserRepository;
    private final SlotManagementRepository slotManagementRepository;

    public Long countUserMatch(String nickName) {
        return redisMatchUserRepository.countMatchTime(nickName);
    }
    public Boolean isUserMatch(String nickName, LocalDateTime startTime) {
        RedisMatchTime matchTime = new RedisMatchTime(startTime);
        if (redisMatchUserRepository.getMatchUserOrder(nickName, matchTime) != null) {
            return true;
        }
        return false;
    }
    public void makeMatch(String nickname, Integer ppp, Option option, LocalDateTime startTime) {
        RedisMatchTime matchTime = new RedisMatchTime(startTime);
        RedisMatchUser matchUser = new RedisMatchUser(nickname, ppp, option);

        //유저 이미 큐에 등록 시 예외 처리
        if (redisMatchUserRepository.getMatchUserOrder(nickname, matchTime) != null) {
            return;
        }
        //3번 이상 매치 넣을 시 예외 처리
        if (redisMatchUserRepository.countMatchTime(nickname) >= 3) {
            return;
        }
        redisMatchTimeRepository.addMatchUser(startTime.toString(), matchUser);
        redisMatchTimeRepository.setMatchTimeWithExpiry(startTime);
        redisMatchUserRepository.addMatchTime(nickname, matchTime);
    }

    public void cancelMatch(String nickName, LocalDateTime startTime) {
        //취소 패널티는 게임이 만들어진 후 고려
        RedisMatchTime matchTime = new RedisMatchTime(startTime);
        redisMatchUserRepository.deleteMatchTime(nickName, matchTime);
        List<RedisMatchUser> allMatchUsers = redisMatchTimeRepository.getAllMatchUsers(startTime.toString());
        for (RedisMatchUser matchUser : allMatchUsers) {
            if (matchUser.getNickName().equals(nickName)) {
                redisMatchTimeRepository.deleteMatchUser(startTime.toString(), matchUser);
                break ;
            }
        }
    }


    public MatchStatusResponseListDto getAllMatchStatus(String nickName) {
//        SlotManagement slotManagement = slotManagementRepository.findFirstByOrderByCreatedAtDesc();
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime standardTime = LocalDateTime.of(
//                now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0);
//        LocalDateTime minTime = standardTime.minusHours(slotManagement.getPastSlotTime());
//        LocalDateTime maxTime = standardTime.plusHours(slotManagement.getFutureSlotTime());
////        LocalDateTime time = minTime;
//        Integer countPerHour = 60 / slotManagement.getGameInterval();
//        Integer pastSlotCount = (slotManagement.getPastSlotTime() * 60) / slotManagement.getGameInterval();
//        Integer fastSlotCount = (slotManagement.getFutureSlotTime() * 60) / slotManagement.getGameInterval();
//        //HashMap의 순서로 접근해보기?
//        Integer interval = slotManagement.getGameInterval();//60분에서 나눴을 때 나머지가 0
//
//        //어떻게 하면 효율적으로 할 수 있을까?
//        //hash map 생성
//        HashMap<LocalDateTime, MatchStatusDto> map = new LinkedHashMap<LocalDateTime, MatchStatusDto>();
//        //일단 user 등록 테이블을 담는다.
//        //이미 matching 된 슬롯인지 체크한다.
        //mytable slot 이 있는지 check 한다.
        //등록된 slot을 check 한다.
        //과거 슬롯인지,
        //과거시간
//        for (int i = 0; i < pastSlotCount ; i++) {
//            time = time.plusMinutes(slotManagement.getGameInterval());
//        }
//        List<List<MatchStatusDto>> matchStatusResponseListDto = new ArrayList<List<MatchStatusDto>>();
//        for (LocalDateTime time = minTime ; time.isBefore(maxTime) ; time.plusHours(1)) {
//            matchStatusResponseListDto.add(getListMatchStatusDto(time, now, interval));
//        }

        //현재시간

        //미래시간

//        LinkedHashMap<LocalDateTime, MatchStatusDto> map = new LinkedHashMap<LocalDateTime, MatchStatusDto>();
        //redisMatchTimeRepository.getAllEnrolledStartTimes();
//        for (LocalDateTime enrolledTime : redisMatchTimeRepository.getAllEnrolledStartTimes()) {
//
//        }
//        slotManagement.getFutureSlotTime();
//        slotManagement.getGameInterval();
//        slotManagement.getPastSlotTime();

        return null;
    }

//    private List<MatchStatusDto> getListMatchStatusDto(LocalDateTime start, LocalDateTime now, Integer interval){
//        List<MatchStatusDto> matchStatusDtoList = new ArrayList<MatchStatusDto>();
//        for (LocalDateTime time = start; time.isBefore(start.plusHours(1)); time.plusHours(interval)) {
//            if (time.isBefore(now) ) {
//                matchStatusDtoList.add(getPastMatchStatusDto(time, interval));
//                //매칭 됐는지 고려 또는 매칭
//            } else {
//
//            }
//        }
//    }

//    private MatchStatusDto getPastMatchStatusDto(LocalDateTime pastStartTime, Integer interval) {
//        return MatchStatusDto.builder().status(MatchStatus.CLOSE.getCode())
//                .startTime(pastStartTime)
//                .endTime(pastStartTime.plusMinutes(interval))
//                .normalCount(-1)
//                .rankCount(-1)
//                .build();
//    }

//    private MatchStatusDto getFutureMatchStatusDto(LocalDateTime futureStartTime) {
//        return MatchStatusDto.builder().status(MatchStatus.)
//    }
}
