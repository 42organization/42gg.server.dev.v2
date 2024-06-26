package gg.data.agenda;

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

	@Column(name = "is_used", nullable = false, columnDefinition = "BIT(1)")
	private Boolean isUsed;

	@Column(name = "is_approve", nullable = false, columnDefinition = "BIT(1)")
	private Boolean isApprove;

	@Builder
	public Ticket(Long id, AgendaProfile agendaProfile, Boolean isUsed, Boolean isApprove) {
		this.id = id;
		this.agendaProfile = agendaProfile;
		this.isUsed = isUsed;
		this.isApprove = isApprove;
	}
}

