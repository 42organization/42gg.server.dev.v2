package gg.agenda.api.admin.agendaprofile.service;

import org.springframework.stereotype.Service;

import gg.admin.repo.agenda.AgendaProfileAdminRepository;
import gg.admin.repo.user.UserAdminRepository;
import gg.agenda.api.admin.agendaprofile.controller.request.AgendaProfileAdminReqDto;
import gg.data.agenda.AgendaProfile;
import gg.data.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaProfileAdminService {
	private final UserAdminRepository userAdminRepository;
	private final AgendaProfileAdminRepository agendaProfileAdminRepository;

	public void modifyAgendaProfile(String intraId, AgendaProfileAdminReqDto agendaProfileAdminReqDto) {
		User user = userAdminRepository.findByIntraId(intraId)
			.orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

		AgendaProfile agendaProfile = agendaProfileAdminRepository.findByUserId(user.getId())
			.orElseThrow(() -> new IllegalArgumentException("해당 사용자의 AgendaProfile이 존재하지 않습니다."));

		agendaProfile.modifyAgendaProfile(agendaProfileAdminReqDto);
	}
}
