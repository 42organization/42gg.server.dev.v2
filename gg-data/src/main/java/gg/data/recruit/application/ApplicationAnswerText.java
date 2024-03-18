package gg.data.recruit.application;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@DiscriminatorValue("TEXT")
public class ApplicationAnswerText extends ApplicationAnswer {

	@Id
	private Long id;

	@Column(length = 1000)
	private String answer;

	@Override
	public ApplicationAnswerEntityDto toForm() {
		return new ApplicationAnswerEntityDto(this.getQuestionId(), this.getInputType(), answer);
	}
}
