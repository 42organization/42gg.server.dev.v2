package gg.data.agenda;

import java.time.LocalDateTime;
import java.util.Objects;
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

	@Column(name = "issued_from", columnDefinition = "BINARY(16)")
	private UUID issuedFrom;

	@Column(name = "used_to", columnDefinition = "BINARY(16)")
	private UUID usedTo;

	@Column(name = "is_approved", nullable = false, columnDefinition = "BIT(1)")
	private Boolean isApproved;

	@Column(name = "approved_at", columnDefinition = "DATETIME")
	private LocalDateTime approvedAt;

	@Column(name = "is_used", nullable = false, columnDefinition = "BIT(1)")
	private Boolean isUsed;

	@Column(name = "used_at", columnDefinition = "DATETIME")
	private LocalDateTime usedAt;

	@Builder
	public Ticket(AgendaProfile agendaProfile, UUID issuedFrom, UUID usedTo, Boolean isApproved,
		LocalDateTime approvedAt, Boolean isUsed, LocalDateTime usedAt) {
		this.agendaProfile = agendaProfile;
		this.issuedFrom = issuedFrom;
		this.usedTo = usedTo;
		this.isApproved = isApproved;
		this.approvedAt = approvedAt;
		this.isUsed = isUsed;
		this.usedAt = usedAt;
	}

	public static void createRefundedTicket(AgendaTeamProfile agendaTeamProfile) {
		Ticket.builder()
			.agendaProfile(agendaTeamProfile.getProfile())
			.issuedFrom(agendaTeamProfile.getAgenda().getAgendaKey())
			.usedTo(null)
			.isApproved(true)
			.approvedAt(LocalDateTime.now())
			.isUsed(false)
			.usedAt(null)
			.build();
	}

	public static Ticket createApproveTicket(AgendaProfile agendaProfile) {
		return Ticket.builder()
			.agendaProfile(agendaProfile)
			.issuedFrom(null)
			.usedTo(null)
			.isApproved(true)
			.approvedAt(LocalDateTime.now())
			.isUsed(false)
			.usedAt(null)
			.build();
	}

	public static Ticket createNotApporveTicket(AgendaProfile agendaProfile) {
		return Ticket.builder()
			.agendaProfile(agendaProfile)
			.issuedFrom(null)
			.usedTo(null)
			.isApproved(false)
			.approvedAt(null)
			.isUsed(false)
			.usedAt(null)
			.build();
	}

	public static Ticket createAdminTicket(AgendaProfile agendaProfile, UUID issuedFromKey) {
		return Ticket.builder()
			.agendaProfile(agendaProfile)
			.issuedFrom(issuedFromKey)
			.usedTo(null)
			.isApproved(true)
			.approvedAt(LocalDateTime.now())
			.isUsed(false)
			.usedAt(null)
			.build();
	}

	public void useTicket(UUID usedTo) {
		this.usedTo = usedTo;
		this.usedAt = LocalDateTime.now();
		this.isUsed = true;
	}

	public void changeIsApproved() {
		this.isApproved = true;
		this.approvedAt = LocalDateTime.now();
	}

	public void updateTicketAdmin(UUID issuedFrom, UUID usedTo, Boolean isApproved, LocalDateTime approvedAt,
		Boolean isUsed, LocalDateTime usedAt) {
		this.issuedFrom = issuedFrom;
		this.usedTo = usedTo;
		this.approvedAt = approvedAt;
		this.usedAt = usedAt;
		if (Objects.nonNull(isUsed)) {
			this.isUsed = isUsed;
		}
		if (Objects.nonNull(isApproved)) {
			this.isApproved = isApproved;
		}
	}
}
