package gg.pingpong.data.manage;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import gg.pingpong.data.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Announcement extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotNull
	@Column(name = "content", length = 1000)
	private String content;
	@NotNull
	@Column(name = "creator_intra_id", length = 30)
	private String creatorIntraId;
	@Column(name = "deleter_intra_id", length = 30)
	private String deleterIntraId;
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Builder
	public Announcement(String content, String creatorIntraId) {
		this.content = content;
		this.creatorIntraId = creatorIntraId;
	}

	public void update(String deleterIntraId, LocalDateTime deletedAt) {
		this.deleterIntraId = deleterIntraId;
		this.deletedAt = deletedAt;
	}

	public static Announcement from(String content, String creatorIntraId) {
		return Announcement.builder()
			.content(content)
			.creatorIntraId(creatorIntraId)
			.build();
	}
}
