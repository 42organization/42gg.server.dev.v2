package com.gg.server.domain.coin.data;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedDate;

import com.gg.server.domain.user.data.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class CoinHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "history", length = 30)
	private String history;

	@Column(name = "amount")
	private int amount;

	@CreatedDate
	@Column(name = "createdAt", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	public CoinHistory(User user, String history, int amount) {
		this.user = user;
		this.history = history;
		this.amount = amount;
		this.createdAt = LocalDateTime.now();
	}
}
