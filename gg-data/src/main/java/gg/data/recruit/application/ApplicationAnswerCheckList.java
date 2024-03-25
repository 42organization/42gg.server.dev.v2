package gg.data.recruit.application;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import gg.data.recruit.recruitment.CheckList;
import gg.data.recruit.recruitment.Question;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@DiscriminatorValue("CHECK_LIST")
public class ApplicationAnswerCheckList extends ApplicationAnswer {

	@Id
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "check_list_id", nullable = false)
	private CheckList checkList;

	@Override
	public String getAnswer() {
		return checkList.getContent();
	}

	@Override
	public ApplicationAnswerEntityDto toForm() {
		return new ApplicationAnswerEntityDto(this.getQuestionId(), this.getInputType(),
			new CheckListEntityDto(checkList.getId(), checkList.getContent()));
	}

	public ApplicationAnswerCheckList(Application application, Question question,
		CheckList checkList) {
		super(application, question);
		this.checkList = checkList;
	}
}
