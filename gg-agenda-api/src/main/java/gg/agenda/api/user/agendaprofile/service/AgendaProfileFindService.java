package gg.agenda.api.user.agendaprofile.service;

import static gg.utils.exception.ErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.agendaprofile.controller.response.CurrentAttendAgendaListResDto;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.type.AgendaStatus;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.agenda.AgendaTeamProfileRepository;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgendaProfileFindService {

	private final AgendaProfileRepository agendaProfileRepository;
	private final AgendaTeamProfileRepository agendaTeamProfileRepository;

	@Transactional(readOnly = true)
	public AgendaProfile findAgendaProfileByIntraId(String intraId) {
		return agendaProfileRepository.findByIntraId(intraId)
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));
	}

	/**
	 * 자기가 참여중인 Agenda 목록 조회하는 메서드
	 * @param intraId 로그인한 유저의 id
	 * @return AgendaProfileDetailsResDto 객체
	 */
	@Transactional(readOnly = true)
	public List<CurrentAttendAgendaListResDto> findCurrentAttendAgenda(String intraId) {
		AgendaProfile agendaProfile = agendaProfileRepository.findByIntraId(intraId)
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		List<AgendaTeamProfile> agendaTeamProfiles = agendaTeamProfileRepository.findByProfileAndIsExistTrue(
			agendaProfile);

		return agendaTeamProfiles.stream()
			.filter(agendaTeamProfile -> {
				AgendaStatus status = agendaTeamProfile.getAgenda().getStatus();
				return status == AgendaStatus.OPEN || status == AgendaStatus.CONFIRM;
			})
			.map(CurrentAttendAgendaListResDto::new)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Page<AgendaTeamProfile> findAttendedAgenda(String intraId, Pageable pageable) {
		AgendaProfile agendaProfile = agendaProfileRepository.findByIntraId(intraId)
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));
		return agendaTeamProfileRepository.findByProfileAndIsExistTrueAndAgendaStatus(
				agendaProfile, AgendaStatus.FINISH, pageable);
	}

	@Transactional(readOnly = true)
	public List<AgendaTeamProfile> findTeamMatesFromAgendaTeam(AgendaTeam agendaTeam) {
		return agendaTeamProfileRepository.findByAgendaTeamAndIsExistTrue(agendaTeam);
	}
}
