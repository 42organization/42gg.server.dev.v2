package gg.agenda.api.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.repo.agenda.AgendaTeamProfileRepository;
import gg.utils.sns.MessageSender;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgendaSlackService {
	private final MessageSender messageSender;
	private final SnsMessageUtil snsMessageUtil;
	private final AgendaTeamProfileRepository agendaTeamProfileRepository;

	public void slackAddAgendaAnnouncement(Agenda agenda, AgendaAnnouncement newAnnounce) {
		List<AgendaTeamProfile> agendaTeamProfiles = agendaTeamProfileRepository.findAllByAgendaAndIsExistTrue(agenda);
		String message = snsMessageUtil.addAgendaAnnouncementMessage(agenda, newAnnounce);
		agendaTeamProfiles.stream().map(atp -> atp.getProfile().getIntraId())
			.forEach(intraId -> messageSender.send(intraId, message));
	}

	public void slackCancelAgenda(Agenda agenda) {
		List<AgendaTeamProfile> agendaTeamProfiles = agendaTeamProfileRepository.findAllByAgendaAndIsExistTrue(agenda);
		String message = snsMessageUtil.cancelAgendaMessage(agenda);
		agendaTeamProfiles.stream().map(atp -> atp.getProfile().getIntraId())
			.forEach(intraId -> messageSender.send(intraId, message));
	}

	public void slackFinishAgenda(Agenda agenda) {
		List<AgendaTeamProfile> agendaTeamProfiles = agendaTeamProfileRepository.findAllByAgendaAndIsExistTrue(agenda);
		String message = snsMessageUtil.finishAgendaMessage(agenda);
		agendaTeamProfiles.stream().map(atp -> atp.getProfile().getIntraId())
			.forEach(intraId -> messageSender.send(intraId, message));
	}

	public void slackConfirmAgenda(Agenda agenda) {
		List<AgendaTeamProfile> agendaTeamProfiles = agendaTeamProfileRepository.findAllByAgendaAndIsExistTrue(agenda);
		String message = snsMessageUtil.confirmAgendaMessage(agenda);
		agendaTeamProfiles.stream().map(atp -> atp.getProfile().getIntraId())
			.forEach(intraId -> messageSender.send(intraId, message));
	}

	public void slackConfirmAgendaTeam(Agenda agenda, AgendaTeam newTeam) {
		List<AgendaTeamProfile> agendaTeamProfiles = agendaTeamProfileRepository.findByAgendaTeamAndIsExistTrue(
			newTeam);
		String message = snsMessageUtil.confirmTeamMessage(agenda, newTeam);
		agendaTeamProfiles.stream().map(atp -> atp.getProfile().getIntraId())
			.forEach(intraId -> messageSender.send(intraId, message));
		if (agenda.getMaxTeam() == agenda.getCurrentTeam()) {
			String toHostMessage = snsMessageUtil.agendaHostMinTeamSatisfiedMessage(agenda);
			messageSender.send(agenda.getHostIntraId(), toHostMessage);
		} else if (agenda.getMinTeam() == agenda.getCurrentTeam()) {
			String toHostMessage = snsMessageUtil.agendaHostMaxTeamSatisfiedMessage(agenda);
			messageSender.send(agenda.getHostIntraId(), toHostMessage);
		}
	}

	public void slackCancelAgendaTeam(Agenda agenda, AgendaTeam newTeam) {
		List<AgendaTeamProfile> agendaTeamProfiles = agendaTeamProfileRepository.findByAgendaTeamAndIsExistTrue(
			newTeam);
		String message = snsMessageUtil.cancelTeamMessage(agenda, newTeam);
		agendaTeamProfiles.stream().map(atp -> atp.getProfile().getIntraId())
			.forEach(intraId -> messageSender.send(intraId, message));
	}

	public void slackCancelByAgendaConfirm(Agenda agenda, List<AgendaTeam> failTeam) {
		List<AgendaTeamProfile> agendaTeamProfiles = agendaTeamProfileRepository.findByAgendaTeamInAndIsExistTrue(
			failTeam);
		String message = snsMessageUtil.failTeamMessage(agenda);
		agendaTeamProfiles.stream().map(atp -> atp.getProfile().getIntraId())
			.forEach(intraId -> messageSender.send(intraId, message));
	}

	public void slackAttendTeamMate(Agenda agenda, AgendaTeam agendaTeam, String userIntraId) {
		List<AgendaTeamProfile> agendaTeamProfiles = agendaTeamProfileRepository.findByAgendaTeamAndIsExistTrue(
			agendaTeam);
		String message = snsMessageUtil.attendTeamMateMessage(agenda, agendaTeam, userIntraId);
		agendaTeamProfiles.stream().map(atp -> atp.getProfile().getIntraId())
			.filter(intraId -> !intraId.equals(userIntraId))
			.forEach(intraId -> messageSender.send(intraId, message));
	}

	public void slackLeaveTeamMate(Agenda agenda, AgendaTeam agendaTeam, String userIntraId) {
		List<AgendaTeamProfile> agendaTeamProfiles = agendaTeamProfileRepository.findByAgendaTeamAndIsExistTrue(
			agendaTeam);
		String message = snsMessageUtil.leaveTeamMateMessage(agenda, agendaTeam, userIntraId);
		agendaTeamProfiles.stream().map(atp -> atp.getProfile().getIntraId())
			.forEach(intraId -> messageSender.send(intraId, message));
	}
}
