package gg.agenda.api.user.ticket.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TicketCountResDto {
	private long ticketCount;
	private boolean setupTicket;

	public TicketCountResDto(long ticketCount, boolean setupTicket) {
		this.ticketCount = ticketCount;
		this.setupTicket = setupTicket;
	}
}
