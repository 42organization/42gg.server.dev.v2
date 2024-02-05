package com.gg.server.data.store;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.gg.server.data.user.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Megaphone {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receipt_id")
	private Receipt receipt;

	@Column(name = "content", length = 30)
	private String content;

	@NotNull
	@Column(name = "used_at")
	private LocalDate usedAt;

	public Megaphone(User user, Receipt receipt, String content, LocalDate usedAt) {
		this.user = user;
		this.receipt = receipt;
		this.content = content;
		this.usedAt = usedAt;
	}
}
