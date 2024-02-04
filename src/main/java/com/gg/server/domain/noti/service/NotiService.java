package com.gg.server.domain.noti.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.noti.dto.NotiDto;
import com.gg.server.domain.noti.dto.NotiResponseDto;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotiService {
	private final NotiRepository notiRepository;
	private final UserRepository userRepository;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

	/**
	 * UserDto를 이용하여 User의 알림 목록을 조회합니다.
	 * @param userDto 유저 Dto
	 * @exception UserNotFoundException 유저가 존재하지 않을 경우
	 * @return 알림 목록
	 */
	@Transactional(readOnly = true)
	public List<NotiResponseDto> findNotiByUser(UserDto userDto) {
		User user = userRepository.findById(userDto.getId()).orElseThrow(UserNotFoundException::new);
		List<Noti> notiList = notiRepository.findAllByUserOrderByIdDesc(user);
		List<NotiResponseDto> notiResponseDtoList = notiList.stream()
			.map(NotiResponseDto::from)
			.collect(Collectors.toList());
		return notiResponseDtoList;
	}

	/**
	 * UserDto와 notiId를 이용하여 User의 알림 목록을 조회합니다.
	 * @param userDto 유저 Dto
	 * @param notiId  알림 Id
	 * @exception UserNotFoundException 유저가 존재하지 않을 경우
	 * @return 알림 목록
	 */
	@Transactional
	public NotiDto findNotiByIdAndUser(UserDto userDto, Long notiId) {
		User user = userRepository.findById(userDto.getId()).orElseThrow(UserNotFoundException::new);
		Noti noti = notiRepository.findByIdAndUser(notiId, user)
			.orElseThrow(() -> new NotExistException("요청한 알림을 찾을 수 없습니다.", ErrorCode.NOT_FOUND));
		return NotiDto.from(noti);
	}

	/**
	 * 알림을 읽음 처리합니다.
	 * @param userDto 유저 Dto
	 * @exception UserNotFoundException 유저가 존재하지 않을 경우
	 */
	@Transactional
	public void modifyNotiCheckedByUser(UserDto userDto) {
		User user = userRepository.findById(userDto.getId()).orElseThrow(UserNotFoundException::new);
		List<Noti> notis = notiRepository.findAllByUser(user);
		notis.forEach(noti -> {
			noti.modifyIsChecked(true);
		});
	}

	/**
	 * 알림을 제거합니다.
	 * @param notiId 알림 Id
	 */
	@Transactional
	public void removeNotiById(Long notiId) {
		notiRepository.deleteById(notiId);
	}

	/**
	 * 알림 목록 전체를 제거합니다.
	 * @param userDto 유저 Dto
	 * @exception UserNotFoundException 유저가 존재하지 않을 경우
	 */
	@Transactional
	public void removeAllNotisByUser(UserDto userDto) {
		User user = userRepository.findById(userDto.getId()).orElseThrow(UserNotFoundException::new);
		notiRepository.deleteAllByUser(user);
	}

	/**
	 * 매칭 성사 알림을 생성합니다.
	 * @param user 유저
	 * @param startTime 경기 시작 시간
	 * @return 매칭 성사 알림
	 */
	@Transactional
	public Noti createMatched(User user, LocalDateTime startTime) {
		String notiMessage = startTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "에 신청한 매칭이 성사되었습니다.";
		Noti noti = new Noti(user, NotiType.MATCHED, notiMessage, false);
		notiRepository.save(noti);
		return noti;
	}

	/**
	 * 매칭 취소 알림을 생성합니다.
	 * @param user 유저
	 * @param startTime 경기 시작 시간
	 * @return 매칭 취소 알림
	 */
	@Transactional
	public Noti createMatchCancel(User user, LocalDateTime startTime) {
		String notiMessage = startTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "에 신청한 매칭이 상대에 의해 취소되었습니다.";
		Noti noti = new Noti(user, NotiType.CANCELEDBYMAN, notiMessage, false);
		notiRepository.save(noti);
		return noti;
	}

	/**
	 * 아이템 선물 알림을 생성합니다.
	 * @param ownerUser 선물 받은 유저
	 * @param payUser 선물한 유저
	 * @param itemName 아이템 이름
	 * @return 아이템 선물 알림
	 */
	@Transactional
	public Noti createGiftNoti(User ownerUser, User payUser, String itemName) {
		String notiMessage = "ଘ(੭ˊᵕˋ)੭* ੈ✩ " + payUser.getIntraId() + "님에게 " + itemName + " 아이템을 선물받았어요!";
		Noti noti = new Noti(ownerUser, NotiType.GIFT, notiMessage, false);
		notiRepository.save(noti);
		return noti;
	}

	/**
	 * 경기 임박 알림을 생성합니다.
	 * @param user 유저
	 * @param enemyIntra 상대팀 인트라 아이디
	 * @param notiType 알림 타입
	 * @param gameOpenMinute 게임 시간
	 * @return 경기 임박 알림
	 */
	public Noti createImminentNoti(User user, String enemyIntra, NotiType notiType, Integer gameOpenMinute) {
		String msg = "<intraId::" + enemyIntra + ">님과 경기 " + gameOpenMinute + "분 전 입니다. 서두르세요!";
		return notiRepository.save(new Noti(user, notiType, msg, false));
	}

	/**
	 * 알림 메시지를 가져옵니다.
	 * @param noti 알림
	 * @return 알림 메시지
	 */
	public String getMessage(Noti noti) {
		String message;
		if (noti.getType() != NotiType.ANNOUNCE) {
			message =
				"🧚: \"새로운 알림이 도착했핑.\"\n" + "🧚: \"" + noti.getType().getMessage() + "\"\n\n 🏓42GG와 함께하는 행복한 탁구생활🏓"
					+ "\n$$지금 즉시 접속$$ ----> https://42gg.kr";
		} else {
			message = "🧚: \"새로운 알림이 도착했핑.\"\n" + "🧚: \"" + noti.getType().getMessage() + "\"\n\n공지사항: "
				+ noti.getMessage() + "\n\n 🏓42GG와 함께하는 행복한 탁구생활🏓" + "\n$$지금 즉시 접속$$ ----> https://42gg.kr";
		}
		return message;
	}
}
