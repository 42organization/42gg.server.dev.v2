package gg.data.recruit.application;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import gg.data.recruit.recruitment.Question;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@DiscriminatorValue("TEXT")
public class ApplicationAnswerText extends ApplicationAnswer {
	@Column(length = 1000)
	private String answer;

	@Override
	public String getAnswer() {
		return answer;
	}

	@Override
	public ApplicationAnswerEntityDto toForm() {
		return new ApplicationAnswerEntityDto(this.getQuestionId(), this.getInputType(), answer);
	}

	public ApplicationAnswerText(Application application, Question question, String answer) {
		super(application, question);
		this.answer = answer;
	}
}
