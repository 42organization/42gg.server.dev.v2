package gg.agenda.api.admin.ticket.controller.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketAddAdminResDto {
	private Long ticketId;

	public TicketAddAdminResDto(Long ticketId) {
		this.ticketId = ticketId;
	}
}
