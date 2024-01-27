package com.gg.server.domain.feedback.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.gg.server.domain.feedback.type.FeedbackType;
import com.gg.server.domain.user.data.User;
import com.gg.server.global.utils.BaseTimeEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Getter
public class Feedback extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "category", length = 15)
	private FeedbackType category;

	@NotNull
	@Column(name = "content", length = 600)
	private String content;

	@Setter
	@NotNull
	@Column(name = "is_solved")
	private Boolean isSolved;

	@Builder
	public Feedback(User user, FeedbackType category, String content) {
		this.user = user;
		this.category = category;
		this.content = content;
		this.isSolved = false;
	}
}
