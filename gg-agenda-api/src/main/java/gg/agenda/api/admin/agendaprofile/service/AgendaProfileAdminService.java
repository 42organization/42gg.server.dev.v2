package gg.agenda.api.admin.agendaprofile.service;

import static gg.utils.exception.ErrorCode.*;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import gg.admin.repo.agenda.AgendaProfileAdminRepository;
import gg.agenda.api.admin.agendaprofile.controller.request.AgendaProfileChangeAdminReqDto;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.type.Location;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaProfileAdminService {
	private final AgendaProfileAdminRepository agendaProfileAdminRepository;

	/**
	 * AgendaProfile 변경 메서드
	 * @param intraId 로그인한 유저의 id
	 * @param reqDto 변경할 프로필 정보
	 */
	@Transactional
	public void modifyAgendaProfile(String intraId, AgendaProfileChangeAdminReqDto reqDto) {
		AgendaProfile agendaProfile = agendaProfileAdminRepository.findByIntraId(intraId)
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		agendaProfile.updateProfileAdmin(reqDto.getUserContent(), reqDto.getUserGithub(),
			Location.valueOfLocation(reqDto.getUserLocation()));
		agendaProfileAdminRepository.save(agendaProfile);
	}
}
