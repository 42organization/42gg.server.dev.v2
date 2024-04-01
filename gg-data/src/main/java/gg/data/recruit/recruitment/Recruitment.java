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

	@OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL)
	private List<Question> questions = new ArrayList<>();

	@Builder
	public Recruitment(String title, String contents, String generation, LocalDateTime startTime,
		LocalDateTime endTime) {
		mustNotNull(title, NULL_POINT);
		mustNotNull(contents, NULL_POINT);
		mustNotNull(generation, NULL_POINT);
		mustNotNull(startTime, NULL_POINT);
		mustNotNull(endTime, NULL_POINT);

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

	/**
	 * Question에서 호출하는 연관관계 편의 메서드, 기타 호출 금지
	 * @param question
	 */
	protected void addQuestions(Question question) {
		mustNotNull(question, NULL_POINT);
		this.questions.add(question);
	}

	/**
	 * 연관관계 주인까지 전체 수정 메서드
	 * @param updatedRecruitment
	 * @param questions
	 */
	public void update(Recruitment updatedRecruitment, List<Question> questions) {
		mustNotNull(updatedRecruitment, NULL_POINT);
		mustNotNull(questions, NULL_POINT);

		this.title = updatedRecruitment.getTitle();
		this.contents = updatedRecruitment.getContents();
		this.generation = updatedRecruitment.getGeneration();
		this.startTime = updatedRecruitment.getStartTime();
		this.endTime = updatedRecruitment.getEndTime();

		this.questions.clear();        // 연관관계 주인이므로 연관관계 수정은 주인이 해야함?
		this.questions = questions;
	}
}
