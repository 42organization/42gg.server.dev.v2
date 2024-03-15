package gg.data.recruit.application;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import gg.data.recruit.recruitment.CheckList;
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
	public ApplicationAnswerEntityDto toForm() {
		return new ApplicationAnswerEntityDto(this.getQuestionId(), this.getInputType(),
			new CheckListEntityDto(checkList.getId(), checkList.getContent()));
	}
}
