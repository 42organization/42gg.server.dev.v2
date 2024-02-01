package com.gg.server.admin.noti.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.admin.noti.data.NotiAdminRepository;
import com.gg.server.admin.noti.dto.NotiAdminDto;
import com.gg.server.admin.noti.dto.NotiListAdminResponseDto;
import com.gg.server.admin.noti.dto.SendNotiAdminRequestDto;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.data.noti.Noti;
import com.gg.server.data.noti.type.NotiType;
import com.gg.server.data.user.User;
import com.gg.server.domain.noti.service.SnsNotiService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotiAdminService {
	private final NotiAdminRepository notiAdminRepository;
	private final UserAdminRepository userAdminRepository;
	private final SnsNotiService snsNotiService;

	@Transactional
	public void sendAnnounceNotiToUser(SendNotiAdminRequestDto sendNotiAdminRequestDto) {
		String message = sendNotiAdminRequestDto.getMessage();
		String intraId = sendNotiAdminRequestDto.getIntraId();

		User user = userAdminRepository.findByIntraId(intraId)
			.orElseThrow(UserNotFoundException::new);
		Noti noti = notiAdminRepository.save(new Noti(user, NotiType.ANNOUNCE, message, false));
		snsNotiService.sendSnsNotification(noti, UserDto.from(user));
	}

	@Transactional(readOnly = true)
	public NotiListAdminResponseDto getAllNoti(Pageable pageable) {
		Page<Noti> allNotiPage = notiAdminRepository.findAll(pageable);
		Page<NotiAdminDto> notiAdminDtoPage = allNotiPage.map(NotiAdminDto::new);
		return new NotiListAdminResponseDto(notiAdminDtoPage.getContent(), notiAdminDtoPage.getTotalPages());
	}

	@Transactional(readOnly = true)
	public NotiListAdminResponseDto getFilteredNotifications(Pageable pageable, String intraId) {
		Page<Noti> findNotis = notiAdminRepository.findNotisByUserIntraId(pageable, intraId);
		Page<NotiAdminDto> notiResponseDtoPage = findNotis.map(NotiAdminDto::new);
		return new NotiListAdminResponseDto(notiResponseDtoPage.getContent(), notiResponseDtoPage.getTotalPages());
	}
}
