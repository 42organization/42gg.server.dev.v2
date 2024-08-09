package gg.agenda.api.user.ticket.service;

import static gg.utils.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.ticket.controller.response.TicketHistoryResDto;
import gg.auth.UserDto;
import gg.auth.utils.RefreshTokenUtil;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.Auth42Token;
import gg.data.agenda.Ticket;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.TicketRepository;
import gg.repo.user.Auth42TokenRedisRepository;
import gg.utils.DateTimeUtil;
import gg.utils.exception.custom.DuplicationException;
import gg.utils.exception.custom.NotExistException;
import gg.utils.external.ApiUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {
	private final ApiUtil apiUtil;
	private final RefreshTokenUtil refreshTokenUtil;
	private final TicketRepository ticketRepository;
	private final AgendaRepository agendaRepository;
	private final AgendaProfileRepository agendaProfileRepository;
	private final Auth42TokenRedisRepository auth42TokenRedisRepository;

	@Value("${info.web.pointHistoryUrl}")
	private String pointHistoryUrl;

	private static final String selfDonation = "Provided points to the pool";
	private static final String autoDonation = "correction points trimming weekly";

	@Transactional(propagation = Propagation.MANDATORY)
	public void refundTicket(AgendaTeamProfile changedTeamProfile) {
		Ticket.createRefundedTicket(changedTeamProfile);
	}

	/**
	 * 티켓 설정 추가
	 * @param user 사용자 정보
	 */
	@Transactional
	public void addTicketSetup(UserDto user) {
		AgendaProfile profile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));
		Optional<Ticket> optionalTicket = ticketRepository.findByAgendaProfileAndIsApprovedFalse(profile);
		if (optionalTicket.isPresent()) {
			throw new DuplicationException(ALREADY_TICKET_SETUP);
		}
		ticketRepository.save(Ticket.createNotApporveTicket(profile));
	}

	/**
	 * 티켓 수 조회
	 * @param user 사용자 정보
	 * @return 티켓 수
	 */
	@Transactional(readOnly = true)
	public int findTicketCount(UserDto user) {
		AgendaProfile profile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));
		List<Ticket> tickets = ticketRepository.findByAgendaProfileIdAndIsUsedFalseAndIsApprovedTrue(profile.getId());
		return tickets.size();
	}

	/**
	 * 티켓 승인/거절
	 * @param user 사용자 정보
	 */
	@Transactional
	public void modifyTicketApprove(UserDto user) {
		AgendaProfile profile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		Ticket setUpTicket = ticketRepository.findByAgendaProfileAndIsApprovedFalse(profile)
			.orElseThrow(() -> new NotExistException(NOT_SETUP_TICKET));

		Auth42Token auth42Token = auth42TokenRedisRepository.findByIntraId(user.getIntraId())
			.orElseThrow(() -> new NotExistException(AUTH_NOT_FOUND));

		String url = pointHistoryUrl.replace("{id}", auth42Token.getIntra42Id());
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + auth42Token.getAccessToken());
		headers.setContentType(MediaType.APPLICATION_JSON);

		List<Map<String, Object>> response = apiUtil.apiCall(url, List.class, headers, HttpMethod.GET);
		if (response == null || response.isEmpty()) {
			Auth42Token refreshedToken = refreshTokenUtil.refreshAuth42Token(auth42Token);
			auth42TokenRedisRepository.update42Token(user.getIntraId(), refreshedToken);
			response = apiUtil.apiCall(url, List.class, headers, HttpMethod.GET);
		}
		LocalDateTime cutoffTime = setUpTicket.getCreatedAt();

		int ticketSum = response.stream()
			.takeWhile(histories -> DateTimeUtil.convertToSeoulDateTime((String)histories.get("created_at"))
				.isAfter(cutoffTime))
			.filter(histories -> {
				String reason = (String)histories.get("reason");
				return reason.contains(selfDonation) || reason.contains(autoDonation);
			})
			.mapToInt(histories -> ((Number)histories.get("sum")).intValue() * (-1))
			.sum();

		if (ticketSum == 0) {
			throw new NotExistException("POINT_HISTORY_NOT_FOUND");
		} else if (ticketSum >= 2) {
			ticketRepository.save(Ticket.createApproveTicket(profile));
		}
		setUpTicket.changeIsApproved();
		ticketRepository.save(setUpTicket);
	}

	/**
	 * 티켓 이력 조회
	 * @param user 사용자 정보
	 * @param pageable 페이지 정보
	 * @return 티켓 이력 목록
	 */
	@Transactional(readOnly = true)
	public List<TicketHistoryResDto> listTicketHistory(UserDto user, Pageable pageable) {
		AgendaProfile profile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		Page<Ticket> tickets = ticketRepository.findByAgendaProfileId(profile.getId(), pageable);

		List<TicketHistoryResDto> ticketHistoryResDtos = tickets.getContent().stream()
			.map(TicketHistoryResDto::new)
			.collect(Collectors.toList());

		for (TicketHistoryResDto dto : ticketHistoryResDtos) {
			if (dto.getIssuedFromKey() != null) {
				Agenda agenda = agendaRepository.findAgendaByAgendaKey(dto.getIssuedFromKey()).orElse(null);
				dto.changeIssuedFrom(agenda);
			}
			if (dto.getUsedToKey() != null) {
				Agenda agenda = agendaRepository.findAgendaByAgendaKey(dto.getUsedToKey()).orElse(null);
				dto.changeUsedTo(agenda);
			}
		}
		return ticketHistoryResDtos;
	}
}
