package gg.agenda.api.user.agendaprofile.service;

import static gg.utils.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.agendaprofile.controller.response.AgendaProfileDetailsResDto;
import gg.data.agenda.AgendaProfile;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.agenda.AgendaTeamProfileRepository;
import gg.repo.agenda.AgendaTeamRepository;
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
	private final AgendaTeamProfileRepository agendaTeamProfileRepository;
	private final AgendaTeamRepository agendaTeamRepository;

	/**
	 * AgendaProfile 상세 정보를 조회하는 메서드
	 * @param intraId 로그인한 유저의 id
	 * @return AgendaProfileDetailsResDto 객체
	 */
	@Transactional(readOnly = true)
	public AgendaProfileDetailsResDto detailsAgendaProfile(String intraId) {
		AgendaProfile agendaProfile = agendaProfileRepository.findByIntraId(intraId)
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		int ticketCount = ticketRepository.findByAgendaProfileIdAndIsUsedFalseAndIsApprovedTrue(agendaProfile.getId())
			.size();

		return new AgendaProfileDetailsResDto(intraId, agendaProfile, ticketCount);
	}

	// /**
	//  * 자기가 참여중인 Agenda 목록 조회하는 메서드
	//  * @param userId 로그인한 유저의 id
	//  * @return AgendaProfileDetailsResDto 객체
	//  */
	// @Transactional(readOnly = true)
	// public List<CurrentAttendAgendaListResDto> findCurrentAttendAgenda(Long userId) {
	// 	User loginUser = userRepository.getById(userId);
	//
	// 	AgendaProfile agendaProfile = agendaProfileRepository.findByUserId(loginUser.getId())
	// 		.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));
	//
	// 	List<AgendaTeamProfile> agendaTeamProfiles = agendaTeamProfileRepository.findByUserId(loginUser.getId());
	//
	// 	if (agendaTeamProfiles.isEmpty()) {
	// 		throw new NotExistException(AGENDA_PROFILE_NOT_FOUND);
	// 	}
	//
	// 	return agendaTeamProfiles.stream()
	// 		.filter(agendaTeamProfile -> agendaTeamProfile.getAgenda().getStatus() == AgendaStatus.ON_GOING)
	// 		.map(agendaTeamProfile -> {
	// 			AgendaTeam agendaTeam = agendaTeamRepository.findById(agendaTeamProfile.getId())
	// 				.orElseThrow(() -> new NotExistException(AGENDA_TEAM_NOT_FOUND));
	// 			return new CurrentAttendAgendaListResDto(agendaTeamProfile, agendaTeam);
	// 		})
	// 		.collect(Collectors.toList());
	// }
}
