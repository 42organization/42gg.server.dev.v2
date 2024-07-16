package gg.data.agenda;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import gg.data.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ticket")
public class Ticket extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id", nullable = false)
	private AgendaProfile agendaProfile;

	@Column(name = "issued_from", nullable = false, columnDefinition = "BINARY(16)")
	private UUID issuedFrom;

	@Column(name = "used_to", nullable = false, columnDefinition = "BINARY(16)")
	private UUID usedTo;

	@Column(name = "is_approve", nullable = false, columnDefinition = "BIT(1)")
	private Boolean isApprove;

	@Column(name = "approved_at", columnDefinition = "DATETIME")
	private LocalDateTime approvedAt;

	@Column(name = "used_at", columnDefinition = "DATETIME")
	private LocalDateTime usedAt;

	@Builder
	public Ticket(AgendaProfile agendaProfile, UUID issuedFrom, UUID usedTo, Boolean isApprove,
		LocalDateTime approvedAt, LocalDateTime usedAt) {
		this.agendaProfile = agendaProfile;
		this.issuedFrom = issuedFrom;
		this.usedTo = usedTo;
		this.isApprove = isApprove;
		this.approvedAt = approvedAt;
		this.usedAt = usedAt;
	}

	public static Ticket refundTicket(AgendaProfile agendaProfile, UUID issuedFrom) {
		return Ticket.builder()
			.agendaProfile(agendaProfile)
			.issuedFrom(issuedFrom)
			.usedTo(null)
			.isApprove(true)
			.approvedAt(LocalDateTime.now())
			.usedAt(null)
			.build();
	}

	public void useTicket(UUID usedTo) {
		this.usedTo = usedTo;
		this.usedAt = LocalDateTime.now();
	}
}
