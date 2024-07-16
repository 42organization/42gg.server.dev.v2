package gg.agenda.api.user.agendaprofile.service;

import static gg.utils.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.agendaprofile.controller.response.AgendaProfileDetailsResDto;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.data.agenda.AgendaProfile;
import gg.data.user.User;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.agenda.TicketRepository;
import gg.repo.user.UserRepository;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaProfileFindService {

	private final UserRepository userRepository;
	private final AgendaProfileRepository agendaProfileRepository;
	private final TicketRepository ticketRepository;

	/**
	 * AgendaProfile 상세 정보를 조회하는 메서드
	 *
	 * @param user 로그인한 유저의 UserDto
	 * @return AgendaProfileDetailsResDto 객체
	 */
	@Transactional(readOnly = true)
	public AgendaProfileDetailsResDto getAgendaProfileDetails(@Login UserDto user) {
		User loginUser = userRepository.getById(user.getId());

		AgendaProfile agendaProfile = agendaProfileRepository.findByUserId(loginUser.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		int ticketCount = ticketRepository.findByAgendaProfileIdAndIsUsedFalseAndIsApprovedTrue(agendaProfile.getId())
			.size();

		return new AgendaProfileDetailsResDto(loginUser, agendaProfile, ticketCount);
	}
}
