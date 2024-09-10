package gg.agenda.api.user.ticket.controller.response;

import java.time.LocalDateTime;
import java.util.UUID;

import gg.data.agenda.Agenda;
import gg.data.agenda.Ticket;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TicketHistoryResDto {
	private LocalDateTime createdAt;
	private String issuedFrom;
	private UUID issuedFromKey;
	private String usedTo;
	private UUID usedToKey;
	private boolean isApproved;
	private LocalDateTime approvedAt;
	private boolean isUsed;
	private LocalDateTime usedAt;

	public TicketHistoryResDto(Ticket ticket) {
		this.createdAt = ticket.getCreatedAt();
		this.issuedFrom = "42Intra";
		this.issuedFromKey = ticket.getIssuedFrom();
		if (ticket.getIsApproved()) {
			this.usedTo = "NotUsed";
		} else {
			this.usedTo = "NotApproved";
		}
		this.usedToKey = ticket.getUsedTo();
		this.isApproved = ticket.getIsApproved();
		this.approvedAt = ticket.getApprovedAt();
		this.isUsed = ticket.getIsUsed();
		this.usedAt = ticket.getUsedAt();
	}

	public void changeIssuedFrom(Agenda agenda) {
		if (agenda == null) {
			this.issuedFrom = "42Intra";
			return;
		}
		this.issuedFrom = agenda.getTitle();
	}

	public void changeUsedTo(Agenda agenda) {
		if (agenda == null && !this.isApproved) {
			this.usedTo = "NotApproved";
			return;
		}
		if (agenda == null) {
			this.usedTo = "NotUsed";
			return;
		}
		this.usedTo = agenda.getTitle();
	}
}
