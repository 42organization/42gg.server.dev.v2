package gg.agenda.api.admin.ticket.controller.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TicketAddAdminReqDto {

	private UUID issuedFromKey;

	@Builder
	public TicketAddAdminReqDto(UUID issuedFromKey) {
		this.issuedFromKey = issuedFromKey;
	}
}
