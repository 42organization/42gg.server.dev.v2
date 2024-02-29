package gg.data.party;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import gg.data.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long commentId;

	@ManyToOne
	@JoinColumn(name = "id") // user_id is stored as id in db
	private User user;

	@ManyToOne
	@JoinColumn(name = "userroom_id")
	private UserRoom userroom;

	@Column
	private LocalDateTime createDate;

	@Column(name = "content", length = 100)
	private String content;

	// Add Getters and setters
}
