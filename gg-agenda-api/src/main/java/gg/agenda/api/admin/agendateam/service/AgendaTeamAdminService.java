package gg.agenda.api.admin.agendateam.service;

import static gg.utils.exception.ErrorCode.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaProfileAdminRepository;
import gg.admin.repo.agenda.AgendaTeamAdminRepository;
import gg.admin.repo.agenda.AgendaTeamProfileAdminRepository;
import gg.agenda.api.admin.agendateam.controller.request.AgendaTeamMateReqDto;
import gg.agenda.api.admin.agendateam.controller.request.AgendaTeamUpdateDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaTeamAdminService {

	private final AgendaAdminRepository agendaAdminRepository;

	private final AgendaTeamAdminRepository agendaTeamAdminRepository;

	private final AgendaProfileAdminRepository agendaProfileAdminRepository;

	private final AgendaTeamProfileAdminRepository agendaTeamProfileAdminRepository;

	@Transactional(readOnly = true)
	public Page<AgendaTeam> getAgendaTeamList(UUID agendaKey, Pageable pageable) {
		Agenda agenda = agendaAdminRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));
		return agendaTeamAdminRepository.findAllByAgenda(agenda, pageable);
	}

	@Transactional(readOnly = true)
	public AgendaTeam getAgendaTeamByTeamKey(UUID teamKey) {
		return agendaTeamAdminRepository.findByTeamKey(teamKey)
			.orElseThrow(() -> new NotExistException(AGENDA_TEAM_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public List<AgendaProfile> getAgendaProfileListByAgendaTeam(AgendaTeam agendaTeam) {
		return agendaTeamProfileAdminRepository.findAllByAgendaTeamAndIsExistIsTrue(agendaTeam).stream()
			.map(AgendaTeamProfile::getProfile).collect(Collectors.toList());
	}

	@Transactional
	public void updateAgendaTeam(AgendaTeamUpdateDto agendaTeamUpdateDto) {
		AgendaTeam team = agendaTeamAdminRepository.findByTeamKey(agendaTeamUpdateDto.getTeamKey())
			.orElseThrow(() -> new NotExistException(AGENDA_TEAM_NOT_FOUND));
		List<AgendaTeamProfile> profiles = agendaTeamProfileAdminRepository.findAllByAgendaTeamAndIsExistIsTrue(team);
		List<String> updatedTeamMates = agendaTeamUpdateDto.getTeamMates().stream()
			.map(AgendaTeamMateReqDto::getIntraId)
			.collect(Collectors.toList());
		List<String> currentTeamMates = profiles.stream()
			.map(profile -> profile.getProfile().getIntraId())
			.collect(Collectors.toList());

		// AgendaTeam 정보 변경
		team.updateTeamAdmin(agendaTeamUpdateDto.getTeamName(), agendaTeamUpdateDto.getTeamContent(),
			agendaTeamUpdateDto.getTeamIsPrivate(), agendaTeamUpdateDto.getTeamStatus());
		team.getAgenda().adminConfirmTeam(team.getStatus());
		team.updateLocation(agendaTeamUpdateDto.getTeamLocation(), profiles);
		team.acceptAward(agendaTeamUpdateDto.getTeamAward(), agendaTeamUpdateDto.getTeamAwardPriority());

		// AgendaTeam 팀원 내보내기
		profiles.stream().filter(profile -> !updatedTeamMates.contains(profile.getProfile().getIntraId()))
			.forEach(agentTeamProfile -> {
				String intraId = agentTeamProfile.getProfile().getIntraId();
				agentTeamProfile.getAgendaTeam().leaveTeamMateAdmin(intraId);
				agentTeamProfile.changeExistFalse();
			});

		// AgendaTeam 팀원 추가하기
		updatedTeamMates.stream().filter(intraId -> !currentTeamMates.contains(intraId))
			.forEach(intraId -> {
				AgendaProfile profile = agendaProfileAdminRepository.findByIntraId(intraId)
					.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));
				agendaTeamProfileAdminRepository.findByAgendaAndProfileAndIsExistTrue(team.getAgenda(), profile)
					.ifPresent(agendaTeamProfile -> {
						throw new ForbiddenException(AGENDA_TEAM_FORBIDDEN);
					});
				team.attendTeamAdmin(team.getAgenda());
				AgendaTeamProfile agendaTeamProfile = new AgendaTeamProfile(team, team.getAgenda(), profile);
				agendaTeamProfileAdminRepository.save(agendaTeamProfile);
			});

		// 팀장이 없는지 확인하는 로직
		if (!updatedTeamMates.contains(team.getLeaderIntraId())) {
			throw new NotExistException(TEAM_LEADER_NOT_FOUND);
		}
	}

	@Transactional
	public void cancelAgendaTeam(AgendaTeam agendaTeam) {
		List<AgendaTeamProfile> agendaTeamProfiles = agendaTeamProfileAdminRepository
			.findAllByAgendaTeamAndIsExistIsTrue(agendaTeam);
		agendaTeamProfiles.forEach(AgendaTeamProfile::changeExistFalse);
		Agenda agenda = agendaTeam.getAgenda();
		agenda.adminCancelTeam(agendaTeam.getStatus());
		agendaTeam.adminCancelTeam();
	}
}
