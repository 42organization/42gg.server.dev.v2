package gg.agenda.api.user.agendaprofile.service;

import static gg.utils.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.agendaprofile.controller.request.AgendaProfileChangeReqDto;
import gg.data.agenda.AgendaProfile;
import gg.data.user.User;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaProfileService {

	private final UserRepository userRepository;
	private final AgendaProfileRepository agendaProfileRepository;

	/**
	 * AgendaProfile 변경 메서드
	 * @param userId 로그인한 유저의 id
	 * @param reqDto 변경할 프로필 정보
	 */
	@Transactional
	public void modifyAgendaProfile(Long userId, AgendaProfileChangeReqDto reqDto) {
		User user = userRepository.getById(userId);

		AgendaProfile agendaProfile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		agendaProfile.updateProfile(reqDto.getUserContent(), reqDto.getUserGithub());
		agendaProfileRepository.save(agendaProfile);
	}

	@Transactional(readOnly = true)
	public AgendaProfile getAgendaProfile(Long userId) {
		return agendaProfileRepository.findByUserId(userId)
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));
	}
}
