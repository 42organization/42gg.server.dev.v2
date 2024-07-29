package gg.agenda.api.user.ticket.controller.response;

import java.time.LocalDateTime;
import java.util.UUID;

import gg.data.agenda.Ticket;
import lombok.Getter;

@Getter
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
		this.usedTo = "NotUsed";
		this.usedToKey = ticket.getUsedTo();
		this.isApproved = ticket.getIsApproved();
		this.approvedAt = ticket.getApprovedAt();
		this.isUsed = ticket.getIsUsed();
		this.usedAt = ticket.getUsedAt();
	}

	public void changeIssuedFrom(String issuedFrom) {
		this.issuedFrom = issuedFrom;
	}

	public void changeUsedTo(String usedTo) {
		this.usedTo = usedTo;
	}
}
