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
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "agenda_announcement")
public class AgendaAnnouncement extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title", nullable = false, columnDefinition = "VARCHAR(50)")
	private String title;

	@Column(name = "content", nullable = false, columnDefinition = "VARCHAR(1000)")
	private String content;

	@Column(name = "is_show", nullable = false, columnDefinition = "BIT(1)")
	private boolean isShow;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "agenda_id")
	private Agenda agenda;

	@Builder
	public AgendaAnnouncement(Long id, String title, String content, boolean isShow, Agenda agenda) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.isShow = isShow;
		this.agenda = agenda;
	}
}
