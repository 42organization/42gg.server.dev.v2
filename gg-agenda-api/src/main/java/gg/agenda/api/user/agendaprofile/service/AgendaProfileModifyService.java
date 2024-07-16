package gg.agenda.api.user.agendaprofile.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import gg.agenda.api.user.agendaprofile.controller.request.AgendaProfileChangeReqDto;
import gg.data.agenda.AgendaProfile;
import gg.data.user.User;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaProfileModifyService {

	private final UserRepository userRepository;
	private final AgendaProfileRepository agendaProfileRepository;

	@Transactional
	public void modifyAgendaProfile(String intraId, AgendaProfileChangeReqDto reqDto) throws UserNotFoundException {
		// User와 AgendaProfile을 조회
		User user = userRepository.findByIntraId(intraId)
			.orElseThrow(() -> new UserNotFoundException());

		AgendaProfile agendaProfile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new UserNotFoundException());

		// 변경된 값들로 업데이트
		agendaProfile.updateProfile(reqDto.getUserContent(), reqDto.getUserGithub());

		// 변경사항을 저장
		agendaProfileRepository.save(agendaProfile);
	}
}
