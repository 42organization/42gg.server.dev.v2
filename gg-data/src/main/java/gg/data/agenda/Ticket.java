package gg.data.agenda;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import gg.data.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Entity
@Table(name = "ticket")
public class Ticket extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id", nullable = false)
	private AgendaProfile agendaProfile;

	@Column(name = "is_used", nullable = false)
	private Boolean isUsed;

	@Column(name = "is_approve", nullable = false)
	private Boolean isApprove;

	public Ticket(AgendaProfile agendaProfile, Boolean isUsed, Boolean isApprove) {
		this.agendaProfile = agendaProfile;
		this.isUsed = isUsed;
		this.isApprove = isApprove;
	}
}

