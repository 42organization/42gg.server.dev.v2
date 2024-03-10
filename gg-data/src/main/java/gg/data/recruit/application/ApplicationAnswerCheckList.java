package gg.data.recruit.application;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import gg.data.BaseTimeEntity;
import gg.data.recruit.recruitment.CheckList;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ApplicationAnswerCheckList extends ApplicationAnswer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "check_list_id", nullable = false)
	private CheckList checkList;

	@Override
	public FormEntityDto toForm() {
		return new FormEntityDto(this.getQuestionId(), this.getInputType(),
			new CheckListEntityDto(checkList.getId(), checkList.getContent()));
	}
}
