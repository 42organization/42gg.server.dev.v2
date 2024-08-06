package gg.agenda.api.user.ticket.service;

import static gg.utils.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Auth42Token;
import gg.data.agenda.Ticket;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.Auth42TokenRedisRepository;
import gg.repo.agenda.TicketRepository;
import gg.utils.exception.custom.DuplicationException;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.NotExistException;
import gg.utils.external.ApiUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {
	private final ApiUtil apiUtil;
	private final TicketRepository ticketRepository;
	private final AgendaRepository agendaRepository;
	private final AgendaProfileRepository agendaProfileRepository;
	private final Auth42TokenRedisRepository auth42TokenRedisRepository;

	@Value("https://api.intra.42.fr/v2/users/{id}/correction_point_historics?sort=-id")
	private String pointHistoryUrl;

	/**
	 * 티켓 환불
	 * @param changedProfiles 변경된 프로필 목록
	 * @param agendaKey 아젠다 키
	 * @Annotation 트랜잭션의 원자성을 보장하기 위해 부모 트랜잭션이 없을경우 예외를 발생시키는 Propagation.MANDATORY로 설정
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	public void refundTickets(List<AgendaProfile> changedProfiles, UUID agendaKey) {
		List<Ticket> tickets = new ArrayList<>();
		for (
			AgendaProfile profile : changedProfiles) {
			Ticket ticket = Ticket.createRefundedTicket(profile, agendaKey);
			tickets.add(ticket);
		}
		ticketRepository.saveAll(tickets);
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
		if (setUpTicket.getModifiedAt().isAfter(LocalDateTime.now().minusMinutes(1))) {
			throw new ForbiddenException(TICKET_FORBIDDEN);
		}
		Auth42Token auth42Token = auth42TokenRedisRepository.findByIntraId(user.getIntraId())
			.orElseThrow(() -> new NotExistException(AUTH_NOT_FOUND));

		String url = pointHistoryUrl.replace("{id}", auth42Token.getIntra42Id());
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + auth42Token.getAccessToken());
		headers.setContentType(MediaType.APPLICATION_JSON);

		List<Map<String, Object>> response = apiUtil.apiCall(url, List.class, headers, HttpMethod.GET);
		if (response == null) {
			throw new NotExistException("POINT_HISTORY_NOT_FOUND");
		}

		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");
		LocalDateTime cutoffTime = setUpTicket.getCreatedAt();

		String targetReason1 = "Provided points to the pool";
		String targetReason2 = "correction points trimming weekly";
		int ticketSum = 0;

		for (Map<String, Object> item : response) {
			ZonedDateTime createdAtUtc = ZonedDateTime.parse((String)item.get("created_at"), formatter)
				.withZoneSameInstant(ZoneId.of("UTC"));
			LocalDateTime createdAtSeoul = createdAtUtc.withZoneSameInstant(seoulZoneId).toLocalDateTime();

			if (createdAtSeoul.isAfter(cutoffTime)) {
				String reason = (String)item.get("reason");
				if (reason.contains(targetReason1) || reason.contains(targetReason2)) {
					int sum = ((Number)item.get("sum")).intValue();
					ticketSum += sum * (-1);
				}
			} else {
				break;
			}
		}

		if (ticketSum == 0) {
			throw new NotExistException("POINT_HISTORY_NOT_FOUND");
		} else if (ticketSum >= 2) {
			Ticket ticket = Ticket.createRefundedTicket(profile, null);
			ticketRepository.save(ticket);
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
