package gg.agenda.api.user.ticket.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TicketCountResDto {
	private int ticketCount;

	public TicketCountResDto(int ticketCount) {
		this.ticketCount = ticketCount;
	}
}
