package gg.data.agenda;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Builder;

public class AgendaPosterImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;
	@Column(name = "agenda_id", nullable = false)
	private Long agendaId;
	@Column(name = "image_uri", nullable = false, length = 255)
	private String imageUri;
	@Column(name = "is_current", nullable = false)
	private Boolean isCurrent;
	@Column(name = "s3_deleted", nullable = false)
	private Boolean s3Deleted;
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Builder
	public AgendaPosterImage(Long agendaId, String imageUri, Boolean isCurrent, Boolean s3Deleted,
		LocalDateTime createdAt) {
		this.agendaId = agendaId;
		this.imageUri = imageUri;
		this.isCurrent = isCurrent;
		this.s3Deleted = s3Deleted;
		this.createdAt = createdAt;
	}

	public AgendaPosterImage(Long agendaID, String imageUri) {
		this.agendaId = agendaID;
		this.imageUri = imageUri;
		this.isCurrent = true;
		this.s3Deleted = false;
		this.createdAt = LocalDateTime.now();
	}

	public void updateIsCurrentToFalse() {
		this.isCurrent = false;
	}
}
