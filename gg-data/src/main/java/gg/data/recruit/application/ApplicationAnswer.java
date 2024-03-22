package gg.data.recruit.application;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import gg.data.BaseTimeEntity;
import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.enums.InputType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "answer_type")
public abstract class ApplicationAnswer extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id", nullable = false)
	private Question question;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id", nullable = false)
	@Getter
	private Application application;

	public InputType getInputType() {
		return question.getInputType();
	}

	public Long getQuestionId() {
		return question.getId();
	}

	public abstract String getAnswer();
	public abstract ApplicationAnswerEntityDto toForm();

	public ApplicationAnswer(Application application, Question question) {
		this.question = question;
		this.application = application;
	}
}
