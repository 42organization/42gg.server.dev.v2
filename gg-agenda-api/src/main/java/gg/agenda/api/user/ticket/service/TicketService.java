package gg.agenda.api.user.ticket.service;

import static gg.utils.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import gg.auth.FortyTwoAuthUtil;
import gg.auth.UserDto;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.Ticket;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.agenda.TicketRepository;
import gg.utils.DateTimeUtil;
import gg.utils.exception.custom.DuplicationException;
import gg.utils.exception.custom.NotExistException;
import gg.utils.external.ApiUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {
	private final ApiUtil apiUtil;
	private final FortyTwoAuthUtil fortyTwoAuthUtil;
	private final TicketRepository ticketRepository;
	private final AgendaProfileRepository agendaProfileRepository;

	@Value("https://api.intra.42.fr/v2/users/{id}/correction_point_historics?sort=-id")
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
	 * @param profile AgendaProfile
	 * @return 티켓 수
	 */
	@Transactional(readOnly = true)
	public List<Ticket> findUsedTrueApproveTrueTicketList(AgendaProfile profile) {
		return ticketRepository.findByAgendaProfileAndIsUsedFalseAndIsApprovedTrue(profile);
	}

	@Transactional(readOnly = true)
	public List<Ticket> findUsedFalseTicketList(AgendaProfile profile) {
		return ticketRepository.findByAgendaProfileAndIsUsedFalse(profile);
	}

	/**
	 * 티켓 승인/거절
	 * @param profile 사용자 정보
	 */
	@Transactional
	public void modifyTicketApprove(AgendaProfile profile) {
		Ticket setUpTicket = getSetUpTicket(profile);
		List<Map<String, String>> pointHistory = getPointHistory(profile);
		processTicketApproval(profile, setUpTicket, pointHistory);
	}

	/**
	 * 티켓 setup 조회
	 * @param profile AgendaProfile
	 * @return 티켓 설정
	 */
	public Ticket getSetUpTicket(AgendaProfile profile) {
		return ticketRepository.findByAgendaProfileAndIsApprovedFalse(profile)
			.orElseThrow(() -> new NotExistException(NOT_SETUP_TICKET));
	}

	/**
	 * 포인트 이력 조회
	 * @param profile AgendaProfile
	 * @return 포인트 이력
	 */
	private List<Map<String, String>> getPointHistory(AgendaProfile profile) {
		String url = pointHistoryUrl.replace("{id}", profile.getFortyTwoId().toString());
		ParameterizedTypeReference<List<Map<String, String>>> responseType = new ParameterizedTypeReference<>() {
		};
		try {
			String accessToken = fortyTwoAuthUtil.getAccessToken();
			return apiUtil.callApiWithAccessToken(url, accessToken, responseType);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				String accessToken = fortyTwoAuthUtil.refreshAccessToken();
				return apiUtil.callApiWithAccessToken(url, accessToken, responseType);
			}
			throw e;
		}
	}

	/**
	 * 티켓 승인 처리
	 * @param profile AgendaProfile
	 * @param setUpTicket Ticket
	 * @param pointHistory 포인트 이력
	 */
	private void processTicketApproval(AgendaProfile profile, Ticket setUpTicket,
		List<Map<String, String>> pointHistory) {
		LocalDateTime cutoffTime = setUpTicket.getCreatedAt();

		int ticketSum = pointHistory.stream()
			.takeWhile(
				history -> DateTimeUtil.convertToSeoulDateTime(history.get("created_at")).isAfter(cutoffTime))
			.filter(history -> {
				String reason = history.get("reason");
				return reason.contains(selfDonation) || reason.contains(autoDonation);
			})
			.mapToInt(history -> Integer.parseInt(history.get("sum")) * (-1))
			.sum();

		if (ticketSum == 0) {
			throw new NotExistException(POINT_HISTORY_NOT_FOUND);
		}

		if (ticketSum >= 2) {
			ticketRepository.save(Ticket.createApproveTicket(profile));
		}

		setUpTicket.changeIsApproved();
		ticketRepository.save(setUpTicket);
	}

	@Transactional(readOnly = true)
	public Page<Ticket> findTicketsByUserId(Long userId, Pageable pageable) {
		AgendaProfile profile = agendaProfileRepository.findByUserId(userId)
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));
		return ticketRepository.findByAgendaProfileId(profile.getId(), pageable);
	}
}
