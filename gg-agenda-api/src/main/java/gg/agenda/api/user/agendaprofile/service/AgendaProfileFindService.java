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
		// UserDto를 User 엔티티로 변환
		User loginUser = userRepository.getById(user.getId());

		// 유저의 ID로 AgendaProfile을 조회
		AgendaProfile agendaProfile = agendaProfileRepository.findByUserId(loginUser.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		// 임시로 지정된 티켓 수를 가져오는 메서드
		int ticketCount = ticketRepository.findByAgendaProfileIdAndIsUsedFalseAndIsApproveTrue(agendaProfile.getId())
			.size();

		// AgendaProfileDetailsResDto 객체를 생성하여 반환
		return new AgendaProfileDetailsResDto(loginUser, agendaProfile, ticketCount);
	}

}
