package gg.agenda.api.user.agendateam.service;

import static gg.data.agenda.type.AgendaTeamStatus.*;
import static gg.utils.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.agendateam.controller.request.TeamCreateReqDto;
import gg.agenda.api.user.agendateam.controller.request.TeamKeyReqDto;
import gg.agenda.api.user.agendateam.controller.request.TeamUpdateReqDto;
import gg.agenda.api.user.agendateam.controller.response.ConfirmTeamResDto;
import gg.agenda.api.user.agendateam.controller.response.MyTeamSimpleResDto;
import gg.agenda.api.user.agendateam.controller.response.OpenTeamResDto;
import gg.agenda.api.user.agendateam.controller.response.TeamDetailsResDto;
import gg.agenda.api.user.agendateam.controller.response.TeamKeyResDto;
import gg.agenda.api.user.ticket.service.TicketService;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.Ticket;
import gg.data.agenda.type.Coalition;
import gg.data.agenda.type.Location;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.AgendaTeamProfileRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.repo.agenda.TicketRepository;
import gg.utils.exception.custom.BusinessException;
import gg.utils.exception.custom.DuplicationException;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaTeamService {
	private final TicketService ticketService;
	private final AgendaRepository agendaRepository;
	private final TicketRepository ticketRepository;
	private final AgendaTeamRepository agendaTeamRepository;
	private final AgendaProfileRepository agendaProfileRepository;
	private final AgendaTeamProfileRepository agendaTeamProfileRepository;

	/**
	 * 내 팀 간단 정보 조회
	 * @param user 사용자 정보, agendaId 아젠다 아이디
	 * @return 내 팀 간단 정보
	 */
	public Optional<MyTeamSimpleResDto> detailsMyTeamSimple(UserDto user, UUID agendaKey) {
		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));

		AgendaProfile agendaProfile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		Optional<AgendaTeam> agendaTeam = agendaTeamProfileRepository.findByAgendaProfileAndIsExistTrue(agenda,
				agendaProfile)
			.map(AgendaTeamProfile::getAgendaTeam);
		if (agendaTeam.isEmpty()) {
			return Optional.empty();
		}

		List<AgendaTeamProfile> agendaTeamProfileList = agendaTeamProfileRepository
			.findByAgendaTeamAndIsExistTrue(agendaTeam.get());

		List<Coalition> coalitions = agendaTeamProfileList.stream()
			.map(AgendaTeamProfile::getProfile)
			.map(AgendaProfile::getCoalition)
			.collect(Collectors.toList());

		return Optional.of(new MyTeamSimpleResDto(agendaTeam.get(), coalitions));
	}

	/**
	 * 아젠다 팀 상세 정보 조회
	 * @param user 사용자 정보, teamCreateReqDto 팀 키, agendaKey 아젠다 키
	 * @return 만들어진 팀 상세 정보
	 */
	@Transactional(readOnly = true)
	public TeamDetailsResDto detailsAgendaTeam(UserDto user, UUID agendaKey, TeamKeyReqDto teamKeyReqDto) {
		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));

		AgendaTeam agendaTeam = agendaTeamRepository
			.findByAgendaAndTeamKeyAndStatus(agenda, teamKeyReqDto.getTeamKey(), OPEN, CONFIRM)
			.orElseThrow(() -> new NotExistException(AGENDA_TEAM_NOT_FOUND));

		List<AgendaTeamProfile> agendaTeamProfileList = agendaTeamProfileRepository
			.findByAgendaTeamAndIsExistTrue(agendaTeam);

		if (agendaTeam.getStatus().equals(CONFIRM)) {  // 팀이 확정 상태인 경우에
			if (agendaTeamProfileList.stream() // 팀에 속한 유저가 아닌 경우
				.noneMatch(profile -> profile.getProfile().getUserId().equals(user.getId()))) {
				throw new ForbiddenException(NOT_TEAM_MATE); // 조회 불가
			}
		}
		return new TeamDetailsResDto(agendaTeam, agendaTeamProfileList);
	}

	/**
	 * 아젠다 팀 생성하기
	 * @param user 사용자 정보, teamCreateReqDto 팀 생성 요청 정보, agendaId 아젠다 아이디
	 * @return 만들어진 팀 KEY
	 */
	@Transactional
	public TeamKeyResDto addAgendaTeam(UserDto user, TeamCreateReqDto teamCreateReqDto, UUID agendaKey) {
		AgendaProfile agendaProfile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));

		agenda.addTeam(Location.valueOfLocation(teamCreateReqDto.getTeamLocation()), LocalDateTime.now());

		if (agenda.getHostIntraId().equals(user.getIntraId())) {
			throw new ForbiddenException(HOST_FORBIDDEN);
		}

		if (agenda.getLocation() != Location.MIX && agenda.getLocation() != agendaProfile.getLocation()) {
			throw new BusinessException(LOCATION_NOT_VALID);
		}

		agendaTeamProfileRepository.findByAgendaAndProfileAndIsExistTrue(agenda, agendaProfile)
			.ifPresent(teamProfile -> {
				throw new DuplicationException(AGENDA_TEAM_FORBIDDEN);
			});

		if (agenda.getIsOfficial()) {
			Ticket ticket = ticketRepository.findFirstByAgendaProfileAndIsApprovedTrueAndIsUsedFalseOrderByCreatedAtAsc(
					agendaProfile)
				.orElseThrow(() -> new ForbiddenException(TICKET_NOT_EXIST));
			ticket.useTicket(agenda.getAgendaKey());
		}

		agendaTeamRepository.findByAgendaAndTeamNameAndStatus(agenda, teamCreateReqDto.getTeamName(), OPEN, CONFIRM)
			.ifPresent(team -> {
				throw new DuplicationException(TEAM_NAME_EXIST);
			});

		AgendaTeam agendaTeam = TeamCreateReqDto.toEntity(teamCreateReqDto, agenda, user.getIntraId());
		AgendaTeamProfile agendaTeamProfile = new AgendaTeamProfile(agendaTeam, agenda, agendaProfile);
		agendaRepository.save(agenda);
		agendaTeamRepository.save(agendaTeam);
		agendaTeamProfileRepository.save(agendaTeamProfile);
		return new TeamKeyResDto(agendaTeam.getTeamKey().toString());
	}

	/**
	 * 아젠다 팀 확정하기
	 * @param user 사용자 정보, teamKeyReqDto 팀 KEY 요청 정보, agendaId 아젠다 아이디
	 */
	@Transactional
	public void confirmTeam(UserDto user, UUID agendaKey, UUID teamKey) {
		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));

		AgendaTeam agendaTeam = agendaTeamRepository
			.findByAgendaAndTeamKeyAndStatus(agenda, teamKey, OPEN, CONFIRM)
			.orElseThrow(() -> new NotExistException(AGENDA_TEAM_NOT_FOUND));

		if (!agendaTeam.getLeaderIntraId().equals(user.getIntraId())) {
			throw new ForbiddenException(TEAM_LEADER_FORBIDDEN);
		}
		if (agendaTeam.getMateCount() < agenda.getMinPeople()) {
			throw new BusinessException(NOT_ENOUGH_TEAM_MEMBER);
		}
		agenda.confirmTeam(agendaTeam.getLocation(), LocalDateTime.now());
		agendaTeam.confirm();
		agendaTeamRepository.save(agendaTeam);
	}

	/**
	 * 아젠다 팀 찾기
	 * @param teamKey 팀 KEY
	 */
	@Transactional(readOnly = true)
	public AgendaTeam getAgendaTeam(UUID teamKey) {
		return agendaTeamRepository.findByTeamKeyFetchJoin(teamKey)
			.orElseThrow(() -> new NotExistException(AGENDA_TEAM_NOT_FOUND));
	}

	/**
	 * 팀장이 팀 나가기
	 * @param agendaTeam 팀
	 */
	@Transactional
	public void leaveTeamAll(AgendaTeam agendaTeam) {
		List<AgendaTeamProfile> agendaTeamProfiles = agendaTeamProfileRepository
			.findByAgendaTeamAndIsExistTrue(agendaTeam);
		agendaTeamProfiles.forEach(agendaTeamProfile -> leaveTeam(agendaTeam, agendaTeamProfile));
		agendaTeam.cancelTeam();
	}

	/**
	 * 아젠다 팀원 나가기
	 * @param agendaTeam 아젠다 팀, user 사용자 정보
	 */
	@Transactional
	public void leaveTeamMate(AgendaTeam agendaTeam, UserDto user) {
		List<AgendaTeamProfile> agendaTeamProfiles = agendaTeamProfileRepository
			.findByAgendaTeamAndIsExistTrue(agendaTeam);
		AgendaTeamProfile agendaTeamProfile = agendaTeamProfiles.stream()
			.filter(profile -> profile.getProfile().getUserId().equals(user.getId()))
			.findFirst()
			.orElseThrow(() -> new ForbiddenException(NOT_TEAM_MATE));
		leaveTeam(agendaTeam, agendaTeamProfile);
	}

	/**
	 * 팀원이 팀 나가기
	 * @param agendaTeamProfile 팀 프로필
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	public void leaveTeam(AgendaTeam agendaTeam, AgendaTeamProfile agendaTeamProfile) {
		agendaTeam.leaveTeamMate();
		agendaTeamProfile.changeExistFalse();
		if (agendaTeamProfile.getAgenda().getIsOfficial()) {
			ticketService.refundTicket(agendaTeamProfile);
		}
	}

	/**
	 * 아젠다 팀 공개 모집인 팀 목록 조회
	 * @param pageable 페이지네이션 요청 정보, agendaId 아젠다 아이디
	 */
	@Transactional(readOnly = true)
	public List<OpenTeamResDto> listOpenTeam(UUID agendaKey, Pageable pageable) {
		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));
		List<AgendaTeam> agendaTeams = agendaTeamRepository.findByAgendaAndStatusAndIsPrivateFalse(agenda, OPEN,
			pageable).getContent();
		return agendaTeams.stream()
			.map(agendaTeam -> {
				List<Coalition> coalitions = agendaTeamProfileRepository
					.findByAgendaTeamAndIsExistTrue(agendaTeam).stream()
					.map(agendaTeamProfile -> agendaTeamProfile.getProfile().getCoalition())
					.collect(Collectors.toList());
				return new OpenTeamResDto(agendaTeam, coalitions);
			})
			.collect(Collectors.toList());
	}

	/**
	 * 아젠다 팀 확정된 팀 목록 조회
	 * @param pageable 페이지네이션 요청 정보, agendaId 아젠다 아이디
	 */
	@Transactional(readOnly = true)
	public List<ConfirmTeamResDto> listConfirmTeam(UUID agendaKey, Pageable pageable) {
		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));

		List<AgendaTeam> agendaTeams = agendaTeamRepository.findByAgendaAndStatus(agenda, CONFIRM, pageable)
			.getContent();
		return agendaTeams.stream()
			.map(agendaTeam -> {
				List<Coalition> coalitions = agendaTeamProfileRepository
					.findByAgendaTeamAndIsExistTrue(agendaTeam).stream()
					.map(agendaTeamProfile -> agendaTeamProfile.getProfile().getCoalition())
					.collect(Collectors.toList());
				return new ConfirmTeamResDto(agendaTeam, coalitions);
			})
			.collect(Collectors.toList());
	}

	/**
	 * 아젠다 팀 참여하기
	 * @param user 사용자 정보, teamKeyReqDto 팀 KEY 요청 정보, agendaId 아젠다 아이디
	 */
	@Transactional
	public void modifyAttendTeam(UserDto user, TeamKeyReqDto teamKeyReqDto, UUID agendaKey) {
		AgendaProfile agendaProfile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));

		AgendaTeam agendaTeam = agendaTeamRepository
			.findByAgendaAndTeamKeyAndStatus(agenda, teamKeyReqDto.getTeamKey(), OPEN, CONFIRM)
			.orElseThrow(() -> new NotExistException(AGENDA_TEAM_NOT_FOUND));

		agendaTeamProfileRepository.findByAgendaAndProfileAndIsExistTrue(agenda, agendaProfile)
			.ifPresent(profile -> {
				throw new ForbiddenException(AGENDA_TEAM_FORBIDDEN);
			});

		if (agenda.getIsOfficial()) {
			Ticket ticket = ticketRepository
				.findFirstByAgendaProfileAndIsApprovedTrueAndIsUsedFalseOrderByCreatedAtAsc(agendaProfile)
				.orElseThrow(() -> new ForbiddenException(TICKET_NOT_EXIST));
			ticket.useTicket(agenda.getAgendaKey());
		}
		agenda.attendTeam(agendaProfile.getLocation(), LocalDateTime.now());
		agendaTeam.attendTeam(agenda);
		agendaTeamProfileRepository.save(new AgendaTeamProfile(agendaTeam, agenda, agendaProfile));
	}

	/**
	 * 아젠다 팀 수정하기
	 * @param user 사용자 정보, teamUpdateReqDto 팀 수정 요청 정보, agendaId 아젠다 아이디
	 */
	@Transactional
	public void modifyAgendaTeam(UserDto user, TeamUpdateReqDto teamUpdateReqDto, UUID agendaKey) {
		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));

		AgendaTeam agendaTeam = agendaTeamRepository
			.findByAgendaAndTeamKeyAndStatus(agenda, teamUpdateReqDto.getTeamKey(), OPEN, CONFIRM)
			.orElseThrow(() -> new NotExistException(AGENDA_TEAM_NOT_FOUND));

		if (!agendaTeam.getLeaderIntraId().equals(user.getIntraId())) {
			throw new ForbiddenException(TEAM_LEADER_FORBIDDEN);
		}

		List<AgendaTeamProfile> profiles = agendaTeamProfileRepository.findAllByAgendaTeam(agendaTeam);

		agenda.updateTeam(Location.valueOfLocation(teamUpdateReqDto.getTeamLocation()), LocalDateTime.now());
		agendaTeam.updateTeam(teamUpdateReqDto.getTeamName(), teamUpdateReqDto.getTeamContent(),
			teamUpdateReqDto.getTeamIsPrivate(), Location.valueOfLocation(teamUpdateReqDto.getTeamLocation()),
			profiles);
		agendaTeamRepository.save(agendaTeam);
	}
}
