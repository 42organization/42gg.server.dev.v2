package gg.agenda.api.admin.ticket.controller.request;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TicketChangeAdminReqDto {
	private UUID issuedFromKey;
	private UUID usedToKey;
	private Boolean isApproved;
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime approvedAt;
	private Boolean isUsed;
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime usedAt;

	@Builder
	public TicketChangeAdminReqDto(UUID issuedFromKey, UUID usedToKey, Boolean isApproved,
		LocalDateTime approvedAt,
		Boolean isUsed, LocalDateTime usedAt) {
		this.issuedFromKey = issuedFromKey;
		this.usedToKey = usedToKey;
		this.isApproved = isApproved;
		this.approvedAt = approvedAt;
		this.isUsed = isUsed;
		this.usedAt = usedAt;

	}
}
