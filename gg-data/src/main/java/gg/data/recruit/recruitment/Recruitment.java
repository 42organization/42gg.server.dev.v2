package gg.data.recruit.recruitment;

import static gg.utils.exception.BusinessChecker.*;
import static gg.utils.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import gg.data.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recruitment extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	@Column(length = 3000)
	private String contents;

	@Column(length = 50)
	private String generation;

	private Boolean isFinish;

	private Boolean isDeleted;

	@Column(nullable = false)
	private LocalDateTime startTime;

	@Column(nullable = false)
	private LocalDateTime endTime;

	@OneToMany(mappedBy = "recruitments", cascade = CascadeType.ALL)
	private List<Question> questions = new ArrayList<>();

	@Builder
	public Recruitment(String title, String contents, String generation, LocalDateTime startTime,
		LocalDateTime endTime) {
		this.title = title;
		this.contents = contents;
		this.generation = generation;
		this.startTime = startTime;
		this.endTime = endTime;
		this.isFinish = false;
		this.isDeleted = false;
	}

	public void del() {
		this.isDeleted = true;
	}

	public Boolean isEnd() {
		return LocalDateTime.now().isAfter(this.endTime);
	}

	public void setFinish(Boolean finish) {
		this.isFinish = finish;
	}

	protected void addQuestions(Question question) {
		mustNotNull(question, NULL_POINT);
		this.questions.add(question);
	}
}
