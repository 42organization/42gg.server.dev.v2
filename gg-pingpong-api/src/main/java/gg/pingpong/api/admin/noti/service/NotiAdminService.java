package gg.pingpong.api.admin.noti.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.admin.repo.noti.NotiAdminRepository;
import gg.pingpong.admin.repo.user.UserAdminRepository;
import gg.pingpong.api.admin.noti.controller.request.SendNotiAdminRequestDto;
import gg.pingpong.api.admin.noti.controller.response.NotiListAdminResponseDto;
import gg.pingpong.api.admin.noti.dto.NotiAdminDto;
import gg.pingpong.api.user.noti.service.SnsNotiService;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.pingpong.data.noti.Noti;
import gg.pingpong.data.noti.type.NotiType;
import gg.pingpong.data.user.User;
import gg.pingpong.utils.exception.user.UserNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotiAdminService {
	private final NotiAdminRepository notiAdminRepository;
	private final UserAdminRepository userAdminRepository;
	private final SnsNotiService snsNotiService;

	/**
	 * 유저에게 알림을 전송합니다.
	 * @param sendNotiAdminRequestDto 알림 요청 Dto
	 * @exception UserNotFoundException 유저가 존재하지 않을 경우
	 */
	@Transactional
	public void sendAnnounceNotiToUser(SendNotiAdminRequestDto sendNotiAdminRequestDto) {
		String message = sendNotiAdminRequestDto.getMessage();
		String intraId = sendNotiAdminRequestDto.getIntraId();

		User user = userAdminRepository.findByIntraId(intraId)
			.orElseThrow(UserNotFoundException::new);
		Noti noti = notiAdminRepository.save(new Noti(user, NotiType.ANNOUNCE, message, false));
		snsNotiService.sendSnsNotification(noti, UserDto.from(user));
	}

	/**
	 * 전체 알림 목록을 조회합니다.
	 * @param pageable 알림 목록 페이지
	 * @return 알림 목록 응답 Dto
	 */
	@Transactional(readOnly = true)
	public NotiListAdminResponseDto getAllNoti(Pageable pageable) {
		Page<Noti> allNotiPage = notiAdminRepository.findAll(pageable);
		Page<NotiAdminDto> notiAdminDtoPage = allNotiPage.map(NotiAdminDto::new);
		return new NotiListAdminResponseDto(notiAdminDtoPage.getContent(), notiAdminDtoPage.getTotalPages());
	}

	/**
	 * 특정 유저의 알림 목록을 조회합니다.
	 * @param pageable 유저의 알림 목록 페이지
	 * @param intraId 인트라 Id
	 * @return 알림 목록 응답 Dto
	 */
	@Transactional(readOnly = true)
	public NotiListAdminResponseDto getFilteredNotifications(Pageable pageable, String intraId) {
		Page<Noti> findNotis = notiAdminRepository.findNotisByUserIntraId(pageable, intraId);
		Page<NotiAdminDto> notiResponseDtoPage = findNotis.map(NotiAdminDto::new);
		return new NotiListAdminResponseDto(notiResponseDtoPage.getContent(), notiResponseDtoPage.getTotalPages());
	}
}
